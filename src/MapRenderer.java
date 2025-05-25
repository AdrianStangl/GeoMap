import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
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

    private List<DrawableFeature> drawableFeatures;

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

        drawableFeatures = new ArrayList<>();
    }

    private int toPixelX(double lon) {
        return (int) Math.round((lon - env.getMinX()) * scaleX);
    }
    private int toPixelY(double lat) {
        return height - (int) Math.round((lat - env.getMinY()) * scaleY);
    }

    public void drawMap(Connection connection, DataFetcher fetcher) throws Exception {
        drawBackground();
        // drawAreas(connection);
        // drawLines(connection);
        drawWater(connection, fetcher);
        drawGeology(connection, fetcher);
        drawVegetation(connection, fetcher);
        drawResidential(connection, fetcher);
        drawCommercial(connection, fetcher);
        drawOpenarea(connection, fetcher);
        drawOthers(connection, fetcher);
        //markStuff(fetcher.getFeaturesByLsiClass(connection, "UNDEF"));
        // renderer.drawPoints(connection);

        drawableFeatures.sort(Comparator.comparingDouble((DrawableFeature df) -> df.feature().area()).reversed());

        for (DrawableFeature drawFeature : drawableFeatures) {
            drawDomainFeature(drawFeature.feature(), drawFeature.fillColor(), drawFeature.borderColor(), drawFeature.buffer());
        }
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
        //waterGeoms.addAll(otherWater);  // TODO check waterland class

        Color fillColor = new Color(39, 134, 227, 242);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(30, 30, 150, 255);  // Darker blue

        for (DomainFeature feature : waterGeoms) {
            if(!feature.tags().contains("tunnel"))
                addDomainFeatureToGlobalList(feature, fillColor, borderColor, 0.0001);
        }
    }

    public void drawVegetation(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> vegetationGeoms = fetcher.getFeaturesByLsiClass(connection, "VEGETATION", null, false);
        List<DomainFeature> otherVegetation = fetcher.getFeaturesByLsiClass(connection, "PARK", null, false);
        vegetationGeoms.addAll(otherVegetation);

        Color fillColor = new Color(42, 195, 20, 255);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(39, 181, 21, 255);  // Darker blue

        for (DomainFeature feature : vegetationGeoms) {
            addDomainFeatureToGlobalList(feature, fillColor, borderColor,0);
        }
    }

    public void drawCommercial(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> commercialGeoms = fetcher.getFeaturesByLsiClass(connection, "COMMERCIAL", null, false);
        Color fillColor = new Color(207, 73, 114, 180);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(159, 11, 46, 236);  // Darker blue

        for (DomainFeature feature : commercialGeoms) {
            addDomainFeatureToGlobalList(feature, fillColor, borderColor, 0);
        }
    }

    public void drawResidential(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> residentialGeoms = fetcher.getFeaturesByLsiClass(connection, "INHABITED", null, false);
        Color fillColor = new Color(149, 6, 49, 180);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(87, 2, 21, 236);  // Darker blue

        String[] residentialLSIClasses = {
                // Residential
                "GEWAECHSHAUS", "UNTERSTAND", "HUT",
                "SHED", "BARN_BUILDING", "FARM_BUILDING",
                "SCHOOL_BUILDING", "COMMERCIAL_BUILDING", "INDUSTRIAL_BUILDING",
                "DETACHED", "TERRACE", "APPARTEMENTS",
                "GARAGES", "GARAGE", "CARPORT",
                "HOUSE", "RESIDENTIAL_BUILDING", "BUILDING",
                "RESIDENTIAL",

                // Academic
                "ASTRONOMIE", "RESEARCH", "LIBRARY",
                "BESONDERE_SCHULE", "BERUFSSCHULE", "GRUNDSCHULE",
                "HAUPTSCHULE", "REALSCHULE", "GYMNASIUM",
                "UNIVERSITY", "ACADEMIC",

                "MELDUNGSEINRICHTUNGEN",
                "ROHRLEITUNG",

                // Wasseraufbereitung
                "KLAERWERK", "WASSERTURM", "WASSERHOCHBEHAELTER",
                "STAUDAMM", "RESERVOIR", "BASIN",
                "BRUNNEN", "WASSERWERK", "WASSERAUFBEREITUNG",

                // Power plant addon
                "TRANSFORMATOR", "TRAFOHAUS", "STROMVERTEILER",
                "STROMLEITUNG", "STROMMAST", "WASSERRAD",
                "UMSPANNSTATION", "POWER_PLANT_ADDON",
                // Power plants
                "MUEHLE", "WASSER_KRAFTWERK", "SOLAR_KRAFTWERK",
                "WINDMUEHLE", "WINDKRAFTWERK", "BRENN_POWER_PLANT",
                "KOHLE_KRAFTWERK", "OEL_KRAFTWERK", "GAS_KRAFTWERK",
                "POWER_PLANT",
                // Remaining industrial stuff
                "MINING", "INDUSTRIAL"

        };

        for (String lsiClassName : residentialLSIClasses){
            drawFeatureSubSet(residentialGeoms, lsiClassName, fillColor, borderColor, 0.00001);
        }


        for (DomainFeature feature : residentialGeoms) {
            addDomainFeatureToGlobalList(feature, fillColor, borderColor, 0);
        }
    }

    public void drawOpenarea(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> openareaGeoms = fetcher.getFeaturesByLsiClass(connection, "OPENAREA", null, false);
        // Extract and draw streets
        int[] strassenLSIBoundaries = LSIClassCentreDB.lsiClassRange("STRASSEN_WEGE");
        List<DomainFeature> strassenGeos = extractLSISubSet(openareaGeoms, strassenLSIBoundaries[0], strassenLSIBoundaries[1]);
        drawStreets(strassenGeos);

        // Extract and draw Parking spaces
        Color parkingSpaceColor = new Color(92, 79, 79, 255);
        drawFeatureSubSet(openareaGeoms, "RUHENDER_VERKEHR", parkingSpaceColor, parkingSpaceColor, 0.00001);

        // Extract and draw tracks
        Color trackFillColor = new Color(43, 37, 37, 255);
        drawFeatureSubSet(openareaGeoms, "GLEISKOERPER", trackFillColor, trackFillColor, 0.00001);

        // Extract and draw tram tracks
        Color tramFillColor = new Color(21, 20, 20, 255);
        drawFeatureSubSet(openareaGeoms, "TRAM_GLEISE", tramFillColor, tramFillColor, 0.00001);

        // Extract and draw tracks
        Color haltestelleFillColor = new Color(168, 134, 134, 255);
        drawFeatureSubSet(openareaGeoms, "HALTESTELLE", haltestelleFillColor, haltestelleFillColor, 0.00001);

        Color parkFillColor = new Color(4, 151, 4, 255);
        drawFeatureSubSet(openareaGeoms, "GENERAL_PUBLIC_PLACE", parkFillColor, parkFillColor, 0.00001);

        // Extract and draw tram tracks
        Color trainStationFillColor = new Color(119, 118, 118, 255);
        drawFeatureSubSet(openareaGeoms, "BAHNVERKEHR", trainStationFillColor, trainStationFillColor, 0.00001);

        // Extract and draw tram tracks
        Color trafficOtherFillColor = new Color(89, 84, 84, 255);
        drawFeatureSubSet(openareaGeoms, "TRAFFIC_PLACE", trafficOtherFillColor, trafficOtherFillColor, 0.00001);

        // Extract and draw streets
        // TODO Brücken nicht entfernen -> Upper bound anpassen
        int[] trashLSIBoundaries = LSIClassCentreDB.lsiClassRange("TRAFFIC_MORE");
        List<DomainFeature> trash = extractLSISubSet(openareaGeoms, trashLSIBoundaries[0], trashLSIBoundaries[1]);
        trashLSIBoundaries = LSIClassCentreDB.lsiClassRange("BAHNSTEIG");
        trash = extractLSISubSet(openareaGeoms, trashLSIBoundaries[0], trashLSIBoundaries[1]);

        // draw the remaining open areas
        Color fillColor = new Color(239, 221, 18, 255);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(236, 220, 50, 236);  // Darker blue
        for (DomainFeature feature : openareaGeoms) {
            addDomainFeatureToGlobalList(feature, fillColor, borderColor, 0);
        }
    }

    private void drawFeatureSubSet(List<DomainFeature> featureSet, String lsiClassName, Color fillColor, Color borderColor, double buffer){
        int[] lsiBoundaries = LSIClassCentreDB.lsiClassRange(lsiClassName);
        LsiColorMap.ColorPair colorPair = LsiColorMap.getColor(lsiClassName);
        if (colorPair.fill().equals(new Color(200, 200, 200, 180))){
            System.out.println("Use default fill color for class: " + lsiClassName);
            drawFeatureSubSet(featureSet, lsiBoundaries[0], lsiBoundaries[1], fillColor, borderColor, buffer);
        } else
            drawFeatureSubSet(featureSet, lsiBoundaries[0], lsiBoundaries[1], colorPair.fill(), colorPair.stroke(), buffer);
    }

    private void drawFeatureSubSet(List<DomainFeature> featureSet, int lowerLSIUpper, int upperLSIBorder, Color fillColor, Color borderColor, double buffer) {
        List<DomainFeature> subsetGeos = extractLSISubSet(featureSet, lowerLSIUpper, upperLSIBorder);
        for (DomainFeature feature : subsetGeos) {
            addDomainFeatureToGlobalList(feature, fillColor, borderColor, buffer);
        }
    }

    public void drawGeology(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> openareaGeoms = fetcher.getFeaturesByLsiClass(connection, "GEOLOGY", null, false);
        Color fillColor = new Color(95, 103, 112, 255);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(92, 91, 77, 236);  // Darker blue

        for (DomainFeature feature : openareaGeoms) {
            addDomainFeatureToGlobalList(feature, fillColor, borderColor, 0);
        }
    }

    public void drawOthers(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> otherGeoms = fetcher.getFeaturesByLsiClass(connection, "OTHER_OBJECTS", null, false);

        // Extract and draw tram tracks
        Color cityWallFillColor = new Color(204, 59, 27, 255);
        Color cityWallBorderColor = new Color(147, 42, 19, 255);
        drawFeatureSubSet(otherGeoms, "STADTMAUER", cityWallFillColor, cityWallBorderColor, 0.00001);
        drawFeatureSubSet(otherGeoms, "TURM", cityWallFillColor, cityWallBorderColor, 0.00001);

        Color waterProtectAreaFillColor = new Color(39, 134, 227, 242);
        drawFeatureSubSet(otherGeoms, "SCHUTZGEBIET", waterProtectAreaFillColor, waterProtectAreaFillColor, 0);

        Color begrenzungColor = new Color(66, 58, 57, 255);
        drawFeatureSubSet(otherGeoms, "BEGRENZUNG", begrenzungColor, begrenzungColor, 0.00001);

        Color historicColor = new Color(182, 53, 39, 255);
        Color historicBorderColor = new Color(145, 44, 30, 255);
        drawFeatureSubSet(otherGeoms, "HISTORIC", historicColor, historicBorderColor, 0.00001);

        Color specialBuildingColor = new Color(113, 27, 20, 255);
        Color specialBuildingBorderColor = new Color(69, 17, 10, 255);
        drawFeatureSubSet(otherGeoms, "BUILDINGS_SPECIAL_USAGE", specialBuildingColor, specialBuildingBorderColor, 0.00001);


        Color fillColor = new Color(233, 0, 255, 255);
        Color borderColor = new Color(200, 95, 239, 236);
        for (DomainFeature feature : otherGeoms) {
            addDomainFeatureToGlobalList(feature, fillColor, borderColor, 0);
        }
    }

    public void markStuff(List<DomainFeature> stuff) throws Exception {
        Color fillColor = new Color(229, 26, 236, 255);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(255, 0, 234, 255);  // Darker blue

        for (DomainFeature feature : stuff) {
            addDomainFeatureToGlobalList(feature, fillColor, borderColor, 0.0005);
        }
    }

    public void drawStreets(List<DomainFeature> streetGeoms) {
        Color fillColor = new Color(97, 91, 91, 255);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(43, 40, 40, 236);  // Darker blue

        int[] streetSubBoundaries = LSIClassCentreDB.lsiClassRange("INNERORTSTRASSE_ALL");
        List<DomainFeature> innerCityStreets = extractLSISubSet(streetGeoms, streetSubBoundaries[0], streetSubBoundaries[1]);

        drawStreetsFromDomainFeatures(innerCityStreets, fillColor, borderColor, 0.00004, 0.000026 );

        // draw remaning street geos
        drawStreetsFromDomainFeatures(streetGeoms, fillColor, borderColor, 0.000045, 0.00003);
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

                    Color fill = new Color(200, 200, 200, 180);
                    Color border = Color.darkGray;

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
            return;  // TODO handle points
        else if (feature.geometry() instanceof MultiPoint)
            return;
        else
            System.out.println("Instance of " + feature.geometry().getClass().getName() + " is not supported");
    }

    private void addDomainFeatureToGlobalList(DomainFeature feature, Color fillColor, Color borderColor, double buffer){
        drawableFeatures.add(new DrawableFeature(feature,fillColor,borderColor, buffer));
    }

    // TODO Check if still need this method or merge with other subset draw
    /// Draws a thick line of border color and a thinner liner of fillColor
    private void drawStreetsFromDomainFeatures(List<DomainFeature> features, Color fillColor, Color borderColor, double outerBuffer, double innerBuffer) {
//        for (DomainFeature feature : features) {
//            addDomainFeatureToGlobalList(feature, borderColor, borderColor, outerBuffer);
//        }
        for (DomainFeature feature : features) {
            addDomainFeatureToGlobalList(feature, fillColor, borderColor, innerBuffer);
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
                    g.setColor(Color.DARK_GRAY);
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

    /**
     * Speichert das gerenderte Bild als PNG
     */
    public void saveImage(String filename) throws Exception {
        ImageIO.write(image, "PNG", new File(filename));
    }
}