package xivvic.roost.neo;

import com.google.auto.value.AutoValue;

/**
 * NodeFinder that serves as when the value for the given property is
 * not known.  This is an immutable class which is always invalid.
 * 
 * @author Reid
 */
@AutoValue
public abstract class NodeFinderFull
	implements NodeFinder
{

	public NodeFinderFull create(NodeSchema schema, PropMeta prop, Object value)
	{

// Not necessary because AutoValue does this check.
//  
//		if (value == null)
//		{
//			throw new IllegalArgumentException("Cannot have a null property value. Nonsensical to use for finding a node.");
//		}
//
		return new AutoValue_NodeFinderFull(schema, prop, value);
	}

	public static NodeFinderFull create(NodeFinder finder, Object value)
	{
		return new AutoValue_NodeFinderFull(finder.schema(), finder.prop(), value);
	}

	@Override
	public abstract NodeSchema schema();

	@Override
	public abstract PropMeta prop();

	@Override
	public abstract Object value(); 

	@Override
	public final boolean isValid()
	{
		return true;
	}

}
