package stan;

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

        colorMap.put("BEGRENZUNG",          new ColorPair(new Color(150, 130, 110, 220), new Color(100, 90, 80, 200)));
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
        colorMap.put("BAUSTELLE", new ColorPair(new Color(255, 180, 60, 220), new Color(200, 120, 30, 240)));


        // Single vegetation
        colorMap.put("HECKE",             new ColorPair(new Color(120, 180, 100, 180), new Color(80, 140, 60, 200)));    // Dense green hedge
        colorMap.put("BAUMREIHE",         new ColorPair(new Color(110, 160, 90, 180),  new Color(70, 120, 50, 200)));    // Aligned trees
        colorMap.put("EINZELNER_BAUM",    new ColorPair(new Color(130, 190, 110, 180), new Color(90, 150, 70, 200)));    // Single tree
        colorMap.put("VEGETATION_SINGLE_OBJECT", new ColorPair(new Color(190, 230, 150, 180), new Color(140, 180, 110, 200)));

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
        colorMap.put("WALD",              new ColorPair(new Color(140, 190, 110, 180), new Color( 90, 140,  70, 200)));
        // General vegetaion not in the other sub classes
        colorMap.put("VEGETATION",              new ColorPair(new Color(170, 220, 130, 180), new Color(120, 170,  90, 200)));

        // Openarea stuff
        colorMap.put("MUELLDEPONIE",        new ColorPair(new Color(100, 90, 70, 180),   new Color(60, 55, 45, 200)));   // dusty brown/grey
        colorMap.put("TRAM_GLEISE",         new ColorPair(new Color(21, 20, 20, 255),    new Color(21, 20, 20, 255)));   // dark asphalt/steel
        colorMap.put("UBAHN_GLEISE",        new ColorPair(new Color(60, 65, 80, 180),    new Color(40, 45, 60, 200)));   // darker steel/underground
        colorMap.put("BAHNKONTROLLZENTRUM", new ColorPair(new Color(85, 70, 100, 180),   new Color(55, 45, 75, 200)));   // subdued violet-grey
        colorMap.put("BAHNVERKEHR",         new ColorPair(new Color(119, 118, 118, 255),    new Color(119, 118, 118, 255)));   // deep charcoal (pure infra)
        colorMap.put("GLEISKOERPER",         new ColorPair(new Color(43, 37, 37, 255),    new Color(43, 37, 37, 255)));   // deep charcoal (pure infra)
        colorMap.put("TRAFFIC_PLACE",         new ColorPair(new Color(89, 84, 84, 255),    new Color(89, 84, 84, 255)));   // deep charcoal (pure infra)

        colorMap.put("BRIDGE",                    new ColorPair(new Color(220, 210, 200, 180), new Color(160, 150, 140, 200)));
        colorMap.put("BRIDGE_RELATION",           new ColorPair(new Color(210, 200, 190, 180), new Color(150, 140, 130, 200)));

        colorMap.put("STRASSENLAMPE",             new ColorPair(new Color(255, 245, 180, 180), new Color(210, 200, 130, 200)));
        colorMap.put("BOOTSVERLEIH",              new ColorPair(new Color(170, 215, 230, 180), new Color(110, 160, 180, 200)));
        colorMap.put("HAFEN_ALL",                 new ColorPair(new Color(160, 200, 220, 180), new Color(100, 140, 160, 200)));

        colorMap.put("HUBSCHRAUBER_LANDEPLATZ",   new ColorPair(new Color(240, 220, 190, 180), new Color(180, 160, 130, 200)));
        colorMap.put("FLUGHAFEN",                 new ColorPair(new Color(230, 210, 180, 180), new Color(170, 150, 120, 200)));

        colorMap.put("BUSBAHNHOF",                new ColorPair(new Color(90, 90, 90, 180),   new Color(50, 50, 50, 200)));  // dark gray
        colorMap.put("BUSHALTESTELLE",            new ColorPair(new Color(85, 85, 85, 180),   new Color(45, 45, 45, 200)));
        colorMap.put("U_BAHN_HALTESTELLE",        new ColorPair(new Color(70, 70, 80, 180),   new Color(40, 40, 50, 200)));  // steely
        colorMap.put("TRAM_HALTESTELLE",          new ColorPair(new Color(80, 75, 70, 180),   new Color(45, 40, 35, 200)));  // asphalt
        colorMap.put("HALTESTELLE",               new ColorPair(new Color(168, 134, 134, 255),   new Color(168, 134, 134, 255)));
        colorMap.put("BAHNHOF",                   new ColorPair(new Color(55, 55, 65, 180),   new Color(25, 25, 35, 200)));  // dark blue-gray


        colorMap.put("PARKHAUS",                  new ColorPair(new Color(210, 210, 210, 180), new Color(160, 160, 160, 200)));
        colorMap.put("RASTPLATZ",                 new ColorPair(new Color(200, 230, 200, 180), new Color(140, 180, 140, 200)));
        colorMap.put("RASTSTAETTE",               new ColorPair(new Color(195, 225, 195, 180), new Color(135, 175, 135, 200)));
        colorMap.put("ALLGEMEINER_PARKPLATZ",     new ColorPair(new Color(190, 220, 190, 180), new Color(130, 170, 130, 200)));

        colorMap.put("WERTSTOFFSAMMELSTELLE",     new ColorPair(new Color(180, 200, 180, 180), new Color(120, 140, 120, 200)));
        colorMap.put("SCHWIMMBAD_ALL",            new ColorPair(new Color(160, 215, 240, 180), new Color(100, 165, 190, 200)));

        colorMap.put("KLETTERN",                  new ColorPair(new Color(220, 200, 180, 180), new Color(160, 140, 120, 200)));
        colorMap.put("RENNBAHN",                  new ColorPair(new Color(200, 170, 160, 180), new Color(140, 110, 100, 200)));
        colorMap.put("GOLFPLATZ",                 new ColorPair(new Color(180, 220, 150, 180), new Color(120, 160, 100, 200)));
        colorMap.put("BASKETBALL_FELD",           new ColorPair(new Color(250, 190, 160, 180), new Color(190, 130, 100, 200)));
        colorMap.put("BOWLING",                   new ColorPair(new Color(230, 180, 180, 180), new Color(170, 120, 120, 200)));
        colorMap.put("TISCHTENNIS",               new ColorPair(new Color(220, 240, 220, 180), new Color(160, 180, 160, 200)));
        colorMap.put("MINIGOLF",                  new ColorPair(new Color(190, 230, 190, 180), new Color(130, 170, 130, 200)));
        colorMap.put("RUDERN",                    new ColorPair(new Color(160, 210, 230, 180), new Color(100, 150, 170, 200)));
        colorMap.put("FAHRRADFAHREN",             new ColorPair(new Color(220, 230, 180, 180), new Color(160, 170, 120, 200)));
        colorMap.put("BEACHVOLLEYBALL",           new ColorPair(new Color(250, 230, 160, 180), new Color(190, 170, 100, 200)));
        colorMap.put("HANDBALL",                  new ColorPair(new Color(240, 200, 160, 180), new Color(180, 140, 100, 200)));
        colorMap.put("BOGENSCHIESSEN",            new ColorPair(new Color(230, 210, 190, 180), new Color(170, 150, 130, 200)));
        colorMap.put("MODELLFLUG",                new ColorPair(new Color(210, 240, 250, 180), new Color(150, 180, 190, 200)));
        colorMap.put("FUSSBALL",                  new ColorPair(new Color(180, 240, 180, 180), new Color(120, 180, 120, 200)));
        colorMap.put("REITEN",                    new ColorPair(new Color(230, 210, 170, 180), new Color(170, 150, 110, 200)));
        colorMap.put("TENNISPLATZ",               new ColorPair(new Color(160, 230, 200, 180), new Color(100, 170, 140, 200)));
        colorMap.put("SPORTPLATZ",                new ColorPair(new Color(190, 240, 210, 180), new Color(130, 180, 150, 200)));
        colorMap.put("STADION",                   new ColorPair(new Color(200, 220, 240, 180), new Color(140, 160, 180, 200)));
        colorMap.put("SPORTS_PLACE",              new ColorPair(new Color(180, 230, 220, 180), new Color(120, 170, 160, 200)));

        colorMap.put("GRUENFLAECHE",         new ColorPair(new Color(81, 188, 81, 200),   new Color(60, 140, 60, 220)));
        colorMap.put("NAHERHOLUNGSGEBIET",   new ColorPair(new Color(76, 168, 88, 200),   new Color(56, 128, 66, 220)));
        colorMap.put("CAMPINGPLATZ",         new ColorPair(new Color(97, 158, 73, 200),   new Color(67, 118, 53, 220)));
        colorMap.put("SPIELPLATZ",           new ColorPair(new Color(110, 160, 90, 200),  new Color(80, 120, 65, 220)));
        colorMap.put("HUNDEPARK",            new ColorPair(new Color(95, 145, 85, 200),   new Color(65, 105, 60, 220)));
        colorMap.put("GRILLSTELLE",          new ColorPair(new Color(102, 132, 70, 200),  new Color(70, 100, 50, 220)));
        colorMap.put("PICNIC_PLATZ",         new ColorPair(new Color(115, 150, 85, 200),  new Color(85, 110, 60, 220)));
        colorMap.put("GARTEN",               new ColorPair(new Color(90, 160, 75, 200),   new Color(60, 120, 50, 220)));
        colorMap.put("PARK",                 new ColorPair(new Color(81, 170, 81, 200),   new Color(55, 125, 55, 220)));
        colorMap.put("GENERAL_PUBLIC_PLACE", new ColorPair(new Color(81, 188, 81, 255),   new Color(61, 148, 61, 255)));
        colorMap.put("PUBLIC_PLACE",         new ColorPair(new Color(76, 160, 90, 200),   new Color(56, 120, 66, 220)));

        // Streets
        colorMap.put("AUTOBAHN",             new ColorPair(new Color(100, 140, 210, 255), new Color( 50,  90, 160, 240)));
        colorMap.put("KRAFTFAHRSTRASSE",     new ColorPair(new Color(100, 160, 190, 255), new Color( 40, 110, 150, 230)));
        colorMap.put("LANDSTRASSE",          new ColorPair(new Color(200, 170, 110, 255), new Color(140, 110,  60, 230)));
        colorMap.put("INNERORTSTRASSE_ALL",  new ColorPair(new Color(120, 120, 120, 255), new Color( 50,  50,  50, 240)));

    }

    public static ColorPair getColor(String key) {
        return colorMap.getOrDefault(key, new ColorPair(new Color(200,200,200,180), new Color(150,150,150,200)));
    }
}
