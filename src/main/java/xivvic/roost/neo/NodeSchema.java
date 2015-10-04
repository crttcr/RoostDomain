package xivvic.roost.neo;

import java.util.List;

import xivvic.neotest.program.RoostNodeType;
/**
 * This class is used to define a particular type of node.
 * 
 * 
 * @author Reid
 *
 */
public interface NodeSchema
{
	public RoostNodeType type();
	public List<String> propertyNames();

	public List<String> propertyKeys();

	/**
	 * Returns the property definition associated with the provided key.
	 * 
	 * Throws IllegalArgumentExceptin if the key is null or if there is 
	 * no property associated with the given key.
	 * 
	 * @param key
	 * @return the definition for the property.
	 */
	public PropMeta property(String key);
	
	/**
	 * Returns the property definitions for which the given predicate evaluates to true.
	 * 
	 * Returns all properties if the predicate is null.
	 * 
	 * @param predicate the test to apply which will serve to filter the returned results
	 * 
	 * @return all the predicates that evaluate to true, or all properties if predicate is null
	 */
	public List<PropMeta> properties(PropPredicate predicate);

}
