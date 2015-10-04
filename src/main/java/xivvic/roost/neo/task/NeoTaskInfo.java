package xivvic.roost.neo.task;

/**
 * This interface contains constants that represent keys in a Command's 
 * property bag that are available to guide the execution of the command
 * against the Neo4j back end.
 * 
 * @author Reid
 *
 */
public interface NeoTaskInfo
{
	public static String EDGE_MAP                   = "edge.map";
	public static String NODE_ONE_SCHEMA            = "node.1.schema";
	public static String NODE_ONE_LOCATOR           = "node.1.locator";
	public static String NODE_TWO_LOCATOR           = "node.2.locator";

	public static String RELATIONSHIP_SCHEMA        = "rship.schema";

}
