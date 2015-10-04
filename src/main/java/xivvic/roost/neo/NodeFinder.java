package xivvic.roost.neo;

/**
 * This class is used to locate a single node.
 * 
 * 
 * @author Reid
 *
 */
public interface NodeFinder
{
	public NodeSchema    schema();
	public PropMeta        prop();
	public Object         value();

	public boolean      isValid();
}
