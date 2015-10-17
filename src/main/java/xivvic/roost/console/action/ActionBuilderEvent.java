package xivvic.roost.console.action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
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
import xivvic.roost.domain.Event;
import xivvic.roost.domain.Person;
import xivvic.roost.neo.EdgeSchema;
import xivvic.roost.neo.NodeFinder;
import xivvic.roost.neo.NodeFinderEmpty;
import xivvic.roost.neo.NodeSchema;
import xivvic.roost.neo.PropMeta;
import xivvic.roost.neo.SchemaManager;
import xivvic.roost.neo.task.NeoTaskInfo;
import xivvic.roost.service.EventService;
import xivvic.util.TimeUtil;
import xivvic.util.identity.RandomString;

/**
 * Builds Event actions for the application.
 * 
 * @author Reid
 *
 */
public class ActionBuilderEvent
	extends ActionBuilderBase
{
	private final static Logger LOG = Logger.getLogger(ActionBuilderEvent.class.getName());

	public ActionBuilderEvent()
	{
	}

	static Action buildListAction()
	{
		String               name = ActionBuilder.EVENT_LIST;
		final String         desc = "Lists all events.";
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
				EventService         service = components.eventService();
				if (service == null)
				{
					String   msg = String.format(name + ": DI returned null service");
					LOG.severe(msg);
					return;
				}

				Consumer<DomainEntity>   printer = e -> System.out.println(e);
				System.out.println("events");
				service.apply(printer);
			}
		};
		
		return action;
	}
	
	static Action buildDeleteAction()
	{
		String               name = ActionBuilder.EVENT_DELETE;
		String               desc = "Deletes an event";
		String              usage = "delete id";
		ActionMetadata       meta = new ActionMetadata(name, desc, usage);
		Set<String>      required = new HashSet<>();
		Map<String, Object>   map = new HashMap<>();

		required.add(Event.PROP_ID);

		NodeSchema schema = SchemaManager.getInstance().getEntitySchema(Event.class);
		PropMeta   p_meta = schema.property(Event.PROP_ID);
		NodeFinder finder = NodeFinderEmpty.create(schema, p_meta);
		map.put(NeoTaskInfo.NODE_ONE_LOCATOR, finder);

		InputProcessor ip = new InputProcessorNVPairs(map, required, null);
		Action     action = ActionBuilderBase.buildActionForDeleteCommand(meta, ip);
		
		return action;
	}

	static Action buildAddAction()
	{
		String               name = ActionBuilder.EVENT_ADD;
		String               desc = "Adds a new event. Required params in name:value list";
		String              usage = "a event_text:{A big birthday} event_date:2016-04-28 event_type:BIRTHDAY person_id:p123";
		ActionMetadata       meta = new ActionMetadata(name, desc, usage);
		Set<String>      required = new HashSet<>();
		Map<String, Object>   map = new HashMap<>();

		required.add(Event.PROP_ID);
		required.add(Event.PROP_TYPE);
		required.add(Event.PROP_TEXT);
		required.add(Person.PROP_ID);

		NodeSchema   schema_event = SchemaManager.getInstance().getEntitySchema(Event.class);
		NodeSchema  schema_person = SchemaManager.getInstance().getEntitySchema(Person.class);
		PropMeta           p_meta = schema_person.property(Person.PROP_ID);
		NodeFinder  finder_person = NodeFinderEmpty.create(schema_person,  p_meta);

		map.put(NeoTaskInfo.NODE_ONE_SCHEMA, schema_event);
		map.put(NeoTaskInfo.NODE_TWO_LOCATOR, finder_person);

		EdgeSchema link_schema = SchemaManager.getInstance().getEdgeSchema(RoostRelType.EVENT_PROGENITOR);
		
		map.put(NeoTaskInfo.RELATIONSHIP_SCHEMA,    link_schema);
	
		Map<String, Function<String, ?>> converters = new HashMap<>();
		converters.put(Event.PROP_DATE, TimeUtil.text2EpochFunction());
		converters.put(Event.PROP_TIME, TimeUtil.textToNanosFunction());
		
		Map<String, Supplier<String>> synthetics = new HashMap<>();
		Supplier<String> id_creator = () -> { RandomString rs = new RandomString(16); return rs.nextString(); };
		synthetics.put(Event.PROP_ID, id_creator);

		InputProcessor  ip = new InputProcessorNVPairs(map, required, converters, synthetics);
		Action      action = ActionBuilderBase.buildActionForAddCommand(meta, ip);
		
		return action;
	}

	static Action buildListEventsForPersonAction()
	{
		String               name = ActionBuilder.EVENT_LIST_FOR_PERSON;
		String               desc = "Lists the events related to a person (not subscriptions)";
		String              usage = "lfp person_id:p123";
		ActionMetadata       meta = new ActionMetadata(name, desc, usage);
		Set<String>      required = new HashSet<>();
		Map<String, Object>   map = new HashMap<>();

		required.add(Person.PROP_ID);

		
		InputProcessor  ip = new InputProcessorNVPairs(map, required, null);

		Action action = new ActionBase(meta.name(), meta.desc(), true)
		{
			@Override
			protected void internal_invoke(Object param)
			{
				Map<String, Object> map = ip.process(param);
				if (map == null)
				{
					String   msg = String.format(meta.name() + ": called with invalid params [%s]. Expecting \"%s:p123\". Abort.", param, Person.PROP_ID);
					LOG.warning(msg);
					return;
				}
				
				ProgramComponents components = DaggerProgramComponents.create();
				EventService         service = components.eventService();
				String                   pid = (String) map.get(Person.PROP_ID);
				List<Event>           events = service.eventsForPerson(pid);
				Consumer<Event>      printer = e -> System.out.println(e);

				System.out.println("Events:");
				events.forEach(printer);
			}
		};
		
		return action;
	}

}
