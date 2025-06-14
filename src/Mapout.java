import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKBWriter;
import fu.keys.LSIClassCentreDB;
import fu.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class Mapout {
    public static void main(String[] args) {
        // ... DBUtil.parseDBparams(args[0],0); etc.
        Connection connection = null;

        try {
            /* Zugang zur Datenbank einrichten */
            DBUtil.parseDBparams("127.0.0.1/5432/dbuser/dbuser/deproDBMittelfrankenPG", 0);
            connection = DBUtil.getConnection(0);
            connection.setAutoCommit(false);  //Getting results based on a cursor
            LSIClassCentreDB.initFromDB(connection);
        } catch (Exception e) {
            System.out.println("Error initialising DB access: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }

        // Kommandozeile:
        double centerLat = Double.parseDouble(args[0]);
        double centerLon = Double.parseDouble(args[1]);
        int pxWidth = Integer.parseInt(args[2]);
        int pxHeight = Integer.parseInt(args[3]);
        double meterWidth = Double.parseDouble(args[4]);
        String outputFile = args[5];

        System.out.println("Read command line parameters: ");
        System.out.println("Center Lat: "+centerLat);
        System.out.println("Center Lon: "+centerLon);
        System.out.println("PxWidth: "+pxWidth);
        System.out.println("PxHeight: "+pxHeight);
        System.out.println("MeterWidth: "+meterWidth);

        try {
            drawMapForParameters(connection, centerLat, centerLon, pxWidth, pxHeight, meterWidth, outputFile);
            drawMapForParameters(connection, 49.445555, 11.082587, 1024, 512, 1234.5, "examplePrompt.png");
            drawMapForParameters(connection, 49.44750, 11.14575, 2048, 1024, 2300, "zoo.png");
            drawMapForParameters(connection, 49.45120, 11.09857, 1024, 512, 500, "see.png");
        }
        catch (Exception e) {
            System.out.println("Error processing DB queries: "+e.toString());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void drawMapForParameters(Connection connection, double centerLat, double centerLon, int pxWidth, int pxHeight, double meterWidth, String outputFile) throws Exception {
        DataFetcher fetcher = new DataFetcher(connection,
                centerLat, centerLon,
                pxWidth, pxHeight,
                meterWidth);
        // Karte rendern
        MapRenderer renderer = new MapRenderer(connection,
                fetcher.getTargetSquare(),
                pxWidth, pxHeight);
        renderer.drawMap(connection, fetcher);
        renderer.saveImage(outputFile);

        // fetcher.printDistinctLSIClassesWithDescription(connection);

        System.out.println("Karte geschrieben nach " + outputFile);
    }

}
