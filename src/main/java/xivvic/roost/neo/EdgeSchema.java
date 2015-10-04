package xivvic.roost.neo;

import java.util.List;

import xivvic.neotest.program.RoostRelType;

/**
 * This class is used to define a particular type of Edge.
 * 
 * 
 * @author Reid
 *
 */
public interface EdgeSchema
{
	RoostRelType type();
	List<String> propertyNames();
	List<String> propertyKeys();
	List<PropMeta> properties(PropPredicate test);
	PropMeta property(String key);

}
