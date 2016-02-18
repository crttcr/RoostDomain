package xivvic.roost.neo;

import com.google.auto.value.AutoValue;

/**
 * NodeFinder that serves when the value for the given property is
 * not known.  This is an immutable class which is always invalid.
 * 
 * A NodeFinder needs 3 pieces of information:
 * 
 * Label
 * PropertyName
 * PropertyValue
 * 
 * When the first two are available but the 3rd is unknown, this class
 * will aggregate those two values for when the final information is available.
 * 
 * 
 * @author Reid
 */
@AutoValue
public abstract class NodeFinderEmpty
	implements NodeFinder
{

	public static NodeFinderEmpty create(NodeSchema schema, PropMeta prop)
	{
		return new AutoValue_NodeFinderEmpty(schema, prop);
	}

	@Override
	public abstract NodeSchema schema();

	@Override
	public abstract PropMeta prop();

	@Override
	public final Object value()
	{
		return null;
	}

	@Override
	public boolean isValid()
	{
		return false;
	}

}
