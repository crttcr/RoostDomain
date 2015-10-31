package xivvic.roost.neo.task;

import java.util.Map;
import java.util.function.Consumer;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import xivvic.command.Command;
import xivvic.command.CommandResult;
import xivvic.roost.neo.NodeFinder;

public class NodeDelete
	extends NeoCommandHandler
{
	public NodeDelete(GraphDatabaseService db, Command command)
	{
		super(db, command);
	}
	
	@Override
	public CommandResult call() throws Exception
	{
		Map<String, Object> data = cmd.properties();
		
		NodeFinder  finder = (NodeFinder)  data.get(NeoTaskInfo.NODE_ONE_LOCATOR);

		if (finder == null)
		{
			String msg = String.format("Cannout delete Node with null label");
			LOG.warn(msg);
			CommandResult result = CommandResult.failure(cmd.id(), msg);
			return result;
		}

		String   key = finder.prop().key();
		Object value = data.get(key);
		
		if (value == null)
		{
			String msg = String.format("Cannout delete Node with null value");
			LOG.warn(msg);
			CommandResult result = CommandResult.failure(cmd.id(), msg);
			return result;
		}
		
		try (Transaction tx = db.beginTx())
		{
			Label label = finder.schema().type();
			Node node = db.findNode(label, key, value);
			if (node == null)
			{
				String           msg = String.format("Failed to locate node of type %s with [%s] -> [%s]", label, key, value);
				CommandResult result = CommandResult.failure(cmd.id(), msg);
				tx.success();
				LOG.warn(msg);
				return result;
			}

			// Delete all relationships for this node
			//
			Consumer<Relationship> delete = r -> r.delete();
			node.getRelationships().forEach(delete);
			
			// Then delete the node
			//
			node.delete();

			tx.success();
		}
		
		CommandResult result = CommandResult.success(cmd.id());
		return result;
	}


}
