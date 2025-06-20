package stan;

import fu.keys.LSIClassCentreDB;
import fu.util.DBUtil;

import java.sql.Connection;

/**
 * Entry point for rendering a map image based on specified geographic and rendering parameters.
 * <p>
 * This class connects to the LSI database, fetches relevant spatial data, and delegates rendering
 * to the {@link MapRenderer} using the {@link DataFetcher}.
 */
public class Mapout {
    /**
     * Main method for running the map rendering pipeline.
     * <p>
     * Required command-line arguments:
     * <ol>
     *   <li>Latitude of map center (double)</li>
     *   <li>Longitude of map center (double)</li>
     *   <li>Image width in pixels (int)</li>
     *   <li>Image height in pixels (int)</li>
     *   <li>Map width in meters (double)</li>
     *   <li>Output file path (String)</li>
     * </ol>
     *
     * @param args command line arguments as described above
     */
    public static void main(String[] args) {
        Connection connection = null;

        try {
            /* Setup DB access */
            DBUtil.parseDBparams("127.0.0.1/5432/dbuser/dbuser/deproDBMittelfrankenPG", 0);
            connection = DBUtil.getConnection(0);
            connection.setAutoCommit(false);
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
        }
        catch (Exception e) {
            System.out.println("Error processing DB queries: "+e.toString());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Rendering of a map image based on the provided parameters.
     *
     * @param connection  active database connection
     * @param centerLat   latitude of map center
     * @param centerLon   longitude of map center
     * @param pxWidth     width of the output image in pixels
     * @param pxHeight    height of the output image in pixels
     * @param meterWidth  width of the map area in meters
     * @param outputFile  file path to save the rendered image
     * @throws Exception if rendering or data fetching fails
     */
    private static void drawMapForParameters(Connection connection, double centerLat, double centerLon, int pxWidth, int pxHeight, double meterWidth, String outputFile) throws Exception {
        DataFetcher fetcher = new DataFetcher(centerLat, centerLon,
                pxWidth, pxHeight,
                meterWidth);
        // Render map
        MapRenderer renderer = new MapRenderer(fetcher.getTargetSquare(), pxWidth, pxHeight, meterWidth);
        renderer.drawMap(connection, fetcher);
        renderer.saveImage(outputFile);

        System.out.println("Karte geschrieben nach " + outputFile);
    }

}
