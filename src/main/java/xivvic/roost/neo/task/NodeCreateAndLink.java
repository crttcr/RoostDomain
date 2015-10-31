package xivvic.roost.neo.task;

import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

import xivvic.command.Command;
import xivvic.command.CommandResult;
import xivvic.roost.neo.EdgeSchema;
import xivvic.roost.neo.NodeFinder;
import xivvic.roost.neo.NodeSchema;

/**
 * This CommandHandler creates a node and links it to an existing node.
 * 
 * The following information is expected to be available from the Command's data container:
 * 
 * NeoTaskInfo.NODE_ONE_SCHEMA     -- Used to create the new node
 * NeoTaskInfo.NODE_TWO_LOCATOR    -- Used to find the node link with the new node
 * NeoTaskInfo.RELATIONSHIP_SCHEMA -- Used to define the link characteristics
 * 
 * @author Reid
 *
 */
public class NodeCreateAndLink
extends NeoCommandHandler
{
	public NodeCreateAndLink(GraphDatabaseService db, Command command)
	{
		super(db, command);
	}
	
	@Override
	public CommandResult call() throws Exception
	{
		Map<String, Object> data = cmd.properties();
		
		NodeSchema  node_1_schema = (NodeSchema) data.get(NeoTaskInfo.NODE_ONE_SCHEMA);
		NodeFinder     node_2_loc = (NodeFinder) data.get(NeoTaskInfo.NODE_TWO_LOCATOR);
		EdgeSchema         schema = (EdgeSchema) data.get(NeoTaskInfo.RELATIONSHIP_SCHEMA);
		
		if (node_1_schema == null)
		{
			String msg = String.format("Cannout CREATE_AND_LINK Node with null schema information on first node");
			LOG.warn(msg);
			CommandResult result = CommandResult.failure(cmd.id(), msg);
			return result;
		}

		if (node_2_loc == null)
		{
			String msg = String.format("Cannout CREATE_AND_LINK Node with null schema information on second node");
			LOG.warn(msg);
			CommandResult result = CommandResult.failure(cmd.id(), msg);
			return result;
		}

		try (Transaction tx = db.beginTx())
		{
			Label  nodeTwoLabel = node_2_loc.schema().type();
			String   nodeTwoKey = node_2_loc.prop().key();
			String nodeTwoValue = (String) data.get(nodeTwoKey);
			
			Node node2 = db.findNode(nodeTwoLabel, nodeTwoKey, nodeTwoValue);
			if (node2 == null)
			{
				String msg = String.format("Node(:%s) with %s=%s not found. Cannot link. Abort.", nodeTwoLabel, nodeTwoKey, nodeTwoValue);
				LOG.warn(msg);
				CommandResult result = CommandResult.failure(cmd.id(), msg);
				tx.success();
				return result;
			}
			
			Label  label_one = node_1_schema.type();
			Node node1 = db.createNode(label_one);
			if (node1 == null)
			{
				String           msg = String.format("Failed to create node of type %s",   label_one);
				CommandResult result = CommandResult.failure(cmd.id(), msg);
				tx.success();
				LOG.warn(msg);
				return result;
			}
			
				
			RelationshipType r_type = schema.type();
			Relationship rship = node1.createRelationshipTo(node2, r_type);
			if (rship == null)
			{
				String           msg = String.format("Failed to create node of type %s",   label_one);
				CommandResult result = CommandResult.failure(cmd.id(), msg);
				tx.success();
				LOG.warn(msg);
				return result;
			}

			// Create all properties for the node
			//
			int count = SetPropertiesOnNode(node1, node_1_schema, data);
			
			if (count == -1)
			{
				tx.failure();
				String           msg = "Unable to set all properties on node";
				CommandResult result = CommandResult.failure(cmd.id(), msg);
				return        result;
			}

			tx.success();
		}
		
		CommandResult result = CommandResult.success(cmd.id());
		return result;
	}


}
