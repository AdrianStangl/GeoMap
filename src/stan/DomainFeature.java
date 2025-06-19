package stan;

import com.vividsolutions.jts.geom.Geometry;
///  Describe a Feature entry storing the lsiclasses, geometry and type as well as the area of the geometry and the tags of the entrie
public record DomainFeature(String realname, int lsiclass1, int lsiclass2, int lsiclass3, Geometry geometry, String geometryType, double area, String tags) {}
