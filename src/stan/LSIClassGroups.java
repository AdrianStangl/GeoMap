package stan;

///  Provides lists of the lsi classes that should be drawn
public class LSIClassGroups {

    public static final String[] RESIDENTIAL = {
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

    public static final String[] OPENAREAS = {
        // Openarea
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

    public static final String[] OTHER = {
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

    public static final String[] VEGETATION = {
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

    public static final String[] STREETS = {
        "AUTOBAHN", "KRAFTFAHRSTRASSE", "LANDSTRASSE",
        "INNERORTSTRASSE_ALL",
    };
}