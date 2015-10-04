package xivvic.roost.console;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

import xivvic.command.Command;
import xivvic.command.CommandBase;
import xivvic.command.CommandProcessor;
import xivvic.command.CommandSource;
import xivvic.command.CommandStatus;
import xivvic.console.action.Action;
import xivvic.console.action.ActionBase;
import xivvic.console.action.ActionMetadata;
import xivvic.console.input.InputProcessor;
import xivvic.neotest.program.NeoUtil;
import xivvic.roost.domain.DomainEntity;
import xivvic.roost.domain.Person;
import xivvic.roost.neo.LinkSpec;
import xivvic.roost.neo.NodeFinder;
import xivvic.roost.neo.NodeFinderFull;
import xivvic.roost.neo.PropMeta;
import xivvic.roost.neo.task.NeoTaskInfo;
import xivvic.roost.service.DomainEntityContainer;
import xivvic.roost.service.GroupService;
import xivvic.roost.service.ServiceLocator;
import xivvic.util.identity.RandomString;
import xivvic.util.text.StringUtil;

/**
 * Base class for action builders
 * 
 * @author Reid
 *
 */
public abstract class ActionBuilderBase
	implements ActionBuilder
{
	private final static Logger LOG = Logger.getLogger(ActionBuilderBase.class.getName());
	
	private static final Set<ActionBuilder> subclasses = new HashSet<>();
	private static final RandomString            idgen = new RandomString(16);
	
	// In this static block, create and store an instance of each subclass of this
	// base class, so that their build() methods can be called to create all the appropriate
	// Actions
	//
	static
	{
		subclasses.add(new ActionBuilderAddress());
		subclasses.add(new ActionBuilderConfiguration());
		subclasses.add(new ActionBuilderCommand());
		subclasses.add(new ActionBuilderEvent());
		subclasses.add(new ActionBuilderGroup());
		subclasses.add(new ActionBuilderHome());
		subclasses.add(new ActionBuilderPerson());
		subclasses.add(new ActionBuilderSubscription());
		subclasses.add(new ActionBuilderUser());
	}


	private static boolean          completedBuilding = false;
	
	/**
	 * Build all the actions for all the subclasses
	 * 
	 */
	public static final void buildAndRegisterActions()
	{
		if (completedBuilding)
		{
			String msg = String.format("Actions have already been built. Return with no action.");
			LOG.warning(msg);
			return;
		}
		
		System.out.println("Initializing actions.");

		for (ActionBuilder ab : subclasses)
		{
			List<Action>  list = ab.build(); 
			
			for (Action a : list)
				ActionRegistry.put(a);
		}
		
		completedBuilding = true;
	}
	

	/**
	 * Reflectively call all the private static buildXXX() methods and
	 * return the resulting actions as a list.
	 * 
	 * This method is used to prevent having to repeat the names of every
	 * build method in this master builder method.
	 * 
	 * @return A list of the actions returned by the build methods.
	 */
	public final List<Action> build()
	{
		
		Action x = ActionBuilderEvent.buildDeleteAction();
		System.out.println(x);

		List<Action> result = new LinkedList<>();
		Class<?>        cls = this.getClass();
		Method[]    methods = cls.getDeclaredMethods();
		System.out.println("Building methods for class: " + cls.getName());
		
		for (Method m : methods)
		{
			if (! thisMethodShouldBeInvoked(m))
				continue;
			
			Object[] args = {};
			Action      a = null;
			try
			{
				System.out.println("Method to invoke: " + m.getName());
				a = (Action) m.invoke(null, args);
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				e.printStackTrace();
				System.err.println("Failed to create program actions.  Exiting.");
				System.exit(0);
			}
			
			if (a != null)
				result.add(a);
		}
		
		return result;
	}
	
	// Only call static methods starting with "build" that take 0 arguments
	//
	private boolean thisMethodShouldBeInvoked(Method m)
	{
		String name = m.getName();
		int    mods = m.getModifiers();
		int    argc = m.getParameterCount();
		
		if (   argc != 0                ) return false;
		if ( ! Modifier.isStatic(mods)  ) return false;
		if ( ! name.startsWith("build") ) return false;
		if ( ! name.endsWith("Action")  ) return false;
		
		return true;
	}
	
	@Override
	public String toString()
	{
		return this.getClass().getSimpleName();
	}

	final static Action buildActionForAddCommand(ActionMetadata meta, InputProcessor aip)
	{
		Action action = new ActionBase(meta.name(), meta.desc(), true)
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

				boolean bind_ok = bindLinkedNodeProperties(map);
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
				
				// TODO:  Need to think about source concept
				// Should it now be from the current user?
				//
				CommandSource source = new CommandSource(){};
				
				String       id = idgen.nextString();
				String   intent = meta.name();
				Command command = CommandBase.builder()
							.id(id)
							.source(source)
							.intent(intent)
							.properties(map)
							.build();

				CommandStatus status = cp.receive(command);
				
				if (status == CommandStatus.ACKNOWLEDGED)
				{
					String msg = String.format("Command submitted for processing [%s]", command.toString());
					LOG.info(msg);
				}
				else
				{
					String msg = String.format("Command rejected: [%s]", status);
					LOG.warning(msg);
				}
			}
		};
		
		return action;
	}

	// The binding for the Node's property is not available until the action's input parameter has
	// been processed. This method will look up that bound value in the argument map and provide it to the
	// NodeSpec so that it can be used later to look up the node to link to.
	//
	private static boolean bindLinkedNodeProperties(Map<String, Object> map)
	{
		if (map == null)
			return false;
		
		String link_key = NeoTaskInfo.EDGE_MAP;
		
		@SuppressWarnings("unchecked")
		Map<String, LinkSpec> links = (Map<String, LinkSpec>) map.get(link_key);
		if (links == null)
		{
			String   msg = String.format("Note: List of links to create not available. Need key[%s]", link_key);
			LOG.info(msg);
			return true;
		}
		
		Consumer<Entry<String, LinkSpec>> bind = (entry) -> 
		{ 
			LinkSpec     spec = entry.getValue();
			NodeFinder finder = spec.finder();
			PropMeta   p_meta = finder.prop();
			String        key = p_meta.key();
			Object      value = map.get(key);
			
			if (value == null)
			{
				String   msg = String.format("Property for key [%s] not available.", key);
				LOG.warning(msg);
				return;
			}

			NodeFinder new_finder = NodeFinderFull.create(finder, value);
			LinkSpec     new_spec = LinkSpec.create(spec.schema(), new_finder);
			entry.setValue(new_spec);
		};

		links.entrySet().forEach(bind);
		
		return true;
	}
	


	static Action buildListAction(ActionMetadata meta, InputProcessor aip)
	{
		Action action = new ActionBase(meta.name(), meta.desc(), true)
		{
			@Override
			protected void internal_invoke(Object param)
			{
				if (param != null)
				{
					String   msg = String.format(meta.name() + ": called with param [%s]. Not used.", param);
					LOG.warning(msg);
				}
				
				DomainEntityContainer  container = (DomainEntityContainer) ServiceLocator.locator().get(meta.service());
				if (container == null)
				{
					String   msg = String.format(meta.name() + ": Could not locate service [%s]. Abort.", meta.service());
					LOG.severe(msg);
					return;
				}

				Consumer<DomainEntity>   printer = e -> System.out.println(e);
				System.out.println(meta.name() + "s:");
				container.apply(printer);
			}
		};
		
		return action;
	}

	static Action buildListGroupMembersAction(ActionMetadata meta, InputProcessor aip)
	{
		Action action = new ActionBase(meta.name(), meta.desc(), true)
		{
			@Override
			protected void internal_invoke(Object param)
			{
				String           params = (String) param;
				Map<String, Object> map = aip.process(params);
				if (map == null)
				{
					String   msg = String.format("Error\nUsage: \n%s", meta.usage());
					System.err.println(msg);
					return;
				}
				
				GroupService  svc = (GroupService) ServiceLocator.locator().get(meta.service());
				if (svc == null)
				{
					String   msg = String.format(meta.name() + ": Could not locate service [%s]. Abort.", meta.service());
					LOG.severe(msg);
					return;
				}
				
				String gid = (String) map.get("group_id");
				List<Person> list = svc.listMembers(gid);

				Consumer<Person>   printer = p -> System.out.println(p);
				System.out.println(meta.name() + "s:");
				list.forEach(printer);
			}
		};
		
		return action;
	}
	
	final static Action buildActionForDeleteCommand(ActionMetadata meta, InputProcessor aip)
	{
		Action action = new ActionBase(meta.name(), meta.desc(), true)
		{
			@Override
			protected void internal_invoke(Object param)
			{
				String           params = (String) param;
				Map<String, Object> map = aip.process(params);
				if (map == null)
				{
					String   msg = String.format("Error\nUsage: \n%s", meta.usage());
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
				
				CommandSource source = new CommandSource() {};
				String            id = idgen.nextString();
				String        intent = meta.name();
				Command      command = CommandBase.builder()
						.id(id)
						.source(source)
						.intent(intent)
						.properties(map)
						.build();

				CommandStatus status = cp.receive(command);
				command.setStatus(status);
				
				if (status == CommandStatus.ACKNOWLEDGED)
				{
					String msg = String.format("Command submitted for processing [%s]", command.toString());
					LOG.info(msg);
				}
				else
				{
					String msg = String.format("Command rejected: [%s]", command.toString());
					LOG.warning(msg);
				}
			}
		};
		
		return action;
	}


	final static Action buildActionForLinkCommand(ActionMetadata meta, InputProcessor aip)
	{
		Action action = new ActionBase(meta.name(), meta.desc(), true)
		{
			@Override
			protected void internal_invoke(Object param)
			{
				String           params = (String) param;
				Map<String, Object> map = aip.process(params);
				if (map == null)
				{
					String   msg = String.format("Error\nUsage: \n%s", meta.usage());
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
				
				String            id = idgen.nextString();
				String        intent = meta.name();
				Command      command = CommandBase.builder().id(id).intent(intent).properties(map).build();
				CommandStatus status = cp.receive(command);
				
				if (status == CommandStatus.ACKNOWLEDGED)
				{
					String msg = String.format("Command submitted for processing [%s]", command.toString());
					LOG.info(msg);
				}
				else
				{
					String msg = String.format("Command rejected: [%s]", command.toString());
					LOG.warning(msg);
				}

			}
		};
		
		return action;
	}


	
	public static Action buildActionForUnlinkCommand(ActionMetadata meta,
			InputProcessor aip)
	{
		Action action = new ActionBase(meta.name(), meta.desc(), true)
		{
			@Override
			protected void internal_invoke(Object param)
			{
				String           params = (String) param;
				Map<String, Object> map = aip.process(params);
				if (map == null)
				{
					String   msg = String.format("Error\nUsage: \n%s", meta.usage());
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
				
				String            id = idgen.nextString();
				String        intent = meta.name();
				Command      command = CommandBase.builder().id(id).intent(intent).properties(map).build();
				CommandStatus status = cp.receive(command);
				
				if (status == CommandStatus.ACKNOWLEDGED)
				{
					String msg = String.format("Command submitted for processing [%s]", command.toString());
					LOG.info(msg);
				}
				else
				{
					String msg = String.format("Command rejected: [%s]", command.toString());
					LOG.warning(msg);
				}

			}
		};
		
		return action;
	}

	static Action buildNeoExecCypherAction(final String action_token, final String cypher, final String description)
	{
		Action action = new ActionBase(action_token, description, true)
		{
			@Override
			protected void internal_invoke(Object param)
			{
				GraphDatabaseService db = NeoUtil.acquireAndConfigureDbService();
	
				try (Transaction tx = db.beginTx())
				{
					db.execute(cypher);
					tx.success();
				}
			}
		};
		
		return action;
	}

	static Action buildNeoExecDynamicCypherAction(ActionMetadata meta, final String template, final String desc, InputProcessor ip)
	{
		Action action = new ActionBase(meta.name(), desc, true)
		{
			@Override
			protected void internal_invoke(Object param)
			{
				Map<String, Object> map = ip.process(param);
				
				if (map == null)
				{
					String   msg = String.format("Error\nUsage: \n%s", meta.usage());
					System.err.println(msg);
					return;
				}

				String cypher = bindCypherParameters(template, map);
				
				GraphDatabaseService db = NeoUtil.acquireAndConfigureDbService();
	
				try (Transaction tx = db.beginTx())
				{
					db.execute(cypher);
					tx.success();
				}
			}

			private String bindCypherParameters(String template, Map<String, Object> map)
			{
				if (map == null)
					return template;
				
				String[]   keys = map.keySet().toArray(new String[0]);
				String  result = template;
				
				for (String key : keys)
				{
					Object  value = map.get(key);
					if (value == null)
						continue;
					
					String replace_with = value.toString();
					String       target = StringUtil.convertToTemplateTarget(key);
					result              = result.replace(target, replace_with);
				}
				
				return result;
			}

		};
		
		return action;
	}


	// NOTE:  Your function needs to be expecting the type of object returned by your cypher query.
	// 
	static Action buildNeoExecCypherItemQuery(ActionMetadata meta, final String cypher, Consumer<Map<String, Object>> consumer)
	{
		Action action = new ActionBase(meta.name(), meta.desc(), true)
		{
			@Override
			protected void internal_invoke(Object param)
			{
				GraphDatabaseService db = NeoUtil.acquireAndConfigureDbService();
	
				try (Transaction tx = db.beginTx())
				{
					Result result = db.execute(cypher);
					result.forEachRemaining(consumer);
					result.close();
					tx.success();
				}
			}
		};
		
		return action;
	}

}
