package xivvic.roost.console.action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xivvic.console.action.Action;
import xivvic.console.action.ActionBase;
import xivvic.console.action.ActionMetadata;
import xivvic.console.input.InputProcessor;
import xivvic.console.input.InputProcessorNVPairs;
import xivvic.roost.console.DaggerProgramComponents;
import xivvic.roost.console.ProgramComponents;
import xivvic.roost.domain.DomainEntity;
import xivvic.roost.domain.Person;
import xivvic.roost.neo.NodeFinder;
import xivvic.roost.neo.NodeFinderEmpty;
import xivvic.roost.neo.NodeSchema;
import xivvic.roost.neo.PropMeta;
import xivvic.roost.neo.PropPredicate;
import xivvic.roost.neo.SchemaManager;
import xivvic.roost.neo.task.NeoTaskInfo;
import xivvic.roost.service.PersonService;

/**
 * Builds People actions for the application.
 * 
 * @author Reid
 *
 */
public class ActionBuilderPerson
	extends ActionBuilderBase
{
	private final static Logger LOG = LoggerFactory.getLogger(ActionBuilderPerson.class.getName());
	
	public ActionBuilderPerson()
	{
	}
				


	static Action buildListAction()
	{
		String               name = ActionBuilder.PERSON_LIST;
		final String          desc = "Lists all people.";
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
				PersonService         service = components.personService();
				if (service == null)
				{
					String   msg = String.format(name + ": DI returned null service");
					LOG.error(msg);
					return;
				}

				Consumer<DomainEntity>   printer = e -> System.out.println(e);
				System.out.println("People");
				service.apply(printer);
			}
		};
		
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
