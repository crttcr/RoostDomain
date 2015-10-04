package xivvic.roost.neo.task;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

import xivvic.command.Command;
import xivvic.command.CommandHandler;
import xivvic.command.CommandResult;
import xivvic.roost.neo.EdgeSchema;
import xivvic.roost.neo.NodeFinder;

public class Unlink
implements CommandHandler
{
	private final static Logger       LOG = Logger.getLogger(Unlink.class.getName());
	private final GraphDatabaseService db;
	private final Command             cmd;

	public Unlink(GraphDatabaseService db, Command command)
	{
		this.db  = db;
		this.cmd = command;
	}
	

	@Override
	public CommandResult call() throws Exception
	{
		Map<String, Object> data = cmd.properties();
		
		NodeFinder finder_one  = (NodeFinder)  data.get(NeoTaskInfo.NODE_ONE_LOCATOR);
		NodeFinder finder_two  = (NodeFinder)  data.get(NeoTaskInfo.NODE_TWO_LOCATOR);

		if (finder_one == null || finder_two == null)
		{
			String           msg = String.format("Unlink: Finders for the two nodes cannot be null");
			CommandResult result = CommandResult.failure(cmd.id(), msg);
			return result;
		}

		if (! finder_one.isValid() || ! finder_two.isValid())
		{
			String           msg = String.format("Unlink: Finders for the two nodes must be valid (i.e. have Values)");
			CommandResult result = CommandResult.failure(cmd.id(), msg);
			return result;
		}

		Label  label_one = finder_one.schema().type();
		Label  label_two = finder_two.schema().type();

		String   key_one = finder_one.prop().key();
		String   key_two = finder_two.prop().key();

		Object value_one = data.get(key_one);
		Object value_two = data.get(key_two);

		if (value_one == null || value_two == null)
		{
			String           msg = String.format("Unlink: unable to acquire values for nodes");
			CommandResult result = CommandResult.failure(cmd.id(), msg);
			return result;
		}


		EdgeSchema  link_schema = (EdgeSchema) data.get(NeoTaskInfo.RELATIONSHIP_SCHEMA);
		RelationshipType r_type = link_schema.type();

		try (Transaction tx = db.beginTx())
		{
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

			Iterable<Relationship>  rships = a.getRelationships(r_type);
			Iterator<Relationship>      it = rships.iterator();
			while (it.hasNext())
			{
				Relationship r = it.next();
				Node     other = r.getOtherNode(a);
				if (b.getId() == other.getId())
				{
					// FOUND the relationship we wanted, delete it and exit
					//
					r.delete();
					CommandResult result = CommandResult.success(cmd.id());
					tx.success();
					return result;
				}
			}
			tx.success();
		}

		// Completed iterating through relationships and not able to find the one we
		// wanted to delete.  So return failure message.
		//
		String          left = key_one + ":" + value_one;
		String         right = key_two + ":" + value_two;
		String           msg = String.format("Unable to find relationship for (%s) -- [:%s] -- (%s).", left, r_type, right);
		CommandResult result = CommandResult.failure(cmd.id(), msg);
		LOG.warning(msg);
		return result;
	}
}
