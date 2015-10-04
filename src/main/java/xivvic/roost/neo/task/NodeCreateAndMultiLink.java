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
import xivvic.command.CommandStatus;
import xivvic.roost.neo.LinkSpec;
import xivvic.roost.neo.NodeFinder;
import xivvic.roost.neo.NodeSchema;
import xivvic.roost.neo.PropMeta;

public class NodeCreateAndMultiLink
extends NeoCommandHandler
{
	private String error_message = null;
	
	public NodeCreateAndMultiLink(GraphDatabaseService db, Command command)
	{
		super(db, command);
	}
	
	@Override
	public CommandResult call() throws Exception
	{
		Map<String, Object> data = cmd.properties();
		
		NodeSchema schema = (NodeSchema) data.get(NeoTaskInfo.NODE_ONE_SCHEMA);
		if (schema == null)
		{
			String msg = String.format("Schema for the creating a node was not available from processed parameters.");
			LOG.warning(msg);
			cmd.setStatus(CommandStatus.FAILED);
			CommandResult result = CommandResult.failure(cmd.id(), msg);
			return result;
		}
		
		@SuppressWarnings("unchecked")
		Map<RelationshipType, LinkSpec> link_map = (Map<RelationshipType, LinkSpec>) data.get(NeoTaskInfo.EDGE_MAP);
		if (link_map == null)
		{
			String msg = String.format("Map of link specifications is required to create a user");
			LOG.warning(msg);
			cmd.setStatus(CommandStatus.FAILED);
			CommandResult result = CommandResult.failure(cmd.id(), msg);
			return result;
		}

		try (Transaction tx = db.beginTx())
		{
			
			// First create the node.
			//
			Node node = db.createNode(schema.type());
			if (node == null)
			{
				String           msg = String.format("Failed to create node of type %s", schema.type());
				CommandResult result = CommandResult.failure(cmd.id(), msg);
				tx.failure();
				LOG.warning(msg);
				return result;
			}
			
			// Create all properties for the node
			//
			int count = SetPropertiesOnNode(node, schema, data);
			
			if (count == -1)
			{
				tx.failure();
				String           msg = "NodeCreateAndMultiLink: Unable to set all properties on node";
				CommandResult result = CommandResult.failure(cmd.id(), msg);
				return        result;
			}

			// Then link to all its other nodes.
			//
			for (RelationshipType r_type : link_map.keySet())
			{
				LinkSpec  link_spec = link_map.get(r_type);
				Node     other_node = acquireOtherNode(link_spec);
				if (other_node == null)
				{
					CommandResult result = CommandResult.failure(cmd.id(), error_message);
					tx.failure();
					return result;
				}
				
				Relationship rship = node.createRelationshipTo(other_node, r_type);
				if (rship == null)
				{
					NodeFinder    finder = link_spec.finder();
					Label          label = finder.schema().type();
					String          prop = finder.prop().key();
					Object         value = finder.value();
					String           fmt = "Failed to create relationship to node of type %s with prop[%s] -> [%s]";
					String           msg = String.format(fmt,  label, prop, value);
					CommandResult result = CommandResult.failure(cmd.id(), msg);
					tx.failure();
					LOG.warning(msg);
					return result;
				}
			}


			tx.success();
		}
		
		CommandResult result = CommandResult.success(cmd.id());
		return result;
	}


	private Node acquireOtherNode(LinkSpec link_spec)
	{
		RelationshipType   r_type = link_spec.schema().type();
		NodeFinder         finder = link_spec.finder();
		Label    other_node_label = finder.schema().type();
		PropMeta        prop_meta = finder.prop();
		String     other_node_key = prop_meta.key();
		Object   other_node_value = finder.value();

		if (! finder.isValid())
		{
			error_message = String.format("Cannout link [:%s] to Node with label with invalid finder", r_type);
			LOG.warning(error_message);
			return null;
		}
	
		Node other_node = db.findNode(other_node_label, other_node_key, other_node_value);
		if (other_node == null)
		{
			String fmt = "Node(:%s) with %s=%s not found. Cannot link. Abort.";
			String msg = String.format(fmt, other_node_label, other_node_key, other_node_value);
			LOG.warning(msg);
			return null;
		}
		
		return other_node;
	}
}
