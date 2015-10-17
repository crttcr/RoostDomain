package xivvic.roost.console.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;

import xivvic.console.action.Action;
import xivvic.console.action.ActionBase;
import xivvic.console.action.ActionMetadata;
import xivvic.console.input.InputProcessor;
import xivvic.console.input.InputProcessorNVPairs;
import xivvic.neotest.program.RoostRelType;
import xivvic.roost.console.DaggerProgramComponents;
import xivvic.roost.console.ProgramComponents;
import xivvic.roost.domain.DomainEntity;
import xivvic.roost.domain.Group;
import xivvic.roost.domain.Person;
import xivvic.roost.neo.EdgeSchema;
import xivvic.roost.neo.EdgeSchemaImpl;
import xivvic.roost.neo.NodeFinder;
import xivvic.roost.neo.NodeFinderEmpty;
import xivvic.roost.neo.NodeSchema;
import xivvic.roost.neo.PropMeta;
import xivvic.roost.neo.PropPredicate;
import xivvic.roost.neo.SchemaManager;
import xivvic.roost.neo.task.NeoTaskInfo;
import xivvic.roost.service.GroupService;

/**
 * Builds Group actions for the application.
 * 
 * @author Reid
 *
 */
public class ActionBuilderGroup
	extends ActionBuilderBase
{
	private final static Logger LOG = Logger.getLogger(ActionBuilderGroup.class.getName());
	
	public ActionBuilderGroup()
	{
	}

	static Action buildListAction()
	{
		String               name = ActionBuilder.GROUP_LIST;
		final String          desc = "Lists all groups.";
		Action             action = new ActionBase(name, desc, true)
		{
			@Override
			protected void internal_invoke(Object param)
			{
				if (param != null)
				{
					String   msg = String.format(name + ": called with param [%s]. Not used.", param);
					LOG.info(msg);
				}
				
				ProgramComponents components = DaggerProgramComponents.create();
				GroupService         service = components.groupService();
				if (service == null)
				{
					String   msg = String.format(name + ": DI returned null service");
					LOG.severe(msg);
					return;
				}

				Consumer<DomainEntity>   printer = e -> System.out.println(e);
				System.out.println("Groups");
				service.apply(printer);
			}
		};
		
		return action;
	}
	
	static Action buildGroupMembersAction()
	{
		String               name = ActionBuilder.GROUP_LIST_MEMBERS;
		final String         desc = "Lists members for a specified group.";
		String              usage = "list group_id:g123";
		Set<String>      required = new HashSet<>();
		Map<String, Object>   map = new HashMap<>();

		required.add(Group.PROP_ID);

		NodeSchema schema = SchemaManager.getInstance().getEntitySchema(Group.class);
		PropMeta   p_meta = schema.property(Group.PROP_ID);
		NodeFinder finder = NodeFinderEmpty.create(schema, p_meta);
		map.put(NeoTaskInfo.NODE_ONE_LOCATOR, finder);
		
		InputProcessor  ip = new InputProcessorNVPairs(map, required, null);

		Action action = new ActionBase(name, desc, true)
		{
			@Override
			protected void internal_invoke(Object param)
			{
				String           params = (String) param;
				Map<String, Object> map = ip.process(params);
				if (map == null)
				{
					String   msg = String.format("Error\nUsage: \n%s", usage);
					System.err.println(msg);
					return;
				}
					
				ProgramComponents components = DaggerProgramComponents.create();
				GroupService         service = components.groupService();
				String                   gid = (String) map.get("group_id");
				List<Person>            list = service.listMembers(gid);

				Consumer<Person>   printer = p -> System.out.println(p);
				System.out.println(name + "s:");
				list.forEach(printer);
			}
		};
			
		return action;
	}
	
	static Action buildDeleteAction()
	{
		String               name = ActionBuilder.GROUP_DELETE;
		String               desc = "Deletes an event";
		String              usage = "delete id";
		ActionMetadata       meta = new ActionMetadata(name, desc, usage);
		Set<String>      required = new HashSet<>();
		Map<String, Object>   map = new HashMap<>();

		required.add(Group.PROP_ID);

		NodeSchema schema = SchemaManager.getInstance().getEntitySchema(Group.class);
		PropMeta   p_meta = schema.property(Group.PROP_ID);
		NodeFinder finder = NodeFinderEmpty.create(schema, p_meta);
		map.put(NeoTaskInfo.NODE_ONE_LOCATOR, finder);
		
		InputProcessor  ip = new InputProcessorNVPairs(map, required, null);
		Action      action = ActionBuilderBase.buildActionForDeleteCommand(meta, ip);

		return action;
	}

	static Action buildAddAction()
	{
		String               name = ActionBuilder.GROUP_ADD;
		String               desc = "Adds a new Group. Required params in name:value list";
		String              usage = "a group_name:SomeName group_id:g123";
		ActionMetadata       meta = new ActionMetadata(name, desc, usage);
		Set<String>      required = new HashSet<>();
		Map<String, Object>   map = new HashMap<>();

		NodeSchema     schema = SchemaManager.getInstance().getEntitySchema(Group.class);
		List<PropMeta> p_list = schema.properties(PropPredicate.predicateRequired());
		
		for (PropMeta p : p_list)
			required.add(p.key());

		map.put(NeoTaskInfo.NODE_ONE_SCHEMA, schema);
		
		InputProcessor ip = new InputProcessorNVPairs(map, required, null);
		Action     action = ActionBuilderBase.buildActionForAddCommand(meta, ip);
		
		return action;
	}

	static Action buildMemberAddAction()
	{
		String               name = ActionBuilder.GROUP_MEMBER_ADD;
		String               desc = "Adds a person to a group";
		String              usage = "a group_id:g123 person_id:p123";
		ActionMetadata       meta = new ActionMetadata(name, desc, usage);
		Set<String>      required = new HashSet<>();
		Map<String, Object>   map = new HashMap<>();

		required.add(Group.PROP_ID);
		required.add(Person.PROP_ID);

		NodeSchema  group_schema = SchemaManager.getInstance().getEntitySchema(Group.class);
		PropMeta      group_prop = group_schema.property(Group.PROP_ID);
		NodeFinder  group_finder = NodeFinderEmpty.create(group_schema, group_prop);

		NodeSchema person_schema = SchemaManager.getInstance().getEntitySchema(Person.class);
		PropMeta     person_prop = person_schema.property(Person.PROP_ID);
		NodeFinder person_finder = NodeFinderEmpty.create(person_schema, person_prop);

		List<PropMeta> edge_props = new ArrayList<>();
		EdgeSchema    edge_schema = new EdgeSchemaImpl(RoostRelType.GROUP_MEMBER, edge_props);

		map.put(NeoTaskInfo.NODE_ONE_LOCATOR, group_finder);
		map.put(NeoTaskInfo.NODE_TWO_LOCATOR, person_finder);
		map.put(NeoTaskInfo.RELATIONSHIP_SCHEMA, edge_schema);

		InputProcessor  ip = new InputProcessorNVPairs(map, required, null);
		Action      action = ActionBuilderBase.buildActionForLinkCommand(meta, ip);
		
		return action;
	}

	static Action buildMemberRemoveAction()
	{
		String               name = ActionBuilder.GROUP_MEMBER_DELETE;
		String               desc = "Removes a person from a group";
		String              usage = "remove group_id:g123 person_id:p123";
		ActionMetadata       meta = new ActionMetadata(name, desc, usage);
		Set<String>      required = new HashSet<>();
		Map<String, Object>   map = new HashMap<>();

		required.add(Group.PROP_ID);
		required.add(Person.PROP_ID);

		NodeSchema  group_schema = SchemaManager.getInstance().getEntitySchema(Group.class);
		PropMeta      group_prop = group_schema.property(Group.PROP_ID);
		NodeFinder  group_finder = NodeFinderEmpty.create(group_schema, group_prop);

		NodeSchema person_schema = SchemaManager.getInstance().getEntitySchema(Person.class);
		PropMeta     person_prop = person_schema.property(Person.PROP_ID);
		NodeFinder person_finder = NodeFinderEmpty.create(person_schema, person_prop);
		EdgeSchema   edge_schema = SchemaManager.getInstance().getEdgeSchema(RoostRelType.GROUP_MEMBER);

		map.put(NeoTaskInfo.NODE_ONE_LOCATOR, group_finder);
		map.put(NeoTaskInfo.NODE_TWO_LOCATOR, person_finder);
		map.put(NeoTaskInfo.RELATIONSHIP_SCHEMA, edge_schema);
		
		InputProcessor  ip = new InputProcessorNVPairs(map, required, null);
		Action      action = ActionBuilderBase.buildActionForUnlinkCommand(meta, ip);
		
		return action;
	}



}
