import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;

/**
 * Rendert alle Geometrien in ein Bitmap (PNG)
 */
public class MapRenderer {
    private final BufferedImage image;
    private final Graphics2D g;
    private final Geometry target;
    private final Envelope env;
    private final int width;
    private final int height;
    private final double scaleX;
    private final double scaleY;

    public MapRenderer(Connection conn, Geometry targetSquare,
                       int pxWidth, int pxHeight) throws Exception {
        this.target = targetSquare;
        this.env = targetSquare.getEnvelopeInternal();
        this.width = pxWidth;
        this.height = pxHeight;
        this.scaleX = width  / (env.getMaxX() - env.getMinX());
        this.scaleY = height / (env.getMaxY() - env.getMinY());

        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.g = image.createGraphics();
        g.setRenderingHint(
                java.awt.RenderingHints.KEY_ANTIALIASING,
                java.awt.RenderingHints.VALUE_ANTIALIAS_ON
        );
    }

    private int toPixelX(double lon) {
        return (int) Math.round((lon - env.getMinX()) * scaleX);
    }
    private int toPixelY(double lat) {
        return height - (int) Math.round((lat - env.getMinY()) * scaleY);
    }

    public void drawMap(Connection connection, DataFetcher fetcher) throws Exception {
        drawAreas(connection);
        drawLines(connection);
        drawWater(connection, fetcher);
        drawWater(connection, fetcher);
        drawVegetation(connection, fetcher);
        drawVegetation(connection, fetcher);
        drawCommercial(connection, fetcher);
        drawResidential(connection, fetcher);
        drawStreets(connection, fetcher);
        //markStuff(fetcher.getFeaturesByLsiClass(connection, "UNDEF"));
        // renderer.drawPoints(connection);
    }

    public void drawPolygon(Geometry geom, Color fillColor, Color borderColor) {
        Path2D path = new Path2D.Double();
        boolean first = true;
        for (Coordinate c : geom.getCoordinates()) {
            int x = toPixelX(c.x);
            int y = toPixelY(c.y);
            if (first) {
                path.moveTo(x, y);
                first = false;
            } else {
                path.lineTo(x, y);
            }
        }
        path.closePath();
        g.setColor(fillColor);
        g.fill(path);
        g.setColor(borderColor);
        g.draw(path);
    }

    public void drawLineGeometry(Geometry geom, Color borderColor) {
        Path2D path = new Path2D.Double();
        boolean first = true;
        for (Coordinate c : geom.getCoordinates()) {
            int x = toPixelX(c.x), y = toPixelY(c.y);
            if (first) { path.moveTo(x,y); first = false; }
            else      path.lineTo(x,y);
        }
        // g.setStroke(new BasicStroke(3.0f));
        g.setColor(borderColor);
        g.draw(path);
    }

    public void drawWater(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> waterGeoms = fetcher.getFeaturesByLsiClass(connection, "WATER", null, false);
        List<DomainFeature> otherWater = fetcher.getFeaturesByLsiClass(connection, "WASSER_LAND_FORMATION", null, false);
        waterGeoms.addAll(otherWater);

        Color fillColor = new Color(100, 149, 237, 180);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(30, 30, 150, 200);  // Darker blue

        for (DomainFeature feature : waterGeoms) {
            if(feature.geometry() instanceof Polygon)
                drawPolygon(feature.geometry(), fillColor, borderColor);
            else if(feature.geometry() instanceof MultiPolygon)
                for (int i = 0; i < feature.geometry().getNumGeometries(); i++) {
                    drawPolygon(feature.geometry().getGeometryN(i), fillColor, borderColor);
                }
            else
                // drawLineGeometry(feature.geometry(), borderColor);
                drawPolygon(feature.geometry().buffer(0.00009), fillColor, fillColor);
                //continue;
        }
    }

    public void drawVegetation(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> vegetationGeoms = fetcher.getFeaturesByLsiClass(connection, "VEGETATION", null, false);
        List<DomainFeature> otherVegetation = fetcher.getFeaturesByLsiClass(connection, "PARK", null, false);
        vegetationGeoms.addAll(otherVegetation);

        Color fillColor = new Color(42, 195, 20, 236);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(39, 181, 21, 236);  // Darker blue

        for (DomainFeature feature : vegetationGeoms) {
            if(feature.geometryType().equals("A"))
                drawPolygon(feature.geometry(), fillColor, borderColor);
            else
                // drawLineGeometry(feature.geometry(), borderColor);
                drawPolygon(feature.geometry().buffer(0.0001), fillColor, fillColor);
        }
    }

    public void drawCommercial(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> commercialGeoms = fetcher.getFeaturesByLsiClass(connection, "COMMERCIAL", null, false);
        Color fillColor = new Color(223, 9, 74, 213);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(172, 12, 50, 236);  // Darker blue

        for (DomainFeature feature : commercialGeoms) {
            if(feature.geometryType().equals("A"))
                drawPolygon(feature.geometry(), fillColor, borderColor);
            else
                // drawLineGeometry(feature.geometry(), borderColor);
                drawPolygon(feature.geometry().buffer(0.0001), fillColor, fillColor);
        }
    }

