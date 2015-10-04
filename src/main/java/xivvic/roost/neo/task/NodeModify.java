package xivvic.roost.neo.task;

import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import xivvic.command.Command;
import xivvic.command.CommandResult;
import xivvic.roost.neo.NodeFinder;
import xivvic.roost.neo.NodeSchema;

public class NodeModify
extends NeoCommandHandler
{
	public NodeModify(GraphDatabaseService db, Command command)
	{
		super(db, command);
	}
	
	@Override
	public CommandResult call() throws Exception
	{
		Map<String, Object> data = cmd.properties();
		NodeFinder        finder = (NodeFinder)  data.get(NeoTaskInfo.NODE_ONE_LOCATOR);

		if (finder == null)
		{
			String msg = String.format("Cannout modify Node with null locator");
			LOG.warning(msg);
			CommandResult result = CommandResult.failure(cmd.id(), msg);
			return result;
		}

		Label              label = finder.schema().type();
		String               key = finder.prop().key();
		Object             value = data.get(key);

		if (value == null)
		{
			String msg = String.format("Cannout modify Node with null value for property [%s]", key);
			LOG.warning(msg);
			CommandResult result = CommandResult.failure(cmd.id(), msg);
			return result;
		}

		try (Transaction tx = db.beginTx())
		{
			Node node = db.findNode(label, key, value);
			if (node == null)
			{
				String           msg = String.format("Failed to find node of type %s with property %s=%s", label, key, value);
				CommandResult result = CommandResult.failure(cmd.id(), msg);
				tx.success();
				LOG.warning(msg);
				return result;
			}

			// Set all properties for the node (some may already exist, this will overwrite)
			//
			NodeSchema schema = finder.schema();
			int         count = SetPropertiesOnNode(node, schema, data);
			
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
