package xivvic.roost.neo.task;

import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import xivvic.command.Command;
import xivvic.command.CommandResult;
import xivvic.roost.neo.NodeSchema;

public class NodeCreate
extends NeoCommandHandler
{
	public NodeCreate(GraphDatabaseService db, Command command)
	{
		super(db, command);
	}
	
	@Override
	public CommandResult call() throws Exception
	{
		Map<String, Object> data = cmd.properties();
		NodeSchema          schema = (NodeSchema)  data.get(NeoTaskInfo.NODE_ONE_SCHEMA);

		if (schema == null)
		{
			String msg = String.format("Cannout create Node without schema: [%s]", NeoTaskInfo.NODE_ONE_SCHEMA);
			LOG.warning(msg);
			CommandResult result = CommandResult.failure(cmd.id(), msg);
			return result;
		}

		Label type = schema.type();
		try (Transaction tx = db.beginTx())
		{
			Node node = db.createNode(type);
			if (node == null)
			{
				String           msg = String.format("Failed to create node of type [%s]", type);
				CommandResult result = CommandResult.failure(cmd.id(), msg);
				tx.success();
				LOG.warning(msg);
				return result;
			}

			// Create all properties for the node
			//
			int count = SetPropertiesOnNode(node, schema, data);
			
			if (count == -1)
			{
				tx.failure();
				String           msg = "Unable to set all properties on node";
				CommandResult result = CommandResult.failure(cmd.id(), msg);
				return        result;
			}

			tx.success();
			String  msg = String.format("Created node [%s] with [%d] properties", node, count);
			LOG.info(msg);
		}
		
		CommandResult result = CommandResult.success(cmd.id());
		return result;
	}


}
