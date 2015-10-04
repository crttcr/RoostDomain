package xivvic.roost.neo;


public class EdgeFinderNoProperty
	implements EdgeFinder
{
	
	private final EdgeSchema schema;
	private final NodeFinder finder;
	
	public EdgeFinderNoProperty(EdgeSchema schema, NodeFinder finder)
	{
		if (schema == null)
		{
			throw new IllegalArgumentException("Valid schema required in construtor");
		}
			
		if (finder == null)
		{
			throw new IllegalArgumentException("Valid finder required in construtor");
		}
			
		this.schema = schema;
		this.finder = finder;
	}

	@Override
	public EdgeSchema edgeSchema()
	{
		return schema;
	}

	@Override
	public NodeFinder nodeFinder()
	{
		return finder;
	}

}
