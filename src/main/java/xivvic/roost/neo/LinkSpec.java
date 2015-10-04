package xivvic.roost.neo;

import com.google.auto.value.AutoValue;

/**
 * A LinkSpec provides the information required to create a link.
 * 
 * the finder() method returns the information required to find the
 * node at the far end of the link. It is assumed that the user of this
 * class already has a reference to the node from which to make the link.
 * 
 * The schema() method returns the scheme details that can be used 
 * to create a link and add any available properties.
 * 
 * @author Reid
 */
@AutoValue
public abstract class LinkSpec
{
	public abstract EdgeSchema schema();
	public abstract NodeFinder finder();
	
   public static LinkSpec create(EdgeSchema schema, NodeFinder finder)
   {
   	return new AutoValue_LinkSpec(schema, finder);
	}
}
