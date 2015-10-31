package xivvic.roost.neo.task;

import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import xivvic.command.Command;
import xivvic.command.CommandResult;
import xivvic.neotest.program.RoostRelType;
import xivvic.roost.neo.EdgeSchema;
import xivvic.roost.neo.NodeFinder;

public class Link
	extends NeoCommandHandler
{
	public Link(GraphDatabaseService db, Command command)
	{
		super(db, command);
	}
		

	@Override
	public CommandResult call() throws Exception
	{
		Map<String, Object> data = cmd.properties();
		
		if (data == null)
		{
			String           msg = String.format("Command has no property map [%s].", cmd);
			CommandResult result = CommandResult.failure(cmd.id(), msg);
			return result;
		}
		
		NodeFinder  finder_one = (NodeFinder)  data.get(NeoTaskInfo.NODE_ONE_LOCATOR);
		NodeFinder  finder_two = (NodeFinder)  data.get(NeoTaskInfo.NODE_TWO_LOCATOR);
		EdgeSchema      schema = (EdgeSchema)  data.get(NeoTaskInfo.RELATIONSHIP_SCHEMA);

		String key_one   = finder_one.prop().key();
		String key_two   = finder_two.prop().key();

		Object value_one = data.get(key_one);
		Object value_two = data.get(key_two);

		RoostRelType   rel_type = schema.type();
		
		try (Transaction tx = db.beginTx())
		{
			Label label_one = finder_one.schema().type();
			Label label_two = finder_two.schema().type();

			Node a = db.findNode(label_one, key_one, value_one);
			Node b = db.findNode(label_two, key_two, value_two);
			if (a == null)
			{
				String           msg = String.format("Unable to find node of type [%s] with [%s] -> [%s] not found.", label_one, key_one, value_one);
				CommandResult result = CommandResult.failure(cmd.id(), msg);
				tx.success();
				return result;
			}
		
			if (b == null)
			{
				String           msg = String.format("Unable to find node of type [%s] with [%s] -> [%s] not found.", label_two, key_two, value_two);
				CommandResult result = CommandResult.failure(cmd.id(), msg);
				tx.success();
				return result;
			}

			Relationship rel = a.createRelationshipTo(b, rel_type);
			if (rel == null)
			{
				String          left = key_one + ":" + value_one;
				String         right = key_two + ":" + value_two;
				String           msg = String.format("Unable to find relationship for (%s) -- [:%s] -- (%s).", left, rel_type, right);
				CommandResult result = CommandResult.failure(cmd.id(), msg);
				LOG.warn(msg);
				tx.success();
				return result;
			}
			
			// Create all properties for the link
			//
			int count = SetPropertiesOnLink(rel, schema, data);
			
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
