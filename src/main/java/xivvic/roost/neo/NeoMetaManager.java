package xivvic.roost.neo;

import java.util.HashMap;
import java.util.Map;

import xivvic.neotest.program.RoostNodeType;

public class NeoMetaManager
{
	private static NeoMetaManager INSTANCE = new NeoMetaManager();
	
	public static NeoMetaManager getInstance()
	{
		return INSTANCE;
	}
	
	private Map<RoostNodeType, NodeSchema> nodeTypeMap = new HashMap<>();
	
	public NodeSchema getMetadata(RoostNodeType type)
	{
		return nodeTypeMap.get(type);
		
	}
}
