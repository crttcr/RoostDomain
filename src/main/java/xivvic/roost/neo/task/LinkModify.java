package xivvic.roost.neo.task;

import java.util.Iterator;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import xivvic.command.Command;
import xivvic.command.CommandResult;
import xivvic.roost.neo.EdgeSchema;
import xivvic.roost.neo.NodeFinder;

public class LinkModify
extends NeoCommandHandler
{
	public LinkModify(GraphDatabaseService db, Command command)
	{
		super(db, command);
	}
	
	@Override
	public CommandResult call() throws Exception
	{
		Map<String, Object> data = cmd.properties();

		NodeFinder finder_one = (NodeFinder) data.get(NeoTaskInfo.NODE_ONE_LOCATOR);
		NodeFinder finder_two = (NodeFinder) data.get(NeoTaskInfo.NODE_TWO_LOCATOR);

		if (finder_one == null)
		{
			String msg = String.format("Cannout modify link without locator for first node");
			LOG.warn(msg);
			CommandResult result = CommandResult.failure(cmd.id(), msg);
			return result;
		}

		if (finder_two == null)
		{
			String msg = String.format("Cannout modify link without locator for second node");
			LOG.warn(msg);
			CommandResult result = CommandResult.failure(cmd.id(), msg);
			return result;
		}
		
		Label       label_one = finder_one.schema().type();
		Label       label_two = finder_two.schema().type();
		
		String        key_one = finder_one.prop().key();
		String        key_two = finder_two.prop().key();
		
		String      nodeOneValue = (String)  data.get(key_one);
		String      nodeTwoValue = (String)  data.get(key_two);

		EdgeSchema  schema = (EdgeSchema) data.get(NeoTaskInfo.RELATIONSHIP_SCHEMA);


		try (Transaction tx = db.beginTx())
		{
			Node node = db.findNode(label_one, key_one, nodeOneValue);
			if (node == null)
			{
				String           msg = String.format("Failed to find node of type %s with property %s=%s", label_one, key_one, nodeOneValue);
				CommandResult result = CommandResult.failure(cmd.id(), msg);
				tx.success();
				LOG.warn(msg);
				return result;
			}

			Iterator<Relationship> iter = node.getRelationships(schema.type()).iterator();
			Relationship          rship = null;
			
			while (iter.hasNext())
			{
				Relationship r_current = iter.next();
				Node             other = r_current.getOtherNode(node);
				if (! other.hasLabel(label_two))
					continue;

				Object other_value = other.getProperty(key_two);
				if (other_value.equals(nodeTwoValue))
				{
					rship = r_current;
					break;
				}
			}
			
			if (rship == null)
			{
				String           msg = String.format("Failed to specified relationship of type %s from node with %s=%s", schema.type(), key_one, nodeOneValue);
				CommandResult result = CommandResult.failure(cmd.id(), msg);
				tx.success();
				LOG.warn(msg);
				return result;
			}

			// Set all properties for the relationship
			//
			int count = SetPropertiesOnLink(rship, schema, data);
			
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
