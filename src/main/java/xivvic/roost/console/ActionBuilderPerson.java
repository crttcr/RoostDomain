package xivvic.roost.console;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import xivvic.console.action.Action;
import xivvic.console.action.ActionMetadata;
import xivvic.console.input.InputProcessor;
import xivvic.console.input.InputProcessorNVPairs;
import xivvic.roost.domain.Person;
import xivvic.roost.neo.NodeFinder;
import xivvic.roost.neo.NodeFinderEmpty;
import xivvic.roost.neo.NodeSchema;
import xivvic.roost.neo.PropMeta;
import xivvic.roost.neo.PropPredicate;
import xivvic.roost.neo.SchemaManager;
import xivvic.roost.neo.task.NeoTaskInfo;
import xivvic.roost.service.ServiceLocator;

/**
 * Builds People actions for the application.
 * 
 * @author Reid
 *
 */
public class ActionBuilderPerson
	extends ActionBuilderBase
{
//	private final static Logger LOG = Logger.getLogger(ActionBuilderPerson.class.getName());
	
	public ActionBuilderPerson()
	{
	}
				


	static Action buildListAction()
	{
		String               name = ActionBuilder.PERSON_LIST;
		final String          desc = "Lists all people.";
		String              usage = "list";
		ActionMetadata       meta = new ActionMetadata(name, desc, usage, ServiceLocator.SERVICE_PERSON);
		InputProcessor  ip = null;
		Action      action = ActionBuilderBase.buildListAction(meta, ip);
		return action;
	}
	
	static Action buildDeleteAction()
	{
		String               name = ActionBuilder.PERSON_DELETE;
		String               desc = "Deletes a person";
		String              usage = "delete id";
		ActionMetadata       meta = new ActionMetadata(name, desc, usage);
		Set<String>      required = new HashSet<>();
		Map<String, Object>   map = new HashMap<>();

		required.add(Person.PROP_ID);

		NodeSchema schema = SchemaManager.getInstance().getEntitySchema(Person.class);
		PropMeta   p_meta = schema.property(Person.PROP_ID);
		NodeFinder finder = NodeFinderEmpty.create(schema, p_meta);
		map.put(NeoTaskInfo.NODE_ONE_LOCATOR, finder);

		InputProcessor  ip = new InputProcessorNVPairs(map, required, null);
		Action      action = ActionBuilderBase.buildActionForDeleteCommand(meta, ip);
		
		return action;
	}

	static Action buildAddAction()
	{
		String               name = ActionBuilder.PERSON_ADD;
		String               desc = "Adds a new person. Required params in name:value list";
		String              usage = "a person_id:1234 person_name_first:Carlton person_name_last:Turner person_name_middle:Reid";
		ActionMetadata       meta = new ActionMetadata(name, desc, usage);
		Set<String>      required = new HashSet<>();
		Map<String, Object>   map = new HashMap<>();

		NodeSchema schema = SchemaManager.getInstance().getEntitySchema(Person.class);

		PropPredicate pred = PropPredicate.predicateRequired();
		for (PropMeta pm : schema.properties(pred))
		{
			String key = pm.key();
			required.add(key);
		}

		map.put(NeoTaskInfo.NODE_ONE_SCHEMA, schema);

		InputProcessor  ip = new InputProcessorNVPairs(map, required, null);
		Action      action = ActionBuilderBase.buildActionForAddCommand(meta, ip);
		
		return action;
	}
	
}
