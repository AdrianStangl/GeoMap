import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Coordinate;
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
        drawWater(fetcher.getWater(connection));
        // renderer.drawPoints(connection);
    }

    public void drawPolygon(ResultSet rs, Color fillColor, Color borderColor) throws Exception{
        WKBReader reader = new WKBReader();

        String name = rs.getString(1);
        byte[] wkb = rs.getBytes(2);
        Geometry geom = reader.read(wkb);
        Path2D path = new Path2D.Double();
        boolean first = true;
        for (Coordinate c : geom.getCoordinates()) {
            int x = toPixelX(c.x);
            int y = toPixelY(c.y);
            if (first) { path.moveTo(x, y); first = false; }
            else      path.lineTo(x, y);
        }
        path.closePath();
        g.setColor(fillColor);
        g.fill(path);
        g.setColor(borderColor);
        g.draw(path);
    }
    /**
     * Zeichnet Flächenobjekte (geometry='A')
     */

    public void drawWater(ResultSet rs) throws Exception{
        drawPolygon(rs, Color.BLUE, Color.white);
    }

    public void drawAreas(Connection conn) throws Exception {
        String sql =
                "SELECT realname, ST_AsEWKB(geom :: geometry) " +
                        "FROM domain " +
                        "WHERE geometry='A' AND ST_Within(geom :: geometry, ST_GeomFromWKB(?,4326))";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBytes(1, new WKBWriter().write(target));
            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    drawPolygon(rs, fillColorFor("here goes the lsiClass"), borderColorFor("lsiclass as well") );
                }
                System.out.println("there are " + count + " Polygons");
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

    private Color fillColorFor(String name) {
        return new Color(200, 200, 200, 180);
    }
    private Color borderColorFor(String name) {
        return Color.DARK_GRAY;
    }

    /**
     * Speichert das gerenderte Bild als PNG
     */
    public void saveImage(String filename) throws Exception {
        ImageIO.write(image, "PNG", new File(filename));
    }
}