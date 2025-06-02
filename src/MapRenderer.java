import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
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

    private final int iconSize = 18; // Square icon size in pixels

    private List<DrawableFeature> drawableFeatures;
    private List<IconDrawInfo> iconDrawList = new ArrayList<>();

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

        System.out.println("icon amount " + iconDrawList.size());
        List<Rectangle> usedLabelAreas = new ArrayList<>();
        List<Rectangle> usedIconAreas = new ArrayList<>();
        Font font = new Font("SansSerif", Font.BOLD, 8);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);

        for (IconDrawInfo icon : iconDrawList) {
            try {
                BufferedImage img = ImageIO.read(new File(icon.iconPath()));
                int x = icon.x();
                int y = icon.y();
                int width = icon.width();
                int height = icon.height();

                Rectangle iconBox = new Rectangle(x, y, width, height);
                boolean overlapsAnotherIcon = usedIconAreas.stream().anyMatch(r -> r.intersects(iconBox));

                if (overlapsAnotherIcon) {
                    // Optionally skip drawing or try shifting (not yet implemented)
                    continue;
                }

                // Draw icon only if it doesn’t overlap
                g.drawImage(img, x, y, width, height, null);
                usedIconAreas.add(iconBox);

                // Label logic
                String label = cleanRealName(icon.label());
                int textWidth = fm.stringWidth(label);
                int textHeight = fm.getHeight();

                int baseX = x + (width - textWidth) / 2;
                int baseY = y + height + textHeight;

                int[][] offsets = {
                        {0, 0},                             // below
                        {0, -textHeight - 4},              // above
                        {-textWidth - 5, 0},               // left
                        {textWidth + 5, 0},                // right
                        {-textWidth / 2, textHeight + 4},  // bottom-left
                        {textWidth / 2, textHeight + 4},   // bottom-right
                };

                boolean placed = false;
                for (int[] offset : offsets) {
                    int tx = baseX + offset[0];
                    int ty = baseY + offset[1];
                    Rectangle labelBox = new Rectangle(tx, ty - textHeight, textWidth, textHeight);

                    boolean overlapsIcon = usedIconAreas.stream().anyMatch(r -> r.intersects(labelBox));
                    boolean overlapsLabel = usedLabelAreas.stream().anyMatch(r -> r.intersects(labelBox));

                    if (!overlapsIcon && !overlapsLabel) {
                        drawLabel(g, label, tx + textWidth / 2, ty - textHeight + fm.getAscent());
                        usedLabelAreas.add(labelBox);
                        placed = true;
                        break;
                    }
                }

            } catch (IOException e) {
                System.err.println("Could not load icon: " + icon.iconPath());
            }
        }
    }

        private void drawIconWithLabel(Graphics2D g, BufferedImage icon, int x, int y, int width, int height, String label) {
        // Icon zeichnen (zentriert um x/y)
        g.drawImage(icon, x, y, width, height, null);

        // Label unter dem Icon zeichnen
        int labelX = x + width / 2;
        int labelY = y + height + 2;
        drawLabel(g, label, labelX, labelY);
    }

    private void drawLabel(Graphics2D g, String label, int centerX, int baselineY) {
        Font font = new Font("SansSerif", Font.BOLD, 8);
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics(font);

        int textX = centerX - metrics.stringWidth(label) / 2;
        int textY = baselineY + metrics.getAscent();

        // Schatten (schwarz)
        g.setColor(Color.BLACK);
        g.drawString(label, textX + 1, textY + 1);

        // Vordergrund (weiß)
        g.setColor(Color.WHITE);
        g.drawString(label, textX, textY);
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

        Color fillColor = new Color(42, 195, 20, 255);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(39, 181, 21, 255);  // Darker blue

        String[] vegetationLSIClasses = {
            // single vegetation objects
            "HECKE", "BAUMREIHE", "EINZELNER_BAUM",
            "VEGETATION_SINGLE_OBJECT",

            "GRASFLAECHE", "FEUCHTWIESE", "SUMPF",

            // Agriculture
            "ACKERLAND", "OBST_ANBAUFLAECHE", "WEINBERG",
            "WEIDELAND",
            "AGRICULTURAL",

            // Forest
            "LAUBWALD", "NADELWALD", "MISCHWALD",
            "BUSCHWERK" , "WALD",
            // Rest of the vegetation
            "VEGETATION"
        };

        for (String lsiClassName : vegetationLSIClasses) {
            drawFeatureSubSet(vegetationGeoms, lsiClassName, fillColor, borderColor, 0.00001);
        }

        for (DomainFeature feature : vegetationGeoms) {
            addDomainFeatureToGlobalList(feature, fillColor, borderColor,0);
        }
    }

    public void drawCommercial(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> commercialGeoms = fetcher.getFeaturesByLsiClass(connection, "COMMERCIAL", null, false);
        Color fillColor = new Color(207, 73, 114, 180);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(159, 11, 46, 236);  // Darker blue

        String[] commercialLSIClasses = {
                // Commercial
                // Every gastronomie
                "GASTRONOMY",
                // Every shop
                "SHOP_MORE", "LESEN_SCHREIBEN", "BAUEN_EINRICHTEN_GARTEN",
                "ELEKTRONIKSHOP", "SPORT_FREIZEIT_SHOP", "KLEIDUNG",
                "KOERPERPFLEGE", "LEBENSMITTEL",
                "SHOP",
                // Every Handwerk
                "HANDWERK",
                // Sonstige  commercial ober kategorien
                "DIENSTLEISTUNG_AUTO", "FINANZEINRICHTUNG" , "POST",
                "COMMUNICATION", "TOILETS", "UNTERHALTUNG_KOMMERZIELL",
                "NACHTLEBEN", "WEITERE_DIENSTLEISTUNGEN", "UEBERNACHTUNGEN",

                "BEHOERDE",
                // Medical
                "KRANKENHAUS", "APOTHEKE", "ARZTPRAXIS",
                "MEDICAL",

                "SOCIAL_RELIGIOES",
                // Freizeit
                "UNTERHALTUNGSGEBAEUDE", "TIERPARK" , "FREIZEIT",
                // Laendlich
                "LAENDLICH",

                // Remaining non listed commercial things
                "COMMERCIAL"
        };

        for (String lsiClassName : commercialLSIClasses){
            drawFeatureSubSet(commercialGeoms, lsiClassName, fillColor, borderColor, 0.00001);
        }

        for (DomainFeature feature : commercialGeoms) {
            addDomainFeatureToGlobalList(feature, fillColor, borderColor, 0);
        }
    }

    public void drawResidential(Connection connection, DataFetcher fetcher) throws Exception {
        List<DomainFeature> residentialGeoms = fetcher.getFeaturesByLsiClass(connection, "INHABITED", null, false);
        Color fillColor = new Color(149, 6, 49, 180);
        Color borderColor = new Color(87, 2, 21, 236);

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
                "MINING", "INDUSTRIAL",

                // Commercial
                // Every gastronomie
                "GASTRONOMY",
                // Every shop
                "SHOP_MORE", "LESEN_SCHREIBEN", "BAUEN_EINRICHTEN_GARTEN",
                "ELEKTRONIKSHOP", "SPORT_FREIZEIT_SHOP", "KLEIDUNG",
                "KOERPERPFLEGE", "LEBENSMITTEL",
                "SHOP",
                // Every Handwerk
                "HANDWERK",
                // Sonstige  commercial ober kategorien
                "DIENSTLEISTUNG_AUTO", "FINANZEINRICHTUNG" , "POST",
                "COMMUNICATION", "TOILETS", "UNTERHALTUNG_KOMMERZIELL",
                "NACHTLEBEN", "WEITERE_DIENSTLEISTUNGEN", "UEBERNACHTUNGEN",

                "BEHOERDE",
                // Medical
                "KRANKENHAUS", "APOTHEKE", "ARZTPRAXIS",
                "MEDICAL",

                "SOCIAL_RELIGIOES",
                // Freizeit
                "UNTERHALTUNGSGEBAEUDE", "TIERPARK" , "FREIZEIT",
                // Laendlich
                "LAENDLICH",

                // Remaining non listed commercial things
                "COMMERCIAL"
        };

        for (String lsiClassName : residentialLSIClasses){
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


        String[] residentialLSIClasses = {
                // Residential
                "MUELLDEPONIE",
                "UBAHN_GLEISE", "BAHNKONTROLLZENTRUM",
                "BAHNVERKEHR",
                "BRIDGE", "BRIDGE_RELATION",
                "STRASSENLAMPE", "BOOTSVERLEIH", "HAFEN_ALL",
                "HUBSCHRAUBER_LANDEPLATZ", "FLUGHAFEN",

                "BUSBAHNHOF", "BUSHALTESTELLE", "U_BAHN_HALTESTELLE",
                "TRAM_HALTESTELLE", "HALTESTELLE", "BAHNHOF",

                "PARKHAUS", "RASTPLATZ", "RASTSTAETTE",
                "ALLGEMEINER_PARKPLATZ",

                "WERTSTOFFSAMMELSTELLE", "SCHWIMMBAD_ALL",

                "KLETTERN", "RENNBAHN", "GOLFPLATZ",
                "BASKETBALL_FELD", "BOWLING", "TISCHTENNIS",
                "MINIGOLF", "RUDERN", "FAHRRADFAHREN",
                "BEACHVOLLEYBALL", "HANDBALL", "BOGENSCHIESSEN",
                "MODELLFLUG", "FUSSBALL", "REITEN",
                "TENNISPLATZ", "SPORTPLATZ", "STADION",
                "SPORTS_PLACE", // remaning places

                "GRUENFLAECHE", "NAHERHOLUNGSGEBIET", "CAMPINGPLATZ",
                "SPIELPLATZ", "HUNDEPARK", "GRILLSTELLE",
                "PICNIC_PLATZ", "GARTEN", "PARK",
                "GENERAL_PUBLIC_PLACE",
                "PUBLIC_PLACE", // Remaining things
        };

        for (String lsiClassName : residentialLSIClasses){
            drawFeatureSubSet(openareaGeoms, lsiClassName, fillColor, borderColor, 0.00002);
        }

        // Extract and draw streets
        // TODO Brücken nicht entfernen -> Upper bound anpassen
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

        String[] otherObjectsLSIClasses = {
            // Other cool things
            "SITZBANK", "MUELLEIMER", "VERKAUFSAUTOMAT",
            // Tower
            "BEOBACHTUNGSTURM", "TURM", "BEGRENZUNG",
            // Sight points
            "ZIERBRUNNEN", "DENKMAL", "SIGHT_POINT",
            // Historic things
            "SCHLOSS", "RUINE", "SCHLOSS",
            "STADTMAUER", "STADTTOR", "HISTORIC",
            // Protected areas
            "NATIONALPARK", "NATURSCHUTZGEBIET",
            // Security and military
            "POLIZEI", "GEFAENGNIS", "FEUERWEHR",
            "MILITARY", "BUILDINGS_SPECIAL_USAGE",
            // Include construction works since many building are in this category...
            "BAUSTELLE"
        };

        for (String lsiClassName : otherObjectsLSIClasses) {
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
        Color fillColor = new Color(97, 91, 91, 255);  // Cornflower Blue, semi-transparent
        Color borderColor = new Color(43, 40, 40, 236);  // Darker blue

        int[] streetSubBoundaries = LSIClassCentreDB.lsiClassRange("INNERORTSTRASSE_ALL");
        List<DomainFeature> innerCityStreets = extractLSISubSet(streetGeoms, streetSubBoundaries[0], streetSubBoundaries[1]);

        drawStreetsFromDomainFeatures(innerCityStreets, fillColor, borderColor, 0.00004, 0.000026 );

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

        int lsiClass = feature.lsiclass1();
        String label = "";
        IconDisplayInfo displayInfo = getIconDisplayInfo(lsiClass);
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

    private IconDisplayInfo getIconDisplayInfo(int lsiClass) {
        if (lsiClass == 93120000) {
            return new IconDisplayInfo("fontain", true);
        } else if (lsiClass == 32115000) {
            return new IconDisplayInfo("taxi", false);
        } else if (lsiClass == 21000000) {
            return new IconDisplayInfo("park", false);
        } else if (lsiClass == 32116000 || lsiClass == 32117000) {
            return new IconDisplayInfo("parkinglot_bike", false);
        } else if (lsiClass >= 32110000 && lsiClass <= 32130000) {
            return new IconDisplayInfo("parkinglot", false);
        } else if (lsiClass >= 32140000 && lsiClass <= 32143000) {
            return new IconDisplayInfo("parking_house", false);
        } else if (lsiClass == 92330000 || lsiClass >= 31110000 && lsiClass <= 31113000) {
            return new IconDisplayInfo("park", true);
        } else if (lsiClass == 20211000) {
            return new IconDisplayInfo("university", true);  // Uni
        } else if (lsiClass == 32440000) {
            return new IconDisplayInfo("station", true);
        } else if (lsiClass >= 32410000 && lsiClass <= 32430000) {
            return new IconDisplayInfo("station", false);
        } else if (lsiClass >= 20501240 && lsiClass <= 20501247) {  // Only small restaurant subset, to many otherwise
            return new IconDisplayInfo("restaurant", true);
        }else if (lsiClass >= 20212000 && lsiClass <= 20214100) {  // schools
            return new IconDisplayInfo("school", true);
        }
        return null;
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

    private String cleanRealName(String name) {
        int idx = name.indexOf('_');
        return (idx >= 0) ? name.substring(0, idx) : name;
    }
}