    public void drawResidential(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> residentialGeoms = fetcher.getFeaturesByLsiClass(connection, "INHABITED", null, false);
        Color fillColor = new Color(149, 6, 49, 213);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(94, 3, 23, 236);  // Darker blue

        for (DomainFeature feature : residentialGeoms) {
            if(feature.geometryType().equals("A"))
                drawPolygon(feature.geometry(), fillColor, borderColor);
            else
                // drawLineGeometry(feature.geometry(), borderColor);
                drawPolygon(feature.geometry(), fillColor, fillColor);
        }
    }

    public void markStuff(List<DomainFeature> stuff) throws Exception {
        Color fillColor = new Color(229, 26, 236, 255);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(255, 0, 234, 255);  // Darker blue

        for (DomainFeature feature : stuff) {
            if(feature.geometry() instanceof Polygon)
                drawPolygon(feature.geometry(), fillColor, borderColor);
            else if(feature.geometry() instanceof MultiPolygon)
                for (int i = 0; i < feature.geometry().getNumGeometries(); i++) {
                    drawPolygon(feature.geometry().getGeometryN(i), fillColor, borderColor);
                }
            else
                // drawLineGeometry(feature.geometry(), borderColor);
                drawPolygon(feature.geometry().buffer(0.00005), fillColor, fillColor);
            //continue;
        }
    }

    public void drawStreets(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> streetGeoms = fetcher.getFeaturesByLsiClass(connection, "STRASSEN_WEGE");
        Color fillColor = new Color(151, 94, 108, 213);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(142, 121, 126, 236);  // Darker blue

        for (DomainFeature feature : streetGeoms) {
            if(feature.geometryType().equals("A"))
                drawPolygon(feature.geometry(), fillColor, borderColor);
            else
                drawLineGeometry(feature.geometry(), borderColor);
                // drawPolygon(feature.geometry(), fillColor, fillColor);
        }
    }

    /**
     * Zeichnet Flächenobjekte (geometry='A')
     */
    public void drawAreas(Connection conn) throws Exception {
        String sql =
                "SELECT lsiclass1, ST_AsEWKB(geom :: geometry) " +
                        "FROM domain " +
                        "WHERE geometry='A' AND ST_Within(geom :: geometry, ST_GeomFromWKB(?,4326))";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBytes(1, new WKBWriter().write(target));
            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                WKBReader reader = new WKBReader();
                while (rs.next()) {
                    count++;
                    int lsiClass = rs.getInt("lsiclass1");
                    byte[] wkb = rs.getBytes(2);
                    Geometry geom = reader.read(wkb);

                    Color fill = fillColorFor(lsiClass);
                    Color border = borderColorFor(lsiClass);

                    drawPolygon(geom, fill, border);
                }
                System.out.println("There are " + count + " polygons");
            }
        }
    }


    /**
     * Zeichnet Linienobjekte (geometry='L')
     */
    public void drawLines(Connection conn) throws Exception {
        String sql =
                "SELECT ST_AsEWKB(geom :: geometry), realname " +
                        "FROM domain " +
                        "WHERE geometry='L' AND ST_Within(geom :: geometry, ST_GeomFromWKB(?,4326))";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBytes(1, new WKBWriter().write(target));
            try (ResultSet rs = ps.executeQuery()) {
                WKBReader reader = new WKBReader();
                int count = 0;
                while (rs.next()) {
                    count++;
                    byte[] wkb = rs.getBytes(1);
                    String name = rs.getString(2);
                    Geometry geom = reader.read(wkb);
                    Path2D path = new Path2D.Double();
                    boolean first = true;
                    for (Coordinate c : geom.getCoordinates()) {
                        int x = toPixelX(c.x), y = toPixelY(c.y);
                        if (first) { path.moveTo(x,y); first = false; }
                        else      path.lineTo(x,y);
                    }
                    g.setColor(borderColorFor(name));
                    g.draw(path);
                }
                System.out.println("there are " + count + " Lines");
            }
        }
    }

    /**
     * Zeichnet Punktobjekte (geometry='P')
     */
    public void drawPoints(Connection conn) throws Exception {
        String sql =
                "SELECT ST_AsEWKB(geom :: geometry), realname " +
                        "FROM domain " +
                        "WHERE geometry='P' AND ST_Within(geom :: geometry, ST_GeomFromWKB(?,4326))";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBytes(1, new WKBWriter().write(target));
            try (ResultSet rs = ps.executeQuery()) {
                WKBReader reader = new WKBReader();
                while (rs.next()) {
                    byte[] wkb = rs.getBytes(1);
                    String name = rs.getString(2);
                    Geometry geom = reader.read(wkb);
                    Coordinate c = geom.getCoordinate();
                    int x = toPixelX(c.x), y = toPixelY(c.y);
                    // Icon-Rendering oder einfacher Punkt
                    g.setColor(Color.RED);
                    g.fillOval(x-3, y-3, 6, 6);
                    g.drawString(name, x+5, y-5);
                }
            }
        }
    }

    private Color fillColorFor(int lsiClass) {
        return new Color(200, 200, 200, 180);
    }
    private Color borderColorFor(int lsiClass) {
        return Color.DARK_GRAY;
    }

    private Color fillColorFor(String lsiClassName) {
        return new Color(200, 200, 200, 180);
    }
    private Color borderColorFor(String lsiClassName) {
        return Color.DARK_GRAY;
    }

    /**
     * Speichert das gerenderte Bild als PNG
     */
    public void saveImage(String filename) throws Exception {
        ImageIO.write(image, "PNG", new File(filename));
    }
}