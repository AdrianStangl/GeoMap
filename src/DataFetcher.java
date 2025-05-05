import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKBReader;

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
        this.targetSquare = fetchTargetSquare(conn,
                centerLat, centerLon,
                pxWidth, pxHeight,
                meterWidth);
    }

    /**
     * Führt die PostGIS-Abfrage durch, um ein WGS84-Polygon des Rechtecks
     * über Web‑Mercator zu bekommen.
     */
    private static Geometry fetchTargetSquare(Connection conn,
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
}