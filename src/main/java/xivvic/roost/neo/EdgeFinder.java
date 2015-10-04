package xivvic.roost.neo;


/**
 * This class is used to locate a single link.
 * 
 * @author Reid
 */
public interface EdgeFinder
{
	public EdgeSchema    edgeSchema();
	public NodeFinder    nodeFinder();
}
