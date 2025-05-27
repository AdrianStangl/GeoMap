import com.vividsolutions.jts.geom.Geometry;

public record DomainFeature(String realname, int lsiclass1, int lsiclass2, int lsiclass3, Geometry geometry, String geometryType, double area, String tags) {
}
