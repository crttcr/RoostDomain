package xivvic.roost.neo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import xivvic.neotest.program.RoostNodeType;
/**
 * This class is used to define a particular type of node.
 * 
 * 
 * @author Reid
 *
 */
public class NodeMetaImmutable
	implements NodeSchema
{
	private final RoostNodeType type;
	private final Map<String, PropMeta> properties;
	
   public NodeMetaImmutable(RoostNodeType type, List<PropMeta> properties)
   {
   	if (type == null)
   		throw new IllegalArgumentException("Type required in constructor for node schema");
   	
   	if (properties == null)
   		throw new IllegalArgumentException("Property list required in constructor for node schema");
   	
   	this.type       = type;
   	this.properties = new HashMap<String, PropMeta>();
   	
   	for (PropMeta p : properties)
   	{
   		this.properties.put(p.key(), p);
   	}
   }

	public RoostNodeType type()
	{
		return type;
	};
	
	public List<String> propertyNames()
	{
		return properties.values().stream().map(p -> p.name()).collect(Collectors.toList());
	}

	public List<String> propertyKeys()
	{
		return properties.values().stream().map(p -> p.key()).collect(Collectors.toList());
	}

	public List<PropMeta> properties(PropPredicate test)
	{
		if (test == null)
			return new ArrayList<PropMeta>(properties.values());
		
		return properties.values().stream().filter(test).collect(Collectors.toList());
	}


	@Override
	public PropMeta property(String key)
	{
		if (key ==null)
		{
			throw new IllegalArgumentException("Asking for a null property makes no sense.");
		}
		
		PropMeta p = properties.get(key);
		if (p == null)
		{
			String msg = String.format("Unable to locate property definition for key [%s].", key);
			throw new IllegalArgumentException(msg);
		}
		
		return p;
	}

}
