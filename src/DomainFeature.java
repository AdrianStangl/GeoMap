import com.vividsolutions.jts.geom.Geometry;

public record DomainFeature(String realname, int lsiclass1, Geometry geometry, String geometryType) {
}
