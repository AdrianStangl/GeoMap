package stan;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.Polygon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GeometryPainter {
    public static void paintGeometry(Graphics2D g, Geometry geom, String name, String icon, Color color) throws IOException {
        if (geom instanceof com.vividsolutions.jts.geom.Polygon) {

            LineString exteriorRing =((Polygon)geom).getExteriorRing();
            int n=exteriorRing.getNumPoints();

            double minX= 1e10;
            double minY= 1e10;
            double maxX=-1e10;
            double maxY=-1e10;

            for (int i=0;i<n;i++) {
                Coordinate coord=exteriorRing.getCoordinateN(i);
                minX=Math.min(minX,coord.x);
                minY=Math.min(minY,coord.y);
                maxX=Math.max(maxX,coord.x);
                maxY=Math.max(maxY,coord.y);
            }


            int[] x=new int[n];
            int[] y=new int[n];

            for (int i=0;i<n;i++) {
                Coordinate coord=exteriorRing.getCoordinateN(i);
                x[i]=(int)Math.round((coord.x-minX)*512/(maxX-minX));
                y[i]=511-(int)Math.round((coord.y-minY)*512/(maxY-minY));
            }

            g.setColor(color);
            g.drawPolygon(x,y,x.length);

            BufferedImage iconImage= ImageIO.read(new File("icons" +File.separator+icon+".png"));
            g.drawImage(iconImage,200,220,null);

            g.drawString(name,200,200);

        }
        else if (geom instanceof MultiPolygon) {
            for (int i = 0; i < geom.getNumGeometries(); i++){
                paintGeometry(g,geom.getGeometryN(i),name,icon, Color.RED);
            }
        }
        else
            throw new IllegalArgumentException("Don't know how to paint "+geom.getClass());
    }


}
