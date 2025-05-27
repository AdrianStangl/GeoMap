import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

// Note: Colors have been AI generated with a lot of pain and suffering
public class LsiColorMap {
    public record ColorPair(Color fill, Color stroke) {}

    private static final Map<String, ColorPair> colorMap = new HashMap<>();

    static {
        // Water color, used for everything water related
        colorMap.put("WATER", new ColorPair(new Color(70, 130, 180, 255), new Color(25, 90, 140, 255))); // steel blue tones
        colorMap.put("SCHUTZGEBIET", new ColorPair(new Color(70, 130, 180, 255), new Color(25, 90, 140, 255))); // steel blue tones

        // ◼ Gebäude / Wohnen
        colorMap.put("GEWAECHSHAUS",        new ColorPair(new Color(144, 238, 144, 180), new Color(34, 139, 34, 200)));
        colorMap.put("UNTERSTAND",          new ColorPair(new Color(189, 252, 201, 180), new Color(85, 107, 47, 200)));
        colorMap.put("HUT",                 new ColorPair(new Color(255, 239, 186, 180), new Color(218, 165, 32, 200)));
        colorMap.put("SHED",                new ColorPair(new Color(230, 190, 255, 180), new Color(147, 112, 219, 200)));
        colorMap.put("BARN_BUILDING",       new ColorPair(new Color(255, 204, 203, 180), new Color(178, 34, 34, 200)));
        colorMap.put("FARM_BUILDING",       new ColorPair(new Color(255, 228, 196, 180), new Color(205, 133, 63, 200)));
        colorMap.put("HOUSE",               new ColorPair(new Color(173, 216, 230, 180), new Color(70, 130, 180, 200)));
        colorMap.put("DETACHED",            new ColorPair(new Color(176, 224, 230, 180), new Color(95, 158, 160, 200)));
        colorMap.put("TERRACE",             new ColorPair(new Color(221, 160, 221, 180), new Color(138, 43, 226, 200)));
        colorMap.put("APPARTEMENTS",        new ColorPair(new Color(215, 191, 216, 180), new Color(186, 85, 211, 200)));
        colorMap.put("GARAGES",             new ColorPair(new Color(255, 222, 173, 180), new Color(210, 105, 30, 200)));
        colorMap.put("GARAGE",              new ColorPair(new Color(255, 218, 185, 180), new Color(244, 164, 96, 200)));
        colorMap.put("CARPORT",             new ColorPair(new Color(240, 230, 140, 180), new Color(189, 183, 107, 200)));
        colorMap.put("BUILDING",            new ColorPair(new Color(176, 196, 222, 180), new Color(119, 136, 153, 200)));
        colorMap.put("RESIDENTIAL_BUILDING",new ColorPair(new Color(250, 250, 210, 180), new Color(189, 183, 107, 200)));
        colorMap.put("RESIDENTIAL",         new ColorPair(new Color(255, 228, 225, 180), new Color(219, 112, 147, 200)));
        colorMap.put("COMMERCIAL_BUILDING", new ColorPair(new Color(255, 160, 122, 180), new Color(255, 99, 71, 200)));
        colorMap.put("INDUSTRIAL_BUILDING", new ColorPair(new Color(176, 224, 230, 180), new Color(70, 130, 180, 200)));
        colorMap.put("GRUNDSCHULE",         new ColorPair(new Color(255, 255, 153, 180), new Color(255, 215, 0, 200)));
        colorMap.put("MELDUNGSEINRICHTUNGEN", new ColorPair(new Color(152, 251, 152, 180), new Color(60, 179, 113, 200)));
        colorMap.put("POWER_PLANT",         new ColorPair(new Color(255, 140, 105, 180), new Color(205, 92, 92, 200)));

        // ◼ Akademisch / Bildung
        colorMap.put("SCHOOL_BUILDING",     new ColorPair(new Color(240, 230, 140, 180), new Color(189, 183, 107, 200)));
        colorMap.put("GRUNDSCHOOL",         new ColorPair(new Color(255, 250, 205, 180), new Color(238, 232, 170, 200)));
        colorMap.put("BERUFSSCHULE",        new ColorPair(new Color(144, 238, 144, 180), new Color(0, 128, 0, 200)));
        colorMap.put("BESONDERE_SCHULE",    new ColorPair(new Color(175, 238, 238, 180), new Color(32, 178, 170, 200)));
        colorMap.put("HAUPTSCHULE",         new ColorPair(new Color(221, 160, 221, 180), new Color(186, 85, 211, 200)));
        colorMap.put("REALSCHULE",          new ColorPair(new Color(173, 216, 230, 180), new Color(0, 191, 255, 200)));
        colorMap.put("GYMNASIUM",           new ColorPair(new Color(240, 248, 255, 180), new Color(135, 206, 235, 200)));
        colorMap.put("UNIVERSITY",          new ColorPair(new Color(176, 196, 222, 180), new Color(123, 104, 238, 200)));
        colorMap.put("ACADEMIC",            new ColorPair(new Color(230, 230, 250, 180), new Color(186, 85, 211, 200)));
        colorMap.put("ASTRONOMIE",          new ColorPair(new Color(216, 191, 216, 180), new Color(138, 43, 226, 200)));
        colorMap.put("RESEARCH",            new ColorPair(new Color(255, 239, 213, 180), new Color(255, 160, 122, 200)));
        colorMap.put("LIBRARY",             new ColorPair(new Color(255, 182, 193, 180), new Color(199, 21, 133, 200)));

        // ◼ Routen
        colorMap.put("ROUTE",               new ColorPair(new Color(200, 230, 255, 180), new Color(150, 190, 240, 200)));
        colorMap.put("ROUTE_TRAIN",         new ColorPair(new Color(180, 210, 255, 180), new Color(130, 170, 240, 200)));
        colorMap.put("ROUTE_TRAM",          new ColorPair(new Color(160, 190, 255, 180), new Color(110, 150, 240, 200)));
        colorMap.put("ROUTE_BUS",           new ColorPair(new Color(140, 170, 255, 180), new Color( 90, 130, 240, 200)));
        colorMap.put("ROUTE_ROAD",          new ColorPair(new Color(120, 150, 255, 180), new Color( 70, 110, 240, 200)));
        colorMap.put("ROUTE_BICYCLE",       new ColorPair(new Color(100, 130, 255, 180), new Color( 50,  90, 240, 200)));

        // ◼ Wasseraufbereitung & Management
        colorMap.put("BRUNNEN",             new ColorPair(new Color(175, 238, 238, 180), new Color(0, 139, 139, 200)));
        colorMap.put("BASIN",               new ColorPair(new Color(0, 191, 255, 180), new Color(0, 0, 139, 200)));
        colorMap.put("RESERVOIR",           new ColorPair(new Color(30, 144, 255, 180), new Color(25, 25, 112, 200)));
        colorMap.put("STAUDAMM",            new ColorPair(new Color(70, 130, 180, 180), new Color(0, 0, 128, 200)));
        colorMap.put("WASSERHOCHBEHAELTER", new ColorPair(new Color(135, 206, 235, 180), new Color(0, 191, 255, 200)));
        colorMap.put("WASSERTURM",          new ColorPair(new Color(176, 224, 230, 180), new Color(72, 209, 204, 200)));
        colorMap.put("KLAERWERK",           new ColorPair(new Color(100, 149, 237, 180), new Color(70, 130, 180, 200)));
        colorMap.put("WASSERWERK",          new ColorPair(new Color(0, 206, 209, 180), new Color(0, 139, 139, 200)));
        colorMap.put("WASSERAUFBEREITUNG",  new ColorPair(new Color(0, 191, 255, 180), new Color(25, 25, 112, 200)));
        colorMap.put("ROHRLEITUNG",         new ColorPair(new Color(176, 196, 222, 180), new Color(119, 136, 153, 200)));

        // ◼ Energie / Kraftwerk
        colorMap.put("MUEHLE",              new ColorPair(new Color(255, 228, 196, 180), new Color(205, 133,  63, 200)));
        colorMap.put("WASSER_KRAFTWERK",    new ColorPair(new Color(135, 206, 250, 180), new Color( 70, 130, 180, 200)));
        colorMap.put("BRENN_POWER_PLANT",   new ColorPair(new Color(255, 160, 122, 180), new Color(178,  34,  34, 200)));
        colorMap.put("KOHLE_KRAFTWERK",     new ColorPair(new Color(105, 105, 105, 180), new Color( 80,  80,  80, 200)));
        colorMap.put("OEL_KRAFTWERK",       new ColorPair(new Color(244, 164,  96, 180), new Color(139,  69,  19, 200)));
        colorMap.put("GAS_KRAFTWERK",       new ColorPair(new Color(255, 222, 173, 180), new Color(210, 180, 140, 200)));
        colorMap.put("SOLAR_KRAFTWERK",     new ColorPair(new Color(255, 223,   0, 180), new Color(218, 165,  32, 200)));
        colorMap.put("WINDMUEHLE",          new ColorPair(new Color(224, 255, 255, 180), new Color(175, 238, 238, 200)));
        colorMap.put("WINDKRAFTWERK",       new ColorPair(new Color(200, 230, 250, 180), new Color(135, 206, 235, 200)));
        colorMap.put("TRANSFORMATOR",       new ColorPair(new Color(211, 211, 211, 180), new Color(169, 169, 169, 200)));
        colorMap.put("TRAFOHAUS",           new ColorPair(new Color(192, 192, 192, 180), new Color(128, 128, 128, 200)));
        colorMap.put("STROMVERTEILER",      new ColorPair(new Color(210, 180, 222, 180), new Color(186,  85, 211, 200)));
        colorMap.put("STROMLEITUNG",        new ColorPair(new Color(190, 190, 190, 180), new Color(119, 136, 153, 200)));
        colorMap.put("STROMMAST",           new ColorPair(new Color(169, 169, 169, 180), new Color(105, 105, 105, 200)));
        colorMap.put("WASSERRAD",           new ColorPair(new Color(222, 184, 135, 180), new Color(160,  82,  45, 200)));
        colorMap.put("UMSPANNSTATION",      new ColorPair(new Color(250, 235, 215, 180), new Color(210, 180, 140, 200)));
        colorMap.put("POWER_PLANT_ADDON",   new ColorPair(new Color(255, 228, 225, 180), new Color(240, 128, 128, 200)));


        // ◼ Übriges Industrie
        colorMap.put("MINING",              new ColorPair(new Color(205, 133,  63, 180), new Color(160,  82,  45, 200)));
        colorMap.put("INDUSTRIAL",          new ColorPair(new Color(176, 196, 222, 180), new Color(119, 136, 153, 200)));

        // Gastronomy
        colorMap.put("GASTRONOMY", new ColorPair(new Color(255, 239, 213, 180), new Color(222, 184, 135, 200))); // warm vanilla/biscuit

        // Shops
        colorMap.put("SHOP_MORE",                   new ColorPair(new Color(240, 255, 240, 180), new Color(144, 238, 144, 200))); // mint green
        colorMap.put("LESEN_SCHREIBEN",             new ColorPair(new Color(224, 255, 255, 180), new Color(175, 238, 238, 200))); // soft blue
        colorMap.put("BAUEN_EINRICHTEN_GARTEN",     new ColorPair(new Color(240, 255, 240, 180), new Color(152, 251, 152, 200))); // garden green
        colorMap.put("ELEKTRONIKSHOP",              new ColorPair(new Color(230, 230, 250, 180), new Color(138,  43, 226, 200))); // electric violet
        colorMap.put("SPORT_FREIZEIT_SHOP",         new ColorPair(new Color(255, 250, 205, 180), new Color(238, 232, 170, 200))); // light gold
        colorMap.put("KLEIDUNG",                    new ColorPair(new Color(255, 228, 225, 180), new Color(219, 112, 147, 200))); // rose/pink
        colorMap.put("KOERPERPFLEGE",               new ColorPair(new Color(255, 240, 245, 180), new Color(255, 182, 193, 200))); // light blush
        colorMap.put("LEBENSMITTEL",                new ColorPair(new Color(250, 250, 210, 180), new Color(238, 232, 170, 200))); // soft yellow
        colorMap.put("SHOP",                        new ColorPair(new Color(245, 245, 245, 180), new Color(192, 192, 192, 200))); // generic grey

        // Handwerk
        colorMap.put("HANDWERK", new ColorPair(new Color(222, 184, 135, 180), new Color(160,  82,  45, 200))); // wood/brown

        // Services
        colorMap.put("DIENSTLEISTUNG_AUTO",         new ColorPair(new Color(211, 211, 211, 180), new Color(169, 169, 169, 200))); // tech grey
        colorMap.put("FINANZEINRICHTUNG",           new ColorPair(new Color(245, 255, 250, 180), new Color(144, 238, 144, 200))); // mint for money
        colorMap.put("POST",                        new ColorPair(new Color(255, 250, 205, 180), new Color(218, 165,  32, 200))); // yellow postal tone
        colorMap.put("COMMUNICATION",              new ColorPair(new Color(224, 255, 255, 180), new Color(176, 224, 230, 200))); // light blue
        colorMap.put("TOILETS",                     new ColorPair(new Color(240, 248, 255, 180), new Color(175, 238, 238, 200))); // hygienic cyan
        colorMap.put("UNTERHALTUNG_KOMMERZIELL",    new ColorPair(new Color(255, 228, 196, 180), new Color(244, 164,  96, 200))); // orange-beige
        colorMap.put("NACHTLEBEN",                  new ColorPair(new Color(138,  43, 226, 180), new Color( 72,  61, 139, 200))); // nightlife purple
        colorMap.put("WEITERE_DIENSTLEISTUNGEN",    new ColorPair(new Color(240, 255, 240, 180), new Color(211, 211, 211, 200))); // pale green/grey
        colorMap.put("UEBERNACHTUNGEN",             new ColorPair(new Color(255, 239, 213, 180), new Color(210, 180, 140, 200))); // warm/soft beige

        // Behörden
        colorMap.put("BEHOERDE", new ColorPair(new Color(245, 245, 245, 180), new Color(190, 190, 190, 200))); // neutral grey-white

        // Medical
        colorMap.put("KRANKENHAUS", new ColorPair(new Color(224, 255, 255, 180), new Color(135, 206, 250, 200))); // clean blue
        colorMap.put("APOTHEKE",    new ColorPair(new Color(255, 228, 225, 180), new Color(240, 128, 128, 200))); // soft red
        colorMap.put("ARZTPRAXIS",  new ColorPair(new Color(255, 250, 250, 180), new Color(220, 220, 220, 200))); // clean white/grey
        colorMap.put("MEDICAL",     new ColorPair(new Color(230, 255, 255, 180), new Color(176, 224, 230, 200))); // cyan blue

        // Religion & Social
        colorMap.put("SOCIAL_RELIGIOES", new ColorPair(new Color(255, 248, 220, 180), new Color(210, 180, 140, 200))); // creamy spiritual

        // Freizeit
        colorMap.put("UNTERHALTUNGSGEBAEUDE", new ColorPair(new Color(255, 240, 245, 180), new Color(255, 182, 193, 200))); // fun pink
        colorMap.put("TIERPARK",              new ColorPair(new Color(204, 255, 204, 180), new Color(152, 251, 152, 200))); // animal green
        colorMap.put("FREIZEIT",              new ColorPair(new Color(255, 228, 225, 180), new Color(255, 160, 122, 200))); // coral fun

        // Ländlich
        colorMap.put("LAENDLICH", new ColorPair(new Color(240, 255, 240, 180), new Color(189, 183, 107, 200))); // field green

        // Catch-all commercial
        colorMap.put("COMMERCIAL", new ColorPair(new Color(245, 245, 220, 180), new Color(200, 200, 180, 200))); // soft beige-grey

        // Other object colors
        colorMap.put("SITZBANK",            new ColorPair(new Color(205, 133, 63, 180),  new Color(139, 90, 43, 200)));   // Saddle brown
        colorMap.put("MUELLEIMER",          new ColorPair(new Color(169, 169, 169, 180), new Color(105, 105, 105, 200))); // Dark gray
        colorMap.put("VERKAUFSAUTOMAT",     new ColorPair(new Color(255, 140, 105, 180), new Color(220, 90, 50, 200)));   // Vending machine orange

        colorMap.put("BEGRENZUNG",          new ColorPair(new Color(150, 130, 110, 180), new Color(100, 90, 80, 200)));
        colorMap.put("BEOBACHTUNGSTURM",    new ColorPair(new Color(190, 190, 250, 180), new Color(130, 130, 200, 200))); // Pale violet tower
        colorMap.put("TURM",                new ColorPair(new Color(175, 160, 210, 180), new Color(115, 105, 160, 200))); // Purple-gray stone tower

        colorMap.put("ZIERBRUNNEN",         new ColorPair(new Color(180, 230, 250, 180), new Color(120, 190, 220, 200))); // Light blue decorative fountain
        colorMap.put("DENKMAL",             new ColorPair(new Color(210, 180, 140, 180), new Color(160, 130, 90, 200)));  // Tan historic monument
        colorMap.put("SIGHT_POINT",         new ColorPair(new Color(240, 200, 120, 180), new Color(200, 160, 80, 200)));  // Golden view point

        colorMap.put("SCHLOSS",             new ColorPair(new Color(200, 170, 180, 180), new Color(150, 120, 130, 200))); // Soft royal rose castle
        colorMap.put("RUINE",               new ColorPair(new Color(160, 160, 140, 180), new Color(110, 110, 90, 200)));  // Weathered stone ruins
        colorMap.put("STADTMAUER",          new ColorPair(new Color(190, 155, 120, 180), new Color(140, 115, 90, 200)));  // Brick historic wall
        colorMap.put("STADTTOR",            new ColorPair(new Color(170, 140, 110, 180), new Color(120, 100, 80, 200)));  // Gate wood/stone
        colorMap.put("HISTORIC",            new ColorPair(new Color(185, 165, 145, 180), new Color(135, 115, 95, 200)));  // Generic historic site

        colorMap.put("LANDSCHAFTSSCHUTZGEBIET", new ColorPair(new Color(170, 220, 180, 180), new Color(110, 180, 130, 200))); // Light green preserve
        colorMap.put("NATIONALPARK",        new ColorPair(new Color(150, 200, 150, 180), new Color(90, 160, 90, 200)));       // Deep green
        colorMap.put("NATURSCHUTZGEBIET",   new ColorPair(new Color(190, 230, 170, 180), new Color(130, 190, 110, 200)));     // Pale nature zone

        colorMap.put("POLIZEI",             new ColorPair(new Color(70, 130, 180, 180),  new Color(25, 90, 140, 200)));   // Strong blue
        colorMap.put("GEFAENGNIS",          new ColorPair(new Color(120, 120, 120, 180), new Color(80, 80, 80, 200)));    // Dark gray stone
        colorMap.put("FEUERWEHR",           new ColorPair(new Color(255, 100, 100, 180), new Color(200, 50, 50, 200)));   // Fire red

        colorMap.put("MILITARY",            new ColorPair(new Color(100, 110, 90, 180),  new Color(70, 80, 60, 200)));    // Army olive tones
        colorMap.put("BUILDINGS_SPECIAL_USAGE", new ColorPair(new Color(190, 190, 230, 180), new Color(140, 140, 180, 200))); // Special use, bluish-gray

        // Single vegetation
        colorMap.put("HECKE",             new ColorPair(new Color(120, 180, 100, 180), new Color(80, 140, 60, 200)));    // Dense green hedge
        colorMap.put("BAUMREIHE",         new ColorPair(new Color(110, 160, 90, 180),  new Color(70, 120, 50, 200)));    // Aligned trees
        colorMap.put("EINZELNER_BAUM",    new ColorPair(new Color(130, 190, 110, 180), new Color(90, 150, 70, 200)));    // Single tree

        // Wet grassy terrain
        colorMap.put("GRASFLAECHE",       new ColorPair(new Color(170, 220, 140, 180), new Color(130, 180, 100, 200)));  // Lush grass
        colorMap.put("FEUCHTWIESE",       new ColorPair(new Color(150, 210, 170, 180), new Color(100, 170, 120, 200)));  // Moist meadow
        colorMap.put("SUMPF",             new ColorPair(new Color(140, 160, 100, 180), new Color(90, 110, 60, 200)));    // Swamp green-brown

        // Agriculture
        colorMap.put("ACKERLAND",         new ColorPair(new Color(200, 180, 120, 180), new Color(160, 140, 80, 200)));   // Cultivated field
        colorMap.put("OBST_ANBAUFLAECHE", new ColorPair(new Color(180, 200, 140, 180), new Color(140, 160, 100, 200)));  // Orchard
        colorMap.put("WEINBERG",          new ColorPair(new Color(190, 210, 160, 180), new Color(150, 170, 120, 200)));  // Vineyard
        colorMap.put("WEIDELAND",         new ColorPair(new Color(180, 230, 150, 180), new Color(140, 190, 110, 200)));  // Pasture
        colorMap.put("AGRICULTURAL",      new ColorPair(new Color(190, 200, 130, 180), new Color(150, 160, 90, 200)));   // General agricultural land

        // Forests
        colorMap.put("LAUBWALD",          new ColorPair(new Color(90, 150, 70, 180),   new Color(60, 100, 40, 200)));    // Deciduous forest
        colorMap.put("NADELWALD",         new ColorPair(new Color(70, 110, 80, 180),   new Color(40, 80, 50, 200)));     // Coniferous forest
        colorMap.put("MISCHWALD",         new ColorPair(new Color(80, 130, 80, 180),   new Color(50, 100, 50, 200)));    // Mixed forest
        colorMap.put("BUSCHWERK",         new ColorPair(new Color(100, 140, 90, 180),  new Color(70, 100, 60, 200)));    // Shrubbery

        // Generated
//        colorMap.put("UNDEF",                    new ColorPair(new Color(238,238,238,180), new Color(200,200,200,200)));
//        colorMap.put("POSTLEITZAHLBEREICH",      new ColorPair(new Color(255,224,224,180), new Color(255,192,192,200)));
//        colorMap.put("ADDRESS_POINT",            new ColorPair(new Color(255,235,238,180), new Color(255,192,200,200)));
//        colorMap.put("BESCHRIFTUNG_VORORT",      new ColorPair(new Color(232,232,255,180), new Color(192,192,255,200)));
//        colorMap.put("BESCHRIFTUNG_LOKALITAET",  new ColorPair(new Color(255,245,232,180), new Color(255,232,192,200)));
//        colorMap.put("BUILDINGAREA",             new ColorPair(new Color(250,240,240,180), new Color(220,200,200,200)));
//        colorMap.put("BUILDING",                 new ColorPair(new Color(240,240,250,180), new Color(200,200,220,200)));
//        colorMap.put("RESIDENTIAL_BUILDING",     new ColorPair(new Color(255,250,240,180), new Color(240,220,200,200)));
//        colorMap.put("GARAGES",                  new ColorPair(new Color(245,245,232,180), new Color(220,220,200,200)));
//        colorMap.put("APPARTEMENTS",             new ColorPair(new Color(232,245,255,180), new Color(200,220,240,200)));
//        colorMap.put("COMMERCIAL_BUILDING",      new ColorPair(new Color(240,255,232,180), new Color(200,240,220,200)));
//        colorMap.put("UNTERSTAND",               new ColorPair(new Color(232,250,245,180), new Color(200,240,225,200)));
//        colorMap.put("BUILDING_ENTRANCE",        new ColorPair(new Color(255,240,232,180), new Color(245,220,200,200)));
//        colorMap.put("ROOF",                     new ColorPair(new Color(240,235,255,180), new Color(220,210,240,200)));
//        colorMap.put("ELEVATOR",                 new ColorPair(new Color(232,232,245,180), new Color(200,200,220,200)));
//        colorMap.put("GRUND_SEKUNDARSCHULE",     new ColorPair(new Color(255,245,232,180), new Color(255,232,200,200)));
//        colorMap.put("TELEFONZELLE",             new ColorPair(new Color(232,255,255,180), new Color(200,232,240,200)));
//        colorMap.put("NOTRUFTELEFON",            new ColorPair(new Color(255,232,232,180), new Color(240,200,200,200)));
//        colorMap.put("UEBERWACHUNGSCAMERA",      new ColorPair(new Color(245,245,245,180), new Color(210,210,210,200)));
//        colorMap.put("STROMMAST",                new ColorPair(new Color(245,250,232,180), new Color(220,240,200,200)));
//        colorMap.put("COMMERCIAL",               new ColorPair(new Color(255,240,245,180), new Color(240,200,215,200)));
//        colorMap.put("RESTAURANT",               new ColorPair(new Color(255,235,232,180), new Color(240,210,200,200)));
//        colorMap.put("RESTAURANT_REGIONAL",      new ColorPair(new Color(255,232,215,180), new Color(235,200,180,200)));
//        colorMap.put("RESTAURANT_ITALIENISCH",   new ColorPair(new Color(255,240,220,180), new Color(240,215,190,200)));
//        colorMap.put("KNEIPE",                   new ColorPair(new Color(240,232,255,180), new Color(205,185,240,200)));
//        colorMap.put("CAFE",                     new ColorPair(new Color(255,245,232,180), new Color(225,200,180,200)));
//        colorMap.put("FASTFOOD",                 new ColorPair(new Color(255,232,240,180), new Color(240,210,220,200)));
//        colorMap.put("GASTRONOMY_AREA",         new ColorPair(new Color(255,240,232,180), new Color(240,215,200,200)));
//        colorMap.put("SHOP",                     new ColorPair(new Color(232,250,255,180), new Color(200,230,240,200)));
//        colorMap.put("SUPERMARKT",               new ColorPair(new Color(232,255,240,180), new Color(200,240,220,200)));
//        colorMap.put("CONVENIENCE",              new ColorPair(new Color(255,250,232,180), new Color(240,235,200,200)));
//        colorMap.put("BAECKER",                  new ColorPair(new Color(255,245,232,180), new Color(245,220,200,200)));
//        colorMap.put("FLEISCHER",                new ColorPair(new Color(255,240,245,180), new Color(240,200,215,200)));
//        colorMap.put("DROGERIE",                 new ColorPair(new Color(250,240,255,180), new Color(230,210,240,200)));
//        colorMap.put("KLEIDUNGSGESCHAEFT",       new ColorPair(new Color(232,245,255,180), new Color(200,215,240,200)));
//        colorMap.put("FAHRRADAUSLEIHE",          new ColorPair(new Color(232,255,250,180), new Color(200,240,230,200)));
//        colorMap.put("MOBILFUNK_HANDY_SHOP",     new ColorPair(new Color(255,232,245,180), new Color(240,200,220,200)));
//        colorMap.put("MOEBELHAUS",               new ColorPair(new Color(245,255,232,180), new Color(220,240,210,200)));
//        colorMap.put("GESCHENKARTIKEL",          new ColorPair(new Color(255,240,232,180), new Color(240,215,200,200)));
//        colorMap.put("FLORIST",                  new ColorPair(new Color(255,232,240,180), new Color(235,200,220,200)));
//        colorMap.put("KIOSK",                    new ColorPair(new Color(232,255,235,180), new Color(200,240,210,200)));
//        colorMap.put("AUTOVERMIETUNG",           new ColorPair(new Color(235,245,255,180), new Color(200,220,240,200)));
//        colorMap.put("BANK_KREDITUNTERNEHMEN",   new ColorPair(new Color(245,235,232,180), new Color(220,210,200,200)));
//        colorMap.put("GELDAUTOMAT",               new ColorPair(new Color(232,240,255,180), new Color(200,220,240,200)));
//        colorMap.put("RECHTSANWALT",             new ColorPair(new Color(255,232,245,180), new Color(240,215,230,200)));
//        colorMap.put("POST_FILIALE",             new ColorPair(new Color(245,250,232,180), new Color(220,240,200,200)));
//        colorMap.put("BRIEFKASTEN",              new ColorPair(new Color(232,235,245,180), new Color(200,210,230,200)));
//        colorMap.put("TOILETS",                  new ColorPair(new Color(240,232,255,180), new Color(215,200,240,200)));
//        colorMap.put("WERBUNG",                  new ColorPair(new Color(255,245,232,180), new Color(240,225,200,200)));
//        colorMap.put("FRISOER",                  new ColorPair(new Color(255,232,232,180), new Color(240,200,200,200)));
//        colorMap.put("REINIGUNG",                new ColorPair(new Color(232,245,232,180), new Color(200,230,200,200)));
//        colorMap.put("HOTEL",                    new ColorPair(new Color(245,232,255,180), new Color(220,200,240,200)));
//        colorMap.put("TOURIST_OFFICE",           new ColorPair(new Color(232,255,232,180), new Color(200,240,200,200)));
//        colorMap.put("TOURIST_BESCHILDERUNG",    new ColorPair(new Color(255,250,232,180), new Color(240,235,200,200)));
//        colorMap.put("TOURIST_KARTE",            new ColorPair(new Color(232,240,255,180), new Color(215,200,240,200)));
//        colorMap.put("BEHOERDE",                 new ColorPair(new Color(255,240,232,180), new Color(240,220,200,200)));
//        colorMap.put("APOTHEKE",                 new ColorPair(new Color(235,240,255,180), new Color(200,220,240,200)));
//        colorMap.put("ARZTPRAXIS",               new ColorPair(new Color(255,232,245,180), new Color(240,200,230,200)));
//        colorMap.put("ARZTPRAXIS_ZAHNARZT",      new ColorPair(new Color(245,232,255,180), new Color(220,200,240,200)));
//        colorMap.put("KINDERGARTEN",             new ColorPair(new Color(255,245,232,180), new Color(240,230,200,200)));
//        colorMap.put("KIRCHLICH",                new ColorPair(new Color(232,232,245,180), new Color(200,200,220,200)));
//        colorMap.put("FREIZEIT",                 new ColorPair(new Color(232,250,232,180), new Color(200,230,200,200)));
//        colorMap.put("KUNSTZENTRUM",             new ColorPair(new Color(255,240,232,180), new Color(240,200,200,200)));
//        colorMap.put("BUEROGEBAEUDE",            new ColorPair(new Color(232,240,255,180), new Color(200,220,240,200)));
//        colorMap.put("NAHERHOLUNGSGEBIET",       new ColorPair(new Color(240,255,232,180), new Color(200,240,200,200)));
//        colorMap.put("ALTGLASBEHAELTER",         new ColorPair(new Color(255,232,240,180), new Color(240,200,220,200)));
//        colorMap.put("ALLGEMEINER_PARKPLATZ",     new ColorPair(new Color(240,232,255,180), new Color(210,200,240,200)));
//        colorMap.put("PARKPLATZ_PRIVAT",         new ColorPair(new Color(232,245,232,180), new Color(200,220,200,200)));
//        colorMap.put("PARKBUCHT",                new ColorPair(new Color(255,232,230,180), new Color(240,210,200,200)));
//        colorMap.put("TAXISTAND",                new ColorPair(new Color(232,250,245,180), new Color(200,240,220,200)));
//        colorMap.put("FAHRRADPARKPLATZ",         new ColorPair(new Color(245,255,232,180), new Color(220,240,200,200)));
//        colorMap.put("FAHRRADPARKPLATZ_GEBAEUDE",new ColorPair(new Color(255,240,245,180), new Color(240,200,230,200)));
//        colorMap.put("CARSHARING",               new ColorPair(new Color(232,255,250,180), new Color(200,240,230,200)));
//        colorMap.put("PARKHAUS",                 new ColorPair(new Color(240,232,255,180), new Color(210,200,240,200)));
//        colorMap.put("BAHNHOF",                  new ColorPair(new Color(255,240,232,180), new Color(240,200,200,200)));
//        colorMap.put("BAHNSTEIG",                new ColorPair(new Color(245,232,255,180), new Color(220,200,240,200)));
//        colorMap.put("HALTESTELLE",              new ColorPair(new Color(232,245,255,180), new Color(200,220,240,200)));
//        colorMap.put("BUSHALTESTELLE",           new ColorPair(new Color(240,255,232,180), new Color(200,240,200,200)));
//        colorMap.put("U_BAHN_HALTESTELLE",       new ColorPair(new Color(255,232,245,180), new Color(240,200,230,200)));
//        colorMap.put("TRAM_HALTESTELLE",         new ColorPair(new Color(232,240,255,180), new Color(200,220,240,200)));
//        colorMap.put("TRAFFIC_MORE",             new ColorPair(new Color(232,240,232,180), new Color(200,215,200,200)));
//        colorMap.put("POLLER",                   new ColorPair(new Color(245,232,255,180), new Color(220,200,240,200)));
//        colorMap.put("FAHRRAD_BARRIERE",         new ColorPair(new Color(232,250,245,180), new Color(200,240,220,200)));
//        colorMap.put("BAUSTELLE_VERKEHR",        new ColorPair(new Color(240,232,245,180), new Color(215,200,230,200)));
//        colorMap.put("VERKEHRSSIGNAL",           new ColorPair(new Color(255,232,240,180), new Color(240,200,220,200)));
//        colorMap.put("KREUZUNGSSIGNAL",          new ColorPair(new Color(232,240,235,180), new Color(200,220,215,200)));
//        colorMap.put("BRIDGE",                   new ColorPair(new Color(245,245,255,180), new Color(220,220,240,200)));
//        colorMap.put("TURN_RESTRICTION",         new ColorPair(new Color(232,245,232,180), new Color(200,230,200,200)));
//        colorMap.put("TMC_RELATION",             new ColorPair(new Color(255,232,232,180), new Color(240,200,200,200)));
//        colorMap.put("NOEXIT",                   new ColorPair(new Color(232,232,242,180), new Color(200,200,210,200)));
//        colorMap.put("BAHNVERKEHR",              new ColorPair(new Color(232,255,255,180), new Color(200,240,240,200)));
//        colorMap.put("GLEISKOERPER",             new ColorPair(new Color(245,232,240,180), new Color(220,200,215,200)));
//        colorMap.put("GLEISKOERPER_UNBENUTZT",   new ColorPair(new Color(232,240,245,180), new Color(200,215,220,200)));
//        colorMap.put("RANGIERBEREICH",           new ColorPair(new Color(255,232,240,180), new Color(240,200,215,200)));
//        colorMap.put("TRAM_GLEISE",              new ColorPair(new Color(240,232,235,180), new Color(215,200,205,200)));
//        colorMap.put("UBAHN_GLEISE",             new ColorPair(new Color(232,240,255,180), new Color(200,215,240,200)));
//        colorMap.put("BAHNEUBERGANG",            new ColorPair(new Color(255,232,240,180), new Color(240,200,230,200)));
//        colorMap.put("PUFFERSTOP",               new ColorPair(new Color(232,245,232,180), new Color(200,230,200,200)));
//        colorMap.put("WEICHE",                   new ColorPair(new Color(245,232,255,180), new Color(220,200,240,200)));
//        colorMap.put("LANDSTRASSE_SEKUNDAER",    new ColorPair(new Color(232,255,240,180), new Color(200,240,220,200)));
//        colorMap.put("LANDSTRASSE_TERTIAER",     new ColorPair(new Color(255,232,232,180), new Color(240,200,200,200)));
//        colorMap.put("LANDSTRASSE_UNKLASSIFIZIERT",new ColorPair(new Color(232,240,255,180), new Color(200,215,240,200)));
//        colorMap.put("INNERORTSTRASSE",          new ColorPair(new Color(255,232,235,180), new Color(240,200,210,200)));
//        colorMap.put("VERKEHRSBERUHIGTER_BEREICH",new ColorPair(new Color(232,232,242,180), new Color(200,200,215,200)));
//        colorMap.put("ERSCHLIESSUNGSWEG",        new ColorPair(new Color(255,240,232,180), new Color(240,215,200,200)));
//        colorMap.put("ZUFAHRT",                  new ColorPair(new Color(232,255,232,180), new Color(200,240,200,200)));
//        colorMap.put("PARKPLATZWEG",             new ColorPair(new Color(245,232,255,180), new Color(220,215,240,200)));
//        colorMap.put("ANSCHLUSSSTELLE_SEKUNDAER",new ColorPair(new Color(232,240,232,180), new Color(200,215,200,200)));
//        colorMap.put("FAHRRAD_FUSS_WEG",         new ColorPair(new Color(255,232,240,180), new Color(240,210,215,200)));
//        colorMap.put("FAHRRADWEG",               new ColorPair(new Color(232,255,235,180), new Color(200,240,215,200)));
//        colorMap.put("FUSSWEG",                  new ColorPair(new Color(232,240,232,180), new Color(200,215,200,200)));
//        colorMap.put("FUSSGAENGERZONE",          new ColorPair(new Color(255,232,245,180), new Color(240,200,230,200)));
//        colorMap.put("TREPPE",                   new ColorPair(new Color(232,245,232,180), new Color(200,230,200,200)));
//        colorMap.put("FUSSGAENGERUEBERWEG",      new ColorPair(new Color(255,240,232,180), new Color(240,215,200,200)));
//        colorMap.put("PFAD_WANDERWEG",           new ColorPair(new Color(232,255,240,180), new Color(200,240,220,200)));
//        colorMap.put("GRASFLAECHE",              new ColorPair(new Color(232,255,232,180), new Color(200,240,200,200)));
//        colorMap.put("EINZELNER_BAUM",           new ColorPair(new Color(232,240,232,180), new Color(200,215,200,200)));
//        colorMap.put("POLIZEI",                  new ColorPair(new Color(255,232,232,180), new Color(240,200,200,200)));
//        colorMap.put("BAUSTELLE",                new ColorPair(new Color(232,230,245,180), new Color(215,200,230,200)));
//        colorMap.put("HISTORIC",                 new ColorPair(new Color(245,232,230,180), new Color(220,215,200,200)));
//        colorMap.put("STADTMAUER",               new ColorPair(new Color(232,240,240,180), new Color(200,215,215,200)));
//        colorMap.put("DENKMAL",                  new ColorPair(new Color(255,232,245,180), new Color(240,200,230,200)));
//        colorMap.put("ZIERBRUNNEN",              new ColorPair(new Color(232,235,255,180), new Color(220,210,240,200)));
//        colorMap.put("KUNSTWERK",                new ColorPair(new Color(255,230,250,180), new Color(240,200,230,200)));
//        colorMap.put("TOURISTIC_SIGHT",          new ColorPair(new Color(255,245,232,180), new Color(240,220,200,200)));
//        colorMap.put("MEILENSTEIN_RAILWAY",      new ColorPair(new Color(232,255,245,180), new Color(200,240,220,200)));
//        colorMap.put("TURM",                     new ColorPair(new Color(240,232,255,180), new Color(215,200,240,200)));
//        colorMap.put("SITZBANK",                 new ColorPair(new Color(255,240,232,180), new Color(240,215,200,200)));
//        colorMap.put("HYDRANT",                  new ColorPair(new Color(240,232,240,180), new Color(215,200,215,200)));
//        colorMap.put("STREUGUTKONTAINER",        new ColorPair(new Color(255,232,235,180), new Color(240,200,210,200)));
//        colorMap.put("MUELLEIMER",               new ColorPair(new Color(232,255,232,180), new Color(200,240,200,200)));
//        colorMap.put("VERKAUFSAUTOMAT",          new ColorPair(new Color(255,232,250,180), new Color(240,200,230,200)));
//        colorMap.put("ZAUN",                     new ColorPair(new Color(232,232,245,180), new Color(215,215,240,200)));
//        colorMap.put("ZAUN_AREA",                new ColorPair(new Color(235,245,255,180), new Color(200,220,240,200)));
//        colorMap.put("MAUER",                    new ColorPair(new Color(255,240,232,180), new Color(240,215,215,200)));
//        colorMap.put("STUETZMAUER",              new ColorPair(new Color(232,255,245,180), new Color(200,240,220,200)));
//        colorMap.put("SCHLAGBAUM",               new ColorPair(new Color(255,232,230,180), new Color(240,210,200,200)));
//        colorMap.put("TOR",                      new ColorPair(new Color(232,240,255,180), new Color(200,215,240,200)));
    }

    public static ColorPair getColor(String key) {
        return colorMap.getOrDefault(key, new ColorPair(new Color(200,200,200,180), new Color(150,150,150,200)));
    }
}
