package stan;

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
 * The MapRenderer class renders geographical data onto a bitmap image (PNG format).
 * It supports various feature types, including water bodies, vegetation, geology,
 * residential areas, open areas, and other objects, categorized via LSI classes.
 *
 * Core Responsibilities:
 * - Setup the drawing environment using given geometry and size parameters.
 * - Convert coordinates to pixel positions based on the bounding envelope.
 * - Retrieve data using a DataFetcher and draw features grouped by type.
 * - Label features where appropriate (e.g., names for lakes, animal attractions).
 * - Handle drawing order and label/icon placement to avoid clutter.
 *
 * Dependencies:
 * - Java AWT for drawing.
 * - JTS Topology Suite for geometrical operations.
 * - DomainFeature and LSI class utilities for data categorization.
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

    private final LabelRenderer labelRenderer;

    private final List<DrawableFeature> drawableFeatures;
    private final List<IconDrawInfo> iconDrawList = new ArrayList<>();
    private final List<IconDrawInfo> labelOnlyList = new ArrayList<>();
    private final List<IconDrawInfo> waterLabelList = new ArrayList<>();
    private final List<DomainFeature> streetFeatureList = new ArrayList<>();

    /**
     * Constructs the renderer.
     *
     * @param targetSquare Geometry that defines the geographic bounds of the rendered image.
     * @param pxWidth Width of the output image in pixels.
     * @param pxHeight Height of the output image in pixels.
     * @param meterWidth Real-world width in meters used for scale-dependent icon/font size.
     */
    public MapRenderer(Geometry targetSquare,
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

    /** Converts longitude to image pixel X. */
    private int toPixelX(double lon) {
        return (int) Math.round((lon - env.getMinX()) * scaleX);
    }
    /** Converts latitude to image pixel Y. */
    private int toPixelY(double lat) {
        return height - (int) Math.round((lat - env.getMinY()) * scaleY);
    }

    /**
     * Draws the full map using the provided database connection and data fetcher.
     * Includes water, geology, vegetation, residential areas, open areas, and others.
     */
    public void drawMap(Connection connection, DataFetcher fetcher) throws Exception {
        drawBackground();
        drawWater(connection, fetcher);
        drawGeology(connection, fetcher);
        drawVegetation(connection, fetcher);
        drawResidential(connection, fetcher);
        drawOpenarea(connection, fetcher);
        drawOthers(connection, fetcher);

        // Order the geometries by size so improve drawing order
        drawableFeatures.sort(Comparator.comparingDouble((DrawableFeature df) -> df.feature().area()).reversed());

        for (DrawableFeature drawFeature : drawableFeatures) {
            drawDomainFeature(drawFeature.feature(), drawFeature.fillColor(), drawFeature.borderColor(), drawFeature.buffer());
        }

        // Store occupied areas
        List<Shape> usedLabelAreas = new ArrayList<>();
        List<Shape> usedIconAreas = new ArrayList<>();

        // Set the font
        Font font = new Font("SansSerif", Font.BOLD, globalFontsize);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);

        // Draw icons and all labels
        labelRenderer.drawIconsAndLabels(iconDrawList, fm, usedIconAreas, usedLabelAreas);
        labelRenderer.drawNormalLabels(labelOnlyList ,usedIconAreas, usedLabelAreas, fm);
        labelRenderer.drawWaterLabels(waterLabelList ,usedIconAreas, usedLabelAreas, fm);
        labelRenderer.drawStreetLabels(g, fm, streetFeatureList, usedIconAreas ,usedLabelAreas);
    }

    /** Computes font size based on real-world width (in meters). */
    private int computeFontSizeForScale(int meters) {
        double scaleFactor = Math.log10(meters);

        int fontSize = (int) (scaleFactor * 6); // Multiplier found experimentally
        return Math.max(8, Math.min(fontSize, 30));
    }

    /** Computes icon size based on real-world width (in meters). */
    private int computeIconSize(int scaleMeters) {
        double scaleFactor = Math.log10(scaleMeters);

        int size = (int) (scaleFactor * 7);
        return Math.max(12, Math.min(size, 32));
    }

    /** Draws a given geometry as a filled polygon with a border. */
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

    /** Draws a geometry as a line using the given color. */
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

    /** Draws a solid background over the full image. */
    public void drawBackground() {
        Color backgroundColor = new Color(66, 76, 71, 181);

        drawPolygon(target, backgroundColor, backgroundColor);
    }

    /** Draws water bodies and labels them appropriately. */
    public void drawWater(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> waterGeoms = fetcher.getFeaturesByLsiClass(connection, "WATER", null, false);
        List<DomainFeature> protectGeoms = fetcher.getFeaturesByLsiClass(connection, "SCHUTZGEBIET", null, false);
        for (DomainFeature protectedFeature : protectGeoms)
            if(protectedFeature.id() == 414048)
                waterGeoms.add(protectedFeature);

        LsiColorMap.ColorPair colorPair = LsiColorMap.getColor("WATER");
        Color fillColor = colorPair.fill();
        Color borderColor = colorPair.stroke();

        for (DomainFeature feature : waterGeoms) {
            if(!feature.tags().contains("tunnel"))
                if(feature.tags().toLowerCase().contains("stream") || feature.tags().toLowerCase().contains("ditch")){
                    addDomainFeatureToGlobalList(feature, fillColor, borderColor, 0.00003);
                }
                else
                    addDomainFeatureToGlobalList(feature, fillColor, borderColor, 0.0001);


            if (!feature.realname().contains("WATER") && !feature.realname().contains("FLUSS") && !feature.realname().contains("SEE")){  // Dont add water names that are just useless (All caps, no special name)
                Coordinate center = feature.geometry().getInteriorPoint().getCoordinate();
                int labelX = toPixelX(center.x);
                int labelY = toPixelY(center.y);
                waterLabelList.add(new IconDrawInfo(null, labelX, labelY, 0, 0, feature.realname()));
            }
        }
    }

    /** Draws vegetation-related geometries. */
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

    /** Draws inhabited/residential area geometries. */
    public void drawResidential(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> residentialGeoms = fetcher.getFeaturesByLsiClass(connection, "INHABITED", null, false);
        Color fillColor = new Color(149, 6, 49, 180);
        Color borderColor = new Color(87, 2, 21, 236);

        // Extract the most important house of them all
        for (DomainFeature feature : residentialGeoms) {
            if(feature.id() == 736068){
                Coordinate center = feature.geometry().getCentroid().getCoordinate();
                int labelX = toPixelX(center.x);
                int labelY = toPixelY(center.y);
                labelOnlyList.add(new IconDrawInfo(null, labelX, labelY, 0, 0, "Joerg Roth"));
            }
        }

        for (String lsiClassName : LSIClassGroups.RESIDENTIAL){
            drawFeatureSubSet(residentialGeoms, lsiClassName, fillColor, borderColor, 0.00002);
        }

        // remaining residential geometries not affected by the subclasses from before
        for (DomainFeature feature : residentialGeoms) {
            addDomainFeatureToGlobalList(feature, fillColor, borderColor, 0);
        }
    }

    /** Draws open area geometries such as tracks, tram lines, stations, and streets. */
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

        // Extract unwanted streets
        List<DomainFeature> trash = extractLSISubSet(openareaGeoms, "TRAFFIC_MORE");
        trash = extractLSISubSet(openareaGeoms, "BAHNSTEIG");

        // draw the remaining open areas
        for (DomainFeature feature : openareaGeoms) {
            addDomainFeatureToGlobalList(feature, fillColor, borderColor, 0);
        }
    }

    /** Draws geology-related geometries. */
    public void drawGeology(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> openareaGeoms = fetcher.getFeaturesByLsiClass(connection, "GEOLOGY", null, false);
        Color fillColor = new Color(95, 103, 112, 255);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(92, 91, 77, 236);  // Darker blue

        for (DomainFeature feature : openareaGeoms) {
            addDomainFeatureToGlobalList(feature, fillColor, borderColor, 0);
        }
    }

    /** Draws various miscellaneous geometries and labels zoo attractions. */
    public void drawOthers(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> otherGeoms = fetcher.getFeaturesByLsiClass(connection, "OTHER_OBJECTS", null, false);
        Color fillColor = new Color(227, 91, 91, 221);
        Color borderColor = new Color(214, 96, 109, 216);

        for(DomainFeature feature : otherGeoms){
            // Add zoo things to labelOnlyList
            if(feature.lsiclass1() == 93140000 && feature.tags().contains("attraction=animal") && !feature.realname().equals("Leer")){
                Coordinate center = feature.geometry().getCentroid().getCoordinate();
                int labelX = toPixelX(center.x);
                int labelY = toPixelY(center.y);
                labelOnlyList.add(new IconDrawInfo(null, labelX, labelY, 0, 0, feature.realname()));
            }
        }

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
            if(feature.id() != 414048)
                addDomainFeatureToGlobalList(feature, fillColor, borderColor, 0);
        }
    }

    /** Draws a subset of features defined by an LSI class name. */
    private void drawFeatureSubSet(List<DomainFeature> featureSet, String lsiClassName, Color fillColor, Color borderColor, double buffer){
        int[] lsiBoundaries = LSIClassCentreDB.lsiClassRange(lsiClassName);
        LsiColorMap.ColorPair colorPair = LsiColorMap.getColor(lsiClassName);
        if (colorPair.fill().equals(new Color(200, 200, 200, 180))){
            System.out.println("Use default fill color for class: " + lsiClassName);
            drawFeatureSubSet(featureSet, lsiBoundaries[0], lsiBoundaries[1], fillColor, borderColor, buffer);
        } else
            drawFeatureSubSet(featureSet, lsiBoundaries[0], lsiBoundaries[1], colorPair.fill(), colorPair.stroke(), buffer);
    }

    /** Draws a subset of features defined by numeric LSI class bounds. */
    private void drawFeatureSubSet(List<DomainFeature> featureSet, int lowerLSIUpper, int upperLSIBorder, Color fillColor, Color borderColor, double buffer) {
        List<DomainFeature> subsetGeos = extractLSISubSet(featureSet, lowerLSIUpper, upperLSIBorder);
        for (DomainFeature feature : subsetGeos) {
            addDomainFeatureToGlobalList(feature, fillColor, borderColor, buffer);
        }
    }

    /**
     * Draws street geometries with appropriate styling and prepares them for labeling.
     * <p>
     * @param streetGeoms List of street features to be processed and drawn.
     */
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

        for (DomainFeature feature : streetGeoms)
            addDomainFeatureToGlobalList(feature, fillColor, borderColor, 0.00003);
    }

    /**
     * Draws a domain feature with a given fill and border color.
     * <p>
     * Handles multiple geometry types: polygons, lines, and points. Adds buffered representations
     * where appropriate and draws icons and/or labels based on LSI class metadata.
     *
     * @param feature     The feature to be drawn.
     * @param fillColor   Fill color for polygons or buffered features.
     * @param borderColor Stroke color for lines and polygon borders.
     * @param buffer      Buffer value in degrees; used to inflate lines and points for visibility.
     */
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
    }

    /**
     * Adds a domain feature with styling to the global drawable feature list.
     *
     * @param feature     The domain feature to add.
     * @param fillColor   Fill color used for rendering.
     * @param borderColor Border color used for rendering.
     * @param buffer      Optional buffer used to inflate geometries.
     */
    private void addDomainFeatureToGlobalList(DomainFeature feature, Color fillColor, Color borderColor, double buffer){
        drawableFeatures.add(new DrawableFeature(feature,fillColor,borderColor, buffer));
    }

    /**
     * Extracts features from a list that fall within the LSI class range associated with a given class name.
     *
     * @param features      The list of features to filter.
     * @param lsiClassName  LSI class name used to get the numeric class range.
     * @return A list of features matching the specified class range.
     */
    private List<DomainFeature> extractLSISubSet(List<DomainFeature> features, String lsiClassName){
        int[] classRange = LSIClassCentreDB.lsiClassRange(lsiClassName);
        return extractLSISubSet(features, classRange[0], classRange[1]);
    }

    /**
     * Extracts and removes features from the input list whose LSI class values fall within a given numeric range.
     * <p>
     * It checks `lsiclass1`, `lsiclass2`, and `lsiclass3` and removes matching features in-place.
     *
     * @param features    The input list of features (will be modified).
     * @param lowerBound  Lower bound of the LSI class range (inclusive).
     * @param upperBound  Upper bound of the LSI class range (inclusive).
     * @return A list of features that fall within the specified range.
     */
    private List<DomainFeature> extractLSISubSet(List<DomainFeature> features, int lowerBound, int upperBound) {
        List<DomainFeature> subset = new ArrayList<>();
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
     * Saves the rendered map image to a PNG file.
     *
     * @param filename The filename (including path) where the PNG image should be saved.
     * @throws Exception If writing the file fails.
     */
    public void saveImage(String filename) throws Exception {
        ImageIO.write(image, "PNG", new File(filename));
    }
}

