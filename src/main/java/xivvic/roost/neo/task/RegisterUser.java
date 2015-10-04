package xivvic.roost.neo.task;

import java.util.Map;
import java.util.logging.Logger;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

import xivvic.command.Command;
import xivvic.command.CommandHandler;
import xivvic.command.CommandResult;
import xivvic.command.CommandStatus;
import xivvic.neotest.program.RoostNodeType;
import xivvic.neotest.program.RoostRelType;
import xivvic.roost.domain.User;
import xivvic.roost.neo.EdgeFinder;
import xivvic.roost.neo.NodeFinder;
import xivvic.roost.neo.NodeSchema;
import xivvic.roost.neo.PropMeta;
import xivvic.util.identity.RandomString;

/*
 * Single command handler that performs multiple commands within a single transaction.
 * There are many potential errors which will result in a CommandResult in the error state
 * with an explanation of the problems.
 * 
 * (create user)
 *   (set properties)
 * (link to person)
 * (link to group)
 * 
 */
public class RegisterUser
	extends NeoCommandHandler
	implements CommandHandler
{
	private final static Logger       LOG = Logger.getLogger(RegisterUser.class.getName());

	public RegisterUser(GraphDatabaseService db, Command command)
	{
		super(db, command);
	}
	
	/**
	 * This command registers a user by creating a User node with the specified properties
	 * and creating relationships to the Group and Person for this user.  
	 * 
	 * There are lots of Application level checks that are performed to ensure that registration
	 * will be successful:
	 * 
	 * Check if a user exists with the same username
	 * Check if a user exists with the same email
	 * Check if a user exists with the same id
	 * Check that information is available to establish the relationships
	 * Check if the specified Group exists
	 * Check if the specified Person exists
	 * 
	 * 
	 */
	@Override
	public CommandResult call()
	{
		Map<String, Object> data = cmd.properties();
		
		NodeSchema u_schema = (NodeSchema) data.get(NeoTaskInfo.NODE_ONE_SCHEMA);
		if (u_schema == null)
		{
			String msg = String.format("Schema for the creating a user node was not available");
			LOG.warning(msg);
			cmd.setStatus(CommandStatus.FAILED);
			CommandResult result = CommandResult.failure(cmd.id(), msg);
			return result;
		}
		
		@SuppressWarnings("unchecked")
		Map<RelationshipType, EdgeFinder> edge_map = (Map<RelationshipType, EdgeFinder>) data.get(NeoTaskInfo.EDGE_MAP);
		if (edge_map == null)
		{
			String msg = String.format("Map of link specifications is required to create a user");
			LOG.warning(msg);
			cmd.setStatus(CommandStatus.FAILED);
			CommandResult result = CommandResult.failure(cmd.id(), msg);
			return result;
		}

		EdgeFinder g_edge = edge_map.get(RoostRelType.USER_GROUP);
		EdgeFinder p_edge = edge_map.get(RoostRelType.USER_PERSON);

		if (g_edge == null)
		{
			String msg = String.format("Information for establishing group->user relationship is missing.");
			LOG.warning(msg);
			cmd.setStatus(CommandStatus.FAILED);
			CommandResult result = CommandResult.failure(cmd.id(), msg);
			return result;
		}
		
		if (p_edge == null)
		{
			String msg = String.format("Information for establishing user->person relationship is missing.");
			LOG.warning(msg);
			cmd.setStatus(CommandStatus.FAILED);
			CommandResult result = CommandResult.failure(cmd.id(), msg);
			return result;
		}
		
		try (Transaction tx = db.beginTx() )
		{
			NodeFinder g_finder = g_edge.nodeFinder();
			NodeFinder p_finder = p_edge.nodeFinder();

			Node g_node = db.findNode(g_finder.schema().type(), g_finder.prop().key(), g_finder.value());
			Node p_node = db.findNode(p_finder.schema().type(), p_finder.prop().key(), p_finder.value());
			
			// If we can't find the specified Person or Group node, then fail this registration attempt.
			// We can't have a floating user out there.
			//
			// We need to link the (User)-[r:USER_GROUP]->(Group)
			//
			if (g_node == null)
			{
				String fmt = "RegisterUser: Failed to find group: [%s] -> [%s]";
				String msg = String.format(fmt, g_finder.prop().name(), g_finder.value());
				LOG.warning(msg);
				tx.success();
				cmd.setStatus(CommandStatus.FAILED);
				CommandResult result = CommandResult.failure(cmd.id(), msg);
				return result;
			}

			// If we can't find the specified Person node, then fail this registration attempt.
			//
			// We need this link:  (User)--[r:USER_PERSON]->(Person)
			//
			if (p_node == null)
			{
				String fmt = "RegisterUser: Failed to find group: [%s] -> [%s]";
				String msg = String.format(fmt, p_finder.prop().name(), p_finder.value());
				LOG.warning(msg);
				tx.success();
				cmd.setStatus(CommandStatus.FAILED);
				CommandResult result = CommandResult.failure(cmd.id(), msg);
				return result;
			}


			// Every User needs an ID.  If one is not provided in the data associated with this command
			// then create one and add it to the data.
			//
			PropMeta  uid_meta = u_schema.property(User.PROP_ID);
			Object   uid_value = data.get(uid_meta.key());
			if (uid_value == null)
			{
				RandomString rs = new RandomString(16);
				data.put(uid_meta.key(), rs);
			}
			
			// Ensure that the User node's unique properties aren't already taken by nodes in the database
			//
			String[] unique_props = RoostNodeType.USER.uniqueProperties();
			for (String unique_prop : unique_props)
			{
				String  value = (String) data.get(unique_prop);
				Node existing = db.findNode(u_schema.type(), unique_prop, value);
				if (existing != null)
				{
					String msg = String.format("User with property [%s] -> [%s] already exists. This property must be unique.", unique_prop, value);
					LOG.warning(msg);
					tx.success();
					cmd.setStatus(CommandStatus.FAILED);
					CommandResult result = CommandResult.failure(cmd.id(), msg);
					return result;
				}
			}
			
			// Create USER node and set all properties
			//
			Node  node = db.createNode(u_schema.type());
			int  count = SetPropertiesOnNode(node, u_schema, data);
			
			if (count == -1)
			{
				tx.failure();
				String           msg = "Unable to set all properties on node";
				CommandResult result = CommandResult.failure(cmd.id(), msg);
				return        result;
			}
			
			// Create relationships
			//
			Relationship u2g = node.createRelationshipTo(g_node, g_edge.edgeSchema().type());
			if (u2g == null)
			{
				String msg = String.format("Failed to link user[%s] with group [%s]", node, g_node);
				LOG.warning(msg);
				tx.success();
				cmd.setStatus(CommandStatus.FAILED);
				node.delete();
				CommandResult result = CommandResult.failure(cmd.id(), msg);
				return result;
			}

			Relationship u2p = node.createRelationshipTo(p_node, p_edge.edgeSchema().type());
			if (u2p == null)
			{
				String msg = String.format("Failed to link user[%s] with person [%s]", node, p_node);
				LOG.warning(msg);
				tx.success();
				cmd.setStatus(CommandStatus.FAILED);
				node.delete();
				CommandResult result = CommandResult.failure(cmd.id(), msg);
				return result;
			}

			tx.success();
		}
		
		CommandResult result = CommandResult.success(cmd.id());
		return result;
	}
	
}
