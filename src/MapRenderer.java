import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.sql.Connection;
import java.util.*;
import java.util.List;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
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

    private final int iconSize; // Square icon size in pixels
    private final int globalFontsize;

    private LabelRenderer labelRenderer;
    private List<DrawableFeature> drawableFeatures;
    private List<IconDrawInfo> iconDrawList = new ArrayList<>();
    private List<IconDrawInfo> labelOnlyList = new ArrayList<>();
    private List<DomainFeature> streetFeatureList = new ArrayList<>();

    public MapRenderer(Connection conn, Geometry targetSquare,
                       int pxWidth, int pxHeight, double meterWidth) throws Exception {
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

        globalFontsize = computeFontSizeForScale((int) meterWidth);
        iconSize = computeIconSize((int) meterWidth);
        drawableFeatures = new ArrayList<>();

        this.labelRenderer = new LabelRenderer(g, iconSize, globalFontsize, width, height, target);
    }

    private int toPixelX(double lon) {
        return (int) Math.round((lon - env.getMinX()) * scaleX);
    }
    private int toPixelY(double lat) {
        return height - (int) Math.round((lat - env.getMinY()) * scaleY);
    }

    public void drawMap(Connection connection, DataFetcher fetcher) throws Exception {
        drawBackground();
        drawWater(connection, fetcher);
        drawGeology(connection, fetcher);
        drawVegetation(connection, fetcher);
        drawResidential(connection, fetcher);
        drawOpenarea(connection, fetcher);
        drawOthers(connection, fetcher);

        drawableFeatures.sort(Comparator.comparingDouble((DrawableFeature df) -> df.feature().area()).reversed());

        for (DrawableFeature drawFeature : drawableFeatures) {
            drawDomainFeature(drawFeature.feature(), drawFeature.fillColor(), drawFeature.borderColor(), drawFeature.buffer());
        }

        List<Shape> usedLabelAreas = new ArrayList<>();
        List<Shape> usedIconAreas = new ArrayList<>();

        Font font = new Font("SansSerif", Font.BOLD, globalFontsize);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);

        labelRenderer.drawIconsAndLabels(iconDrawList, fm, usedIconAreas, usedLabelAreas);
        labelRenderer.drawLabels(labelOnlyList ,usedIconAreas, usedLabelAreas, fm);
        labelRenderer.drawStreetLabels(g, fm, streetFeatureList, usedIconAreas ,usedLabelAreas);
    }

    private int computeFontSizeForScale(int meters) {
        // Logarithmic scaling
        double scaleFactor = Math.log10(meters);

        int fontSize = (int) (scaleFactor * 6); // Multiplier found experimentally
        return Math.max(8, Math.min(fontSize, 30));
    }

    private int computeIconSize(int scaleMeters) {
        double scaleFactor = Math.log10(scaleMeters);

        int size = (int) (scaleFactor * 10);
        return Math.max(12, Math.min(size, 32));
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
        
        g.setColor(borderColor);
        g.draw(path);
    }

    public void drawBackground() {
        Color backgroundColor = new Color(66, 76, 71, 181);  // Cornflower Blue, semi-transparent

        drawPolygon(target, backgroundColor, backgroundColor);
    }

    public void drawWater(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> waterGeoms = fetcher.getFeaturesByLsiClass(connection, "WATER", null, false);
        List<DomainFeature> protectGeoms = fetcher.getFeaturesByLsiClass(connection, "SCHUTZGEBIET", null, false);
        for (DomainFeature protectedFeature : protectGeoms)
            if(protectedFeature.realname().contains("Landschaftsschutzgebiet Wöhrder See"))
                waterGeoms.add(protectedFeature);

        LsiColorMap.ColorPair colorPair = LsiColorMap.getColor("WATER");
        Color fillColor = colorPair.fill();
        Color borderColor = colorPair.stroke();

        for (DomainFeature feature : waterGeoms) {
            if(!feature.tags().contains("tunnel"))
                if(feature.tags().toLowerCase().contains("stream")){
                    addDomainFeatureToGlobalList(feature, fillColor, borderColor, 0.00003);
                }
                else
                    addDomainFeatureToGlobalList(feature, fillColor, borderColor, 0.0001);
        }
    }

    public void drawVegetation(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> vegetationGeoms = fetcher.getFeaturesByLsiClass(connection, "VEGETATION", null, false);

        Color fillColor = new Color(42, 195, 20, 255);
        Color borderColor = new Color(39, 181, 21, 255);

        for (String lsiClassName : LSIClassGroups.VEGETATION) {
            drawFeatureSubSet(vegetationGeoms, lsiClassName, fillColor, borderColor, 0.00001);
        }

        for (DomainFeature feature : vegetationGeoms) {
            addDomainFeatureToGlobalList(feature, fillColor, borderColor,0);
        }
    }

    public void drawResidential(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> residentialGeoms = fetcher.getFeaturesByLsiClass(connection, "INHABITED", null, false);
        Color fillColor = new Color(149, 6, 49, 180);
        Color borderColor = new Color(87, 2, 21, 236);

        for (String lsiClassName : LSIClassGroups.RESIDENTIAL){
            drawFeatureSubSet(residentialGeoms, lsiClassName, fillColor, borderColor, 0.00002);
        }

        // remaining residential geometries not affected by the subclasses from before
        for (DomainFeature feature : residentialGeoms) {
            addDomainFeatureToGlobalList(feature, fillColor, borderColor, 0);
        }
    }

    public void drawOpenarea(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> openareaGeoms = fetcher.getFeaturesByLsiClass(connection, "OPENAREA", null, false);
        Color fillColor = new Color(237, 222, 107, 255);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(200, 190, 73, 236);  // Darker blue

        // Extract and draw streets
        int[] strassenLSIBoundaries = LSIClassCentreDB.lsiClassRange("STRASSEN_WEGE");
        List<DomainFeature> strassenGeos = extractLSISubSet(openareaGeoms, strassenLSIBoundaries[0], strassenLSIBoundaries[1]);
        drawStreets(strassenGeos);

        // These classes need to be drawn before
        // Extract and draw tracks
        Color trackFillColor = new Color(43, 37, 37, 255);
        drawFeatureSubSet(openareaGeoms, "GLEISKOERPER", trackFillColor, trackFillColor, 0.00001);

        // Extract and draw tram tracks
        Color tramFillColor = new Color(21, 20, 20, 255);
        drawFeatureSubSet(openareaGeoms, "TRAM_GLEISE", tramFillColor, tramFillColor, 0.00001);

        // Extract and draw tracks
        Color haltestelleFillColor = new Color(168, 134, 134, 255);
        drawFeatureSubSet(openareaGeoms, "HALTESTELLE", haltestelleFillColor, haltestelleFillColor, 0.00001);

        for (String lsiClassName : LSIClassGroups.OPENAREAS){
            drawFeatureSubSet(openareaGeoms, lsiClassName, fillColor, borderColor, 0.00002);
        }

        // Extract and draw streets
        int[] trashLSIBoundaries = LSIClassCentreDB.lsiClassRange("TRAFFIC_MORE");
        List<DomainFeature> trash = extractLSISubSet(openareaGeoms, trashLSIBoundaries[0], trashLSIBoundaries[1]);
        trashLSIBoundaries = LSIClassCentreDB.lsiClassRange("BAHNSTEIG");
        trash = extractLSISubSet(openareaGeoms, trashLSIBoundaries[0], trashLSIBoundaries[1]);

        // draw the remaining open areas
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
        Color fillColor = new Color(227, 91, 91, 221);
        Color borderColor = new Color(214, 96, 109, 216);

        for (String lsiClassName : LSIClassGroups.OTHER) {
            drawFeatureSubSet(otherGeoms, lsiClassName, fillColor, borderColor, 0.00002);
        }

        // Do not draw water here since already in draw water, just extract
        extractLSISubSet(otherGeoms, "WASSERSCHUTZGEBIET");
        extractLSISubSet(otherGeoms, "SCHUTZGEBIET");
        // Dont care for those objects
        extractLSISubSet(otherGeoms, "HYDRANT");
        extractLSISubSet(otherGeoms, "MEILENSTEIN");

        // Draw remaning geoms not in the list
        for (DomainFeature feature : otherGeoms) {
            System.out.println("not clarified for: " + feature.realname()+ " " + feature.lsiclass1() + " " + feature.lsiclass2()+" " + feature.lsiclass3());
            if(!feature.realname().contains("Landschaftsschutzgebiet Wöhrder See"))
                addDomainFeatureToGlobalList(feature, fillColor, borderColor, 0);
        }
    }

    public void drawStreets(List<DomainFeature> streetGeoms) {
        // List for all the streets that should get labels
        List<DomainFeature> labelstreetGeomList = new ArrayList<>();
        // default road colors is the innerort color
        LsiColorMap.ColorPair pair = LsiColorMap.getColor("INNERORTSTRASSE_ALL");
        Color fillColor = pair.fill();
        Color borderColor = pair.stroke();

        // Extracting the streets in the order of this list also indirectly creates a priority for the labels
        for (String lsiClassName : LSIClassGroups.STREETS) {
            List<DomainFeature> tempStreetList = extractLSISubSet(streetGeoms, lsiClassName);

            labelstreetGeomList.addAll(tempStreetList);
            drawFeatureSubSet(tempStreetList, lsiClassName, fillColor, borderColor, 0.000026);
        }

        for (DomainFeature feature : labelstreetGeomList)
            if (!feature.realname().contains("_"))  // Names like ZUFAHRT_4564485 get skipped
                streetFeatureList.add(feature);

        // draw remaning street geos
        drawStreetsFromDomainFeatures(streetGeoms, fillColor, borderColor, 0.000045, 0.00003);
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
        else if (feature.geometry() instanceof MultiLineString){
            for (int i = 0; i < feature.geometry().getNumGeometries(); i++) {
                drawLineGeometry(feature.geometry().getGeometryN(i), borderColor);
            }
        }
        else if (feature.geometry() instanceof Point)
            drawPolygon(feature.geometry().buffer(0.000001), fillColor, borderColor);
        else if (feature.geometry() instanceof MultiPoint)
            for (int i = 0; i < feature.geometry().getNumGeometries(); i++) {
                drawPolygon(feature.geometry().buffer(0.000001), fillColor, borderColor);
            }
        else
            System.out.println("Instance of " + feature.geometry().getClass().getName() + " is not supported");

        // Add icons and or labels  to icondrawlist
        int lsiClass = feature.lsiclass1();
        String label = "";
        IconDisplayInfo displayInfo = LsiIconLabelMap.getIconDisplayInfo(lsiClass);
        if (displayInfo != null) {
            Coordinate center = feature.geometry().getCentroid().getCoordinate();
            int iconX = toPixelX(center.x) - iconSize / 2; // center with 24px icon
            int iconY = toPixelY(center.y) - iconSize / 2;
            if(displayInfo.display())
                label = feature.realname();

            iconDrawList.add(new IconDrawInfo(
                    "icons/" + displayInfo.icon() + ".png",
                    iconX, iconY, iconSize, iconSize, label
            ));
        }

        // Add zoo things to labelOnlyList
        if(feature.lsiclass1() == 93140000 && feature.tags().contains("attraction=animal") && !feature.realname().equals("Leer")){
            Coordinate center = feature.geometry().getCentroid().getCoordinate();
            int labelX = toPixelX(center.x);
            int labelY = toPixelY(center.y);
            labelOnlyList.add(new IconDrawInfo(null, labelX, labelY, 0, 0, feature.realname()));
            if (feature.realname().contains("vögel")) System.out.println("wasservogel lsi klasse. " + feature.lsiclass1() + " " + feature.lsiclass2() + " " + feature.lsiclass3());
        }
    }


    private void addDomainFeatureToGlobalList(DomainFeature feature, Color fillColor, Color borderColor, double buffer){
        drawableFeatures.add(new DrawableFeature(feature,fillColor,borderColor, buffer));
    }

    // TODO Check if still need this method or merge with other subset draw
    /// Draws a thick line of border color and a thinner liner of fillColor
    private void drawStreetsFromDomainFeatures(List<DomainFeature> features, Color fillColor, Color borderColor, double outerBuffer, double innerBuffer) {
        for (DomainFeature feature : features) {
            addDomainFeatureToGlobalList(feature, fillColor, borderColor, innerBuffer);
        }
    }

    private List<DomainFeature> extractLSISubSet(List<DomainFeature> features, String lsiClassName){
        int[] classRange = LSIClassCentreDB.lsiClassRange(lsiClassName);
        return extractLSISubSet(features, classRange[0], classRange[1]);
    }

    /// Returns subset of features list where the features are between lowerBound and upperBound, including the borders
    private List<DomainFeature> extractLSISubSet(List<DomainFeature> features, int lowerBound, int upperBound) {
        List<DomainFeature> subset = new ArrayList<DomainFeature>();
        for (int i = 0; i < features.size(); i++) {
            DomainFeature feature = features.get(i);
            if (feature.lsiclass3() >= lowerBound && feature.lsiclass3() <= upperBound){
                subset.add(features.remove(i--));
            }
            else if (feature.lsiclass2() >= lowerBound && feature.lsiclass2() <= upperBound){
                subset.add(features.remove(i--));
            }
            else if (feature.lsiclass1() >= lowerBound && feature.lsiclass1() <= upperBound){
                subset.add(features.remove(i--));
            }
        }
        return subset;
    }

    /**
     * Speichert das gerenderte Bild als PNG
     */
    public void saveImage(String filename) throws Exception {
        ImageIO.write(image, "PNG", new File(filename));
    }
}

