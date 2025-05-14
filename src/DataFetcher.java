import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;
import fu.keys.LSIClassCentreDB;

/**
 * Liest aus PostGIS das Polygon des gewünschten Karten-Ausschnitts
 * in WGS84 (Lon/Lat) aus und hält es als JTS-Geometry vor.
 */
public class DataFetcher {
    private static final GeometryFactory geomFact = new GeometryFactory();
    private final Geometry targetSquare;

    /**
     * @param conn       offener JDBC-Connection zu deinem PostGIS
     * @param centerLat  Mittelpunkt-Breitengrad (z.B. 49.445555)
     * @param centerLon  Mittelpunkt-Längengrad (z.B. 11.082587)
     * @param pxWidth    Bildbreite in Pixeln (z.B. 1024)
     * @param pxHeight   Bildhöhe in Pixeln (z.B. 512)
     * @param meterWidth Breite in Metern, die das Bild horizontal abdecken soll (z.B. 1234.5)
     */
    public DataFetcher(Connection conn,
                       double centerLat, double centerLon,
                       int pxWidth, int pxHeight,
                       double meterWidth) throws Exception {
        this.targetSquare = calculateTargetSquare(conn,
                centerLat, centerLon,
                pxWidth, pxHeight,
                meterWidth);
    }

    /**
     * Führt die PostGIS-Abfrage durch, um ein WGS84-Polygon des Rechtecks
     * über Web‑Mercator zu bekommen.
     */
    private static Geometry calculateTargetSquare(Connection conn,
                                                  double lat, double lon,
                                                  int wPx, int hPx,
                                                  double widthMeters) throws Exception {
        // Halbe Ausdehnung in Metern
        double halfW = widthMeters / 2.0;
        // Höhe in Metern
        double heightMeters = widthMeters * hPx / wPx;
        double halfH = heightMeters / 2.0;

        String sql =
                "SELECT ST_AsEWKB( " +
                        "  ST_Transform( " +
                        "    ST_Expand( " +
                        "      ST_Transform( " +
                        "        ST_SetSRID(ST_Point(? , ?), 4326), " +
                        "      3857), " +
                        "      ?, ? " +      // halfWidth, halfHeight in Meter
                        "    ), " +
                        "  4326)" +
                        ")";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // 1 = lon, 2 = lat
            ps.setDouble(1, lon);
            ps.setDouble(2, lat);
            // 3 = halfWidth, 4 = halfHeight
            ps.setDouble(3, halfW);
            ps.setDouble(4, halfH);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalStateException("Keine Geometrie zurückgegeben");
                }
                byte[] wkb = rs.getBytes(1);
                return new WKBReader(geomFact).read(wkb);
            }
        }
    }

    /** Liefert das Polygon, das du in deinen WHERE‑Klauseln benutzen kannst. */
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

    public List<DomainFeature> getFeaturesByLsiClass(Connection conn, int lsiLower, int lsiUpper, String geometryType, boolean excludeHistoric) throws Exception {
        List<DomainFeature> features = new ArrayList<>();
        System.out.println("Fetching from " + lsiLower + " to " + lsiUpper);

        StringBuilder sql = new StringBuilder("""
            SELECT realname, lsiclass1, ST_AsEWKB(geom :: geometry), geometry
            FROM domain
            WHERE ST_Within(geom :: geometry, ST_GeomFromWKB(?, 4326))
              AND (lsiclass1 BETWEEN ? AND ? or lsiclass2 BETWEEN ? AND ? or lsiclass3 BETWEEN ? AND ?)
            ORDER BY ST_Area(geom :: geometry) DESC
        """);

        if (geometryType != null) {
            sql.append(" AND geometry = ?");
        }

        if (excludeHistoric){
            System.out.println("i dont like old things");
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
                    String realname = rs.getString("realname");
                    int lsiclass = rs.getInt("lsiclass1");
                    Geometry geom = reader.read(rs.getBytes(3));
                    String geometryDataType = rs.getString("geometry");
                    features.add(new DomainFeature(realname, lsiclass, geom, geometryDataType));
                }
            }
        }

        return features;
    }

    public void printDistinctLSIClassesWithDescription(Connection conn) throws Exception {
        String sql =
                "SELECT DISTINCT ON (d.lsiclass1) " +
                        "    d.lsiclass1, d.realname, l.description, l.token " +
                        "FROM domain d " +
                        "JOIN lsiclasses l ON d.lsiclass1 = l.id " +
                        "WHERE ST_Within(d.geom::geometry, ST_GeomFromWKB(?, 4326))" +
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