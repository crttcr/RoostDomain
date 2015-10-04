package xivvic.roost.console;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

import org.neo4j.graphdb.RelationshipType;

import xivvic.command.Command;
import xivvic.command.CommandBase;
import xivvic.command.CommandProcessor;
import xivvic.command.CommandSource;
import xivvic.command.CommandStatus;
import xivvic.console.action.Action;
import xivvic.console.action.ActionBase;
import xivvic.console.action.ActionMetadata;
import xivvic.console.input.InputProcessor;
import xivvic.console.input.InputProcessorNVPairs;
import xivvic.neotest.program.RoostRelType;
import xivvic.roost.app.login.LoginService;
import xivvic.roost.domain.Group;
import xivvic.roost.domain.Person;
import xivvic.roost.domain.User;
import xivvic.roost.neo.EdgeFinder;
import xivvic.roost.neo.EdgeFinderNoProperty;
import xivvic.roost.neo.EdgeSchema;
import xivvic.roost.neo.NodeFinder;
import xivvic.roost.neo.NodeFinderEmpty;
import xivvic.roost.neo.NodeFinderFull;
import xivvic.roost.neo.NodeSchema;
import xivvic.roost.neo.PropMeta;
import xivvic.roost.neo.PropPredicate;
import xivvic.roost.neo.SchemaManager;
import xivvic.roost.neo.task.NeoTaskInfo;
import xivvic.roost.service.ServiceLocator;
import xivvic.roost.service.UserService;
import xivvic.util.identity.RandomString;

/**
 * Builds User actions for the application.
 * 
 * NOTE: (IMPORTANT)
 * Be sure to add this class to the build block in the base classes' static block.
 * @see ActionBuilderBase.
 * 
 * 
 * @author Reid
 *
 */
public class ActionBuilderUser
	extends ActionBuilderBase
{
	private final static Logger           LOG = Logger.getLogger(ActionBuilderUser.class.getName());
	private final static RandomString   idgen = new RandomString(16);
	
	
	public ActionBuilderUser()
	{
		super();
	}

	static Action buildListAction()
	{
		String               name = ActionBuilder.USER_LIST;
		final String          desc = "Lists all users.";
		String              usage = "list";
		ActionMetadata       meta = new ActionMetadata(name, desc, usage, ServiceLocator.SERVICE_USER);
		InputProcessor        aip = InputProcessorNVPairs.DEFAULT;
		Action             action = ActionBuilderBase.buildListAction(meta, aip);
		return action;
	}
	
	static Action buildDeleteAction()
	{
		String               name = ActionBuilder.USER_DELETE;
		String               desc = "Deletes a USER";
		String              usage = "delete id | delete username:MyUserName | delete email:me@someemail.com";
		ActionMetadata       meta = new ActionMetadata(name, desc, usage);
		InputProcessor        aip = InputProcessorNVPairs.DEFAULT;
		Action             action = ActionBuilderBase.buildActionForDeleteCommand(meta, aip);
		
		return action;
	}


	/**
	 * Builds the registerUser() action.  
	 * 
	 * Note registering a user requires information about the user (User node properties)
	 * as well as links to the User's group and the person that the user represents.  These
	 * values will be looked up by the action to ensure they exist.
	 * 
	 * @return
	 */

	static Action buildRegisterAction()
	{
		String               name = ActionBuilder.USER_REGISTER;
		String               desc = "Registers a new user. Required params in name:value list";
		String              usage = "r user_email:reid@foobox.com user_passhash:blobblob user_username:crttcr group_id:bacon person_id:p123";
		ActionMetadata       meta = new ActionMetadata(name, desc, usage);
		Set<String>      required = populateRequiredPropertySet();
		NodeSchema    schema_user = SchemaManager.getInstance().getEntitySchema(User.class);
		Map<String, Object>   map = new HashMap<>();

		map.put(NeoTaskInfo.NODE_ONE_SCHEMA, schema_user);

		populateMapWithEdgeInformation(map);

		Map<String, Supplier<String>> synthetics = populateMapWithSynthetics();
		
		final InputProcessor  aip = new InputProcessorNVPairs(map, required, null, synthetics);
		
		Action action = new ActionBase(name, desc, true)
		{
			@Override
			protected void internal_invoke(Object param)
			{
				Map<String, Object> map = aip.process(param);
				if (map == null)
				{
					String   msg = String.format("Error\nUsage: \n%s", meta.usage());
					System.err.println(msg);
					return;
				}

				boolean bind_ok = bindLinkedNodeSearchProperty(map);
				if (! bind_ok)
				{
					String   msg = String.format("Unable to bind inputs required to link nodes");
					System.err.println(msg);
					return;
				}
				
				CommandProcessor cp = (CommandProcessor) ServiceLocator.locator().get(ServiceLocator.COMMAND_PROCESSOR);
				if (cp == null)
				{
					String msg = String.format("Unable to retrieve command processor from ServiceLocator");
					LOG.warning(msg);
					return;
				}

				CommandSource source = new CommandSource(){};
				String            id = idgen.nextString();
				String        intent = meta.name();
				Command      command = CommandBase.builder().id(id).source(source).intent(intent).properties(map).build();
				CommandStatus status = cp.receive(command);
				
				if (status == CommandStatus.ACKNOWLEDGED)
				{
					String msg = String.format("Command submitted for procssing [%s]", command.toString());
					LOG.info(msg);
				}
				else
				{
					String msg = String.format("Command failed: [%s]", command.toString());
					LOG.warning(msg);
				}
			}
		};
		
		return action;
	}
	
	private static Map<String, Supplier<String>> populateMapWithSynthetics()
	{
		final RandomString            random_string = new RandomString(16);
		Map<String, Supplier<String>> result = new HashMap<>();
		
		Supplier<String> random_string_generator = new Supplier<String>()
		{
			@Override
			public String get()
			{
				return random_string.nextString();
			}
		};

		result.put(User.PROP_ID, random_string_generator);
		result.put(User.PROP_SALT, random_string_generator);
		
		return result;
	}

	// Build the set of strings that need to be found in the input.
	//
	private static Set<String> populateRequiredPropertySet()
	{
		Set<String>        result = new HashSet<>();
		NodeSchema    schema_user = SchemaManager.getInstance().getEntitySchema(User.class);
		PropPredicate        test =  PropPredicate.predicateRequired();

		for (PropMeta pm : schema_user.properties(test))
			result.add(pm.key());

		result.add(Group.PROP_ID);
		result.add(Person.PROP_ID);

		return result;
	}

	// Deal with the links to Person and Group
	//
	private static void populateMapWithEdgeInformation(Map<String, Object> map)
	{
		NodeSchema   schema_person = SchemaManager.getInstance().getEntitySchema(Person.class);
		NodeSchema    schema_group = SchemaManager.getInstance().getEntitySchema(Group.class);
		EdgeSchema e_schema_person = SchemaManager.getInstance().getEdgeSchema(RoostRelType.USER_PERSON);
		EdgeSchema  e_schema_group = SchemaManager.getInstance().getEdgeSchema(RoostRelType.USER_GROUP);
		PropMeta            p_meta = schema_person.property(Person.PROP_ID);
		PropMeta            g_meta = schema_group.property(Group.PROP_ID);
		NodeFinder   finder_person = NodeFinderEmpty.create(schema_person,  p_meta);
		NodeFinder    finder_group = NodeFinderEmpty.create(schema_group, g_meta);

		EdgeFinder       ef_person = new EdgeFinderNoProperty(e_schema_person, finder_person);
		EdgeFinder        ef_group = new EdgeFinderNoProperty(e_schema_group, finder_group);

		Map<RelationshipType, EdgeFinder> links = new HashMap<>();
		
		links.put(ef_person.edgeSchema().type(), ef_person);
		links.put(ef_group.edgeSchema().type(), ef_group);

		map.put(NeoTaskInfo.EDGE_MAP,         links);
	}

	// The binding for the Node's property is not available until the action's input parameter has
	// been processed. This method will look up that bound value in the argument map and provide it to the
	// NodeSpec so that it can be used later to look up the node to link to.
	//
	private static boolean bindLinkedNodeSearchProperty(Map<String, Object> map)
	{
		if (map == null)
			return false;
		
		@SuppressWarnings("unchecked")
		Map<String, EdgeFinder> edges = (Map<String, EdgeFinder>) map.get(NeoTaskInfo.EDGE_MAP);
		if (edges == null)
		{
			String   msg = String.format("Error: List of edges to create not available. Need key[%s]", NeoTaskInfo.EDGE_MAP);
			System.err.println(msg);
			return false;
		}
		
		Consumer<Entry<String, EdgeFinder>> bind = (entry) -> 
		{ 
			EdgeFinder finder_edge = entry.getValue();
			NodeFinder finder_node = finder_edge.nodeFinder();
			PropMeta          prop = finder_node.prop();
			Object           value = map.get(prop.key());
			NodeFinder     revised = NodeFinderFull.create(finder_node, value);
			EdgeFinder          ef = new EdgeFinderNoProperty(finder_edge.edgeSchema(), revised);
			
			entry.setValue(ef);
		};

		edges.entrySet().forEach(bind);
		
		return true;
	}
	
	
	static Action buildLogoutAction()
	{
		String desc = "Logs out the current user";
		
		Action action = new ActionBase(LOGOUT, desc, false)
		{
			@Override
			protected void internal_invoke(Object param)
			{
				ProgramState.setSessionKey(null);
				this.disable();

				Action login = ActionRegistry.get(LOGIN);
				login.enable();

				String msg = String.format("Goodbye.\nSession key has been cleared.");
				System.out.println(msg);
			}
			
		};
		
		return action;
	}
				
	static Action buildLoginAction()
	{
		String desc = "Attempts to log a user into the application";
		
		Action action = new ActionBase(LOGIN, desc, true)
		{
			@Override
			protected void internal_invoke(Object param)
			{
				if (param == null)
				{
					String name = this.getClass().getCanonicalName();
					String  msg = String.format("Null parameter to action [%s]", name);
					LOG.warning(msg);
					return;
				}
				
				if (!(param instanceof String))
					return;
				
				String string = (String) param;
				String   trim = string.trim();
				
				if (trim.length() < 3)
				{
					String msg = "Parameter [" + string + "] too short for login action.";
					LOG.warning(msg);
					return;
				}
				
				if (! trim.contains(" "))
				{
					String msg = "Parameter [" + string + "] does not have separate identity and auth tokens.";
					LOG.warning(msg);
					return;
				}
				
				String[] parts = string.split(" +");
				String  u_name = parts[0];
				String  secret = parts[1];

				LoginService login_service = (LoginService) ServiceLocator.locator().get(ServiceLocator.SERVICE_LOGIN);
				String         session_key = login_service.login(u_name, secret);
				
				
				// Perform the required work
				//
				if (session_key != null)
				{
					ProgramState.setSessionKey(session_key);

					UserService  user_service = (UserService) ServiceLocator.locator().get(ServiceLocator.SERVICE_USER);
					User                 user = user_service.findByUserName(u_name);
					
					ProgramState.setUser(session_key, user);

					// Swap the enabled status of Login/Logout actions
					//
					Action logout = ActionRegistry.get(LOGOUT);
					logout.enable();
					this.disable();
					
					String msg = String.format("Hello %s.\nThank you for logging in.\nYour session key is [%s]", u_name, session_key);
					System.out.println(msg);
				}
				else
				{
					String msg = String.format("Login failure for username[%s] and secret[%s]", u_name, secret);
					LOG.warning(msg);
					return;
				}
			}
		};
		
		return action;
	}

}
