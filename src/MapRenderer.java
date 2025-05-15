import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;
import fu.keys.LSIClassCentreDB;

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
        drawBackground();
        drawAreas(connection);
        // drawLines(connection);
        drawWater(connection, fetcher);
        drawVegetation(connection, fetcher);
        drawCommercial(connection, fetcher);
        drawResidential(connection, fetcher);
        drawOpenarea(connection, fetcher);
        drawGeology(connection, fetcher);
        drawOthers(connection, fetcher);
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

    public void drawBackground() {
        Color backgroundColor = new Color(66, 76, 71, 181);  // Cornflower Blue, semi-transparent

        drawPolygon(target, backgroundColor, backgroundColor);

    }

    public void drawWater(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> waterGeoms = fetcher.getFeaturesByLsiClass(connection, "WATER", null, false);
        List<DomainFeature> otherWater = fetcher.getFeaturesByLsiClass(connection, "WASSER_LAND_FORMATION", null, false);
        waterGeoms.addAll(otherWater);

        Color fillColor = new Color(23, 92, 223, 255);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(30, 30, 150, 255);  // Darker blue

        for (DomainFeature feature : waterGeoms) {
            if(!feature.tags().contains("tunnel"))
                drawDomainFeature(feature, fillColor, borderColor, 0.0001);
        }
    }

    public void drawVegetation(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> vegetationGeoms = fetcher.getFeaturesByLsiClass(connection, "VEGETATION", null, false);
        List<DomainFeature> otherVegetation = fetcher.getFeaturesByLsiClass(connection, "PARK", null, false);
        vegetationGeoms.addAll(otherVegetation);

        Color fillColor = new Color(42, 195, 20, 255);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(39, 181, 21, 255);  // Darker blue

        for (DomainFeature feature : vegetationGeoms) {
            drawDomainFeature(feature, fillColor, borderColor,0);
        }
    }

    public void drawCommercial(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> commercialGeoms = fetcher.getFeaturesByLsiClass(connection, "COMMERCIAL", null, false);
        Color fillColor = new Color(207, 73, 114, 180);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(172, 12, 50, 236);  // Darker blue

        int count = 0;
        for (DomainFeature feature : commercialGeoms) {
            if(count < 10){
                count++;
                System.out.println("Drawing the commercial building: " + feature.realname() + " with size: " + feature.area());

            }
            drawDomainFeature(feature, fillColor, borderColor, 0);
        }
    }

    public void drawResidential(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> residentialGeoms = fetcher.getFeaturesByLsiClass(connection, "INHABITED", null, false);
        Color fillColor = new Color(149, 6, 49, 180);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(94, 3, 23, 236);  // Darker blue

        int count = 0;
        for (DomainFeature feature : residentialGeoms) {
            if(count < 10){
                count++;
                System.out.println("Drawing the building: " + feature.realname() + " with size: " + feature.area());

            }
            drawDomainFeature(feature, fillColor, borderColor, 0);
        }
    }

    public void drawOpenarea(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> openareaGeoms = fetcher.getFeaturesByLsiClass(connection, "OPENAREA", null, false);
        Color fillColor = new Color(239, 221, 18, 255);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(236, 220, 50, 236);  // Darker blue

        // Extract and draw streets
        int[] strassenLSIBoundaries = LSIClassCentreDB.lsiClassRange("STRASSEN_WEGE");
        List<DomainFeature> strassenGeos = extractLSISubSet(openareaGeoms, strassenLSIBoundaries[0], strassenLSIBoundaries[1]);
        drawStreets(strassenGeos);

        Color TrackFillColor = new Color(43, 37, 37, 255);
        int[] tracksLsiBoundaries = LSIClassCentreDB.lsiClassRange("GLEISKOERPER");
        List<DomainFeature> tracks = extractLSISubSet(openareaGeoms, tracksLsiBoundaries[0], tracksLsiBoundaries[1]);
        for (DomainFeature feature : tracks) {
            drawDomainFeature(feature, TrackFillColor, TrackFillColor, 0.00001);
        }

        Color tramFillColor = new Color(21, 20, 20, 255);
        int[] tramLsiBoundaries = LSIClassCentreDB.lsiClassRange("TRAM_GLEISE");
        List<DomainFeature> tramGeos = extractLSISubSet(tracks, tramLsiBoundaries[0], tramLsiBoundaries[1]);
        for (DomainFeature feature : tramGeos) {
            drawDomainFeature(feature, tramFillColor, tramFillColor, 0.00001);
        }

        // Extract and draw streets
        // TODO Brücken nicht entfernen -> Upper bound anpassen
        int[] trashLSIBoundaries = LSIClassCentreDB.lsiClassRange("TRAFFIC_MORE");
        List<DomainFeature> trash = extractLSISubSet(openareaGeoms, trashLSIBoundaries[0], trashLSIBoundaries[1]);

        // draw the remaining open areas
        for (DomainFeature feature : openareaGeoms) {
            drawDomainFeature(feature, fillColor, borderColor, 0);
        }
    }
    public void drawGeology(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> openareaGeoms = fetcher.getFeaturesByLsiClass(connection, "GEOLOGY", null, false);
        Color fillColor = new Color(95, 103, 112, 255);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(92, 91, 77, 236);  // Darker blue

        for (DomainFeature feature : openareaGeoms) {
            drawDomainFeature(feature, fillColor, borderColor, 0);
        }
    }

    public void drawOthers(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> openareaGeoms = fetcher.getFeaturesByLsiClass(connection, "OTHER_OBJECTS", null, false);
        Color fillColor = new Color(174, 88, 211, 255);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(200, 95, 239, 236);  // Darker blue

        for (DomainFeature feature : openareaGeoms) {
            drawDomainFeature(feature, fillColor, borderColor, 0);
        }
    }

    public void markStuff(List<DomainFeature> stuff) throws Exception {
        Color fillColor = new Color(229, 26, 236, 255);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(255, 0, 234, 255);  // Darker blue

        for (DomainFeature feature : stuff) {
            drawDomainFeature(feature, fillColor, borderColor, 0.0005);
        }
    }

    public void drawStreets(List<DomainFeature> streetGeoms) {
        Color fillColor = new Color(97, 91, 91, 255);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(43, 40, 40, 236);  // Darker blue

        Color fillColorInnerStreets = new Color(97, 91, 91, 255);
        Color borderColorInnerStreets = new Color(97, 91, 91, 255);

        int[] streetSubBoundaries = LSIClassCentreDB.lsiClassRange("INNERORTSTRASSE_ALL");
        List<DomainFeature> innerCityStreets = extractLSISubSet(streetGeoms, streetSubBoundaries[0], streetSubBoundaries[1]);

        drawStreetsFromDomainFeatures(innerCityStreets, fillColorInnerStreets, borderColorInnerStreets, 0.00004, 0.000026 );

        // draw remaning street geos
        drawStreetsFromDomainFeatures(streetGeoms, fillColor, borderColor, 0.00005, 0.000037);
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

    private void drawDomainFeature(DomainFeature feature, Color fillColor, Color borderColor, double buffer){
        if(feature.geometry() instanceof Polygon)
            drawPolygon(feature.geometry(), fillColor, borderColor);
        else if(feature.geometry() instanceof MultiPolygon)
            for (int i = 0; i < feature.geometry().getNumGeometries(); i++) {
                drawPolygon(feature.geometry().getGeometryN(i), fillColor, borderColor);
            }
        else if (feature.geometry() instanceof LineString)
            if (buffer != 0)
                drawPolygon(feature.geometry().buffer(buffer), fillColor, fillColor);
            else
                drawLineGeometry(feature.geometry(), borderColor);
        else if (feature.geometry() instanceof Point)
            return;
        else if (feature.geometry() instanceof MultiPoint)
            return;
        else
            System.out.println("Instance of " + feature.geometry().getClass().getName() + " is not supported");
    }

    /// Draws a thick line of border color and a thinner liner of fillColor
    private void drawStreetsFromDomainFeatures(List<DomainFeature> features, Color fillColor, Color borderColor, double outerBuffer, double innerBuffer) {
        for (DomainFeature feature : features) {
            drawDomainFeature(feature, borderColor, borderColor, outerBuffer);
        }
        for (DomainFeature feature : features) {
            drawDomainFeature(feature, fillColor, fillColor, innerBuffer);
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

    /// Returns subset of features list where the features are between lowerBound and upperBound, including the borders
    private List<DomainFeature> extractLSISubSet(List<DomainFeature> features, int lowerBound, int upperBound) {
        List<DomainFeature> subset = new ArrayList<DomainFeature>();
        for (int i = 0; i < features.size(); i++) {
            DomainFeature feature = features.get(i);
            if (feature.lsiclass1() >= lowerBound && feature.lsiclass1() <= upperBound){
                subset.add(features.remove(i--));
            }
        }
        return subset;
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