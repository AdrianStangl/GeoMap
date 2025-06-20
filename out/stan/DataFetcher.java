package stan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;
import fu.keys.LSIClassCentreDB;


public class DataFetcher {
    private static final GeometryFactory geomFact = new GeometryFactory();
    private final Geometry targetSquare;

    public DataFetcher(double centerLat, double centerLon,
                       int pxWidth, int pxHeight,
                       double meterWidth) {
        this.targetSquare = calculateTargetSquare(centerLat, centerLon,
                pxWidth, pxHeight,
                meterWidth);
    }

    /// Calculates the square area with center point of the lat, lon coordinates and a width of widthMeters
    /// Only accurate for the region of nuernberg
    private static Geometry calculateTargetSquare(double lat, double lon,
                                                  int wPx, int hPx,
                                                  double widthMeters) {

        // Approximate scale factors für Nuernberg:
        // 1 Grad Länge ~ 72.300 m
        // 1 Grad Breite ~ 111.320 m
        final double metersPerDegLon = 72300;
        final double metersPerDegLat = 111320;

        // Halbe Breite und Höhe in Grad berechnen
        double halfWidthDeg = (widthMeters / 2.0) / metersPerDegLon;
        double heightMeters = widthMeters * ((double) hPx / wPx);
        double halfHeightDeg = (heightMeters / 2.0) / metersPerDegLat;

        double minLon = lon - halfWidthDeg;
        double maxLon = lon + halfWidthDeg;
        double minLat = lat - halfHeightDeg;
        double maxLat = lat + halfHeightDeg;

        Coordinate[] coords = new Coordinate[] {
                new Coordinate(minLon, minLat),
                new Coordinate(minLon, maxLat),
                new Coordinate(maxLon, maxLat),
                new Coordinate(maxLon, minLat),
                new Coordinate(minLon, minLat)
        };

        return geomFact.createPolygon(coords);
    }

    public Geometry getTargetSquare() {
        return targetSquare;
    }

    public List<DomainFeature> getFeaturesByLsiClass(Connection conn, String lsiClassGroup) throws Exception {
        return getFeaturesByLsiClass(conn, lsiClassGroup, null);
    }
    public List<DomainFeature> getFeaturesByLsiClass(Connection conn, int lsiLower, int lsiUpper) throws Exception {
        return getFeaturesByLsiClass(conn, lsiLower, lsiUpper, null);
    }

    public List<DomainFeature> getFeaturesByLsiClass(Connection conn, String lsiClassGroup, String geometryType) throws Exception {
        return getFeaturesByLsiClass(conn, lsiClassGroup, geometryType, false);
    }

    public List<DomainFeature> getFeaturesByLsiClass(Connection conn, String lsiClassGroup, String geometryType, boolean excludeHistoric) throws Exception {
        int[] lsiRange = LSIClassCentreDB.lsiClassRange(lsiClassGroup);

        return getFeaturesByLsiClass(conn, lsiRange[0], lsiRange[1], geometryType, excludeHistoric);
    }

    public List<DomainFeature> getFeaturesByLsiClass(Connection conn, int lsiLower, int lsiUpper, String geometryType) throws Exception{
        return getFeaturesByLsiClass(conn, lsiLower, lsiUpper, geometryType, false);
    }

    ///  Returns a list of domainfeatures that are within the target square and between the lsi boundaries
    /// Optionally historic entries can be ignored with a flag
    public List<DomainFeature> getFeaturesByLsiClass(Connection conn, int lsiLower, int lsiUpper, String geometryType, boolean excludeHistoric) throws Exception {
        List<DomainFeature> features = new ArrayList<>();
        System.out.println("Fetching from " + lsiLower + " to " + lsiUpper);

        StringBuilder sql = new StringBuilder("""
            SELECT d_id AS id, realname, lsiclass1, lsiclass2, lsiclass3, ST_AsEWKB(geom :: geometry) AS geom, geometry, ST_Area(geom :: geometry) AS area, tags
            FROM domain
            WHERE ST_Intersects(geom :: geometry, ST_GeomFromWKB(?, 4326))
              AND (lsiclass1 BETWEEN ? AND ? or lsiclass2 BETWEEN ? AND ? or lsiclass3 BETWEEN ? AND ?)
            ORDER BY ST_Area(geom :: geometry) DESC
        """);

        if (geometryType != null) {
            sql.append(" AND geometry = ?");
        }

        if (excludeHistoric){
            sql.append(" AND lsiclass2 not BETWEEN ? AND ?");
        }

        int parameterIndex = 1;
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setBytes(parameterIndex++, new WKBWriter().write(targetSquare));
            ps.setInt(parameterIndex++, lsiLower);
            ps.setInt(parameterIndex++, lsiUpper);
            ps.setInt(parameterIndex++, lsiLower);
            ps.setInt(parameterIndex++, lsiUpper);
            ps.setInt(parameterIndex++, lsiLower);
            ps.setInt(parameterIndex++, lsiUpper);
            if (geometryType != null) {
                ps.setString(parameterIndex++, geometryType);
            }
            if (excludeHistoric) {
                int[] historicLsiRange = LSIClassCentreDB.lsiClassRange("HISTORIC");
                ps.setInt(parameterIndex++, historicLsiRange[0]);
                ps.setInt(parameterIndex++, historicLsiRange[1]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                WKBReader reader = new WKBReader();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String realname = rs.getString("realname");
                    int lsiclass1 = rs.getInt("lsiclass1");
                    int lsiclass2 = rs.getInt("lsiclass2");
                    int lsiclass3 = rs.getInt("lsiclass3");
                    Geometry geom = reader.read(rs.getBytes("geom"));
                    String geometryDataType = rs.getString("geometry");
                    double area = rs.getDouble("area");
                    String tags = rs.getString("tags");
                    features.add(new DomainFeature(id, realname, lsiclass1, lsiclass2, lsiclass3, geom, geometryDataType, area, tags));
                }
            }
        }

        return features;
    }

    // Helper method to get all distinct LSI classes within a geometry
    public void printDistinctLSIClassesWithDescription(Connection conn) throws Exception {
        String sql =
                "SELECT DISTINCT ON (d.lsiclass1) " +
                        "    d.lsiclass1, d.realname, l.description, l.token " +
                        "FROM domain d " +
                        "JOIN lsiclasses l ON d.lsiclass1 = l.id " +
                        "WHERE ST_Intersects(d.geom::geometry, ST_GeomFromWKB(?, 4326))" +
                        "ORDER BY d.lsiclass1 ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBytes(1, new WKBWriter().write(targetSquare));
            try (ResultSet rs = ps.executeQuery()) {
                List<String> classes = new ArrayList<>();
                System.out.println("Distinct LSI classes in area:");
                while (rs.next()) {
                    String lsiClass = rs.getString("lsiclass1");
                    String realname = rs.getString("realname");
                    String description = rs.getString("description");
                    String token = rs.getString("token");
                    classes.add(lsiClass);
                    System.out.printf("- %s (%s): %s -> %s %n", lsiClass, realname, description, token);
                }
                System.out.println("Total distinct LSI classes: " + classes.size());
            }
        }
    }
}