package xivvic.roost.console.action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import xivvic.roost.domain.Subscription;
import xivvic.roost.domain.User;
import xivvic.roost.neo.EdgeSchema;
import xivvic.roost.neo.NodeFinder;
import xivvic.roost.neo.NodeFinderEmpty;
import xivvic.roost.neo.NodeSchema;
import xivvic.roost.neo.PropMeta;
import xivvic.roost.neo.PropPredicate;
import xivvic.roost.neo.SchemaManager;
import xivvic.roost.neo.task.NeoTaskInfo;
import xivvic.roost.service.SubscriptionService;
import xivvic.util.identity.RandomString;

/**
 * Builds Subscription actions for the application.
 * 
 * @author Reid
 *
 */
public class ActionBuilderSubscription
	extends ActionBuilderBase
{
	private final static Logger LOG = LoggerFactory.getLogger(ActionBuilderSubscription.class.getName());
	
	public ActionBuilderSubscription()
	{
	}

	static Action buildListAction()
	{
		String               name = ActionBuilder.SUBSCRIPTION_LIST;
		final String         desc = "Lists all subscriptions.";
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
				
				ProgramComponents    components = DaggerProgramComponents.create();
				SubscriptionService     service = components.subscriptionService();
				if (service == null)
				{
					String   msg = String.format(name + ": DI returned null service");
					LOG.error(msg);
					return;
				}

				Consumer<DomainEntity>   printer = e -> System.out.println(e);
				System.out.println("Subscriptions");
				service.apply(printer);
			}
		};
		
		return action;
	}
	
	static Action buildDeleteAction()
	{
		String               name = ActionBuilder.SUBSCRIPTION_DELETE;
		String               desc = "Deletes an subscription";
		String              usage = "delete subs_id:id";
		ActionMetadata       meta = new ActionMetadata(name, desc, usage);
		Set<String>      required = new HashSet<>();
		Map<String, Object>   map = new HashMap<>();

		required.add(Subscription.PROP_ID);

		NodeSchema schema = SchemaManager.getInstance().getEntitySchema(Subscription.class);
		PropMeta   p_meta = schema.property(Subscription.PROP_ID);
		NodeFinder finder = NodeFinderEmpty.create(schema, p_meta);
		map.put(NeoTaskInfo.NODE_ONE_LOCATOR, finder);
	
		InputProcessor        aip = new InputProcessorNVPairs(map, required, null);
		Action             action = ActionBuilderBase.buildActionForDeleteCommand(meta, aip);
		
		return action;
	}

	static Action buildAddAction()
	{
		String               name = ActionBuilder.SUBSCRIPTION_ADD;
		String               desc = "Adds a new subscription. Required params in name:value list";
		String              usage = "a subs_expiry:NEVER user_id:u123 event_id:e123";
		ActionMetadata       meta = new ActionMetadata(name, desc, usage);
		Set<String>      required = new HashSet<>();
		Map<String, Object>   map = new HashMap<>();
		NodeSchema         schema = SchemaManager.getInstance().getEntitySchema(Subscription.class);
		List<PropMeta>     p_list = schema.properties(PropPredicate.predicateRequired());
		
		for (PropMeta p : p_list)
			required.add(p.key());

		required.add(Event.PROP_ID);
		required.add(User.PROP_ID);

		map.put(NeoTaskInfo.NODE_ONE_SCHEMA, schema);

		NodeSchema    schema_user = SchemaManager.getInstance().getEntitySchema(User.class);
		NodeSchema   schema_event = SchemaManager.getInstance().getEntitySchema(Event.class);
		PropMeta           u_meta = schema_user.property(User.PROP_ID);
		PropMeta           e_meta = schema_event.property(Event.PROP_ID);
		NodeFinder    finder_user = NodeFinderEmpty.create(schema_user,  u_meta);
		NodeFinder finder_address = NodeFinderEmpty.create(schema_event, e_meta);
		EdgeSchema    edge_schema = SchemaManager.getInstance().getEdgeSchema(RoostRelType.USER_SUBSCRIBE);

		map.put(NeoTaskInfo.NODE_ONE_LOCATOR, finder_user);
		map.put(NeoTaskInfo.NODE_TWO_LOCATOR, finder_address);
		map.put(NeoTaskInfo.RELATIONSHIP_SCHEMA, edge_schema);
		
		Map<String, Function<String, ?>> converters = new HashMap<>();

// TODO:  Don't make this an object, leave it as a string b/c that's what Neo can handle.
//
//		converters.put(SubscriptionDao.PROP_EXPIRY, SubscriptionExpiry.converterFunction());
		
		Map<String, Supplier<String>> synth = new HashMap<>();
		Supplier<String>         id_creator = RandomString.RandomStringSupplier(16);
		synth.put(Subscription.PROP_ID, id_creator);

		InputProcessor  aip = new InputProcessorNVPairs(map, required, converters, synth);
		Action       action = ActionBuilderBase.buildActionForAddCommand(meta, aip);
		
		return action;
	}

	static Action buildListSubscriptionsForUserAction()
	{
		String               name = ActionBuilder.SUBSCRIPTION_LIST_4_USER;
		String               desc = "Lists the subscriptions related to a user";
		String              usage = "l4u user_id:u123";
		ActionMetadata       meta = new ActionMetadata(name, desc, usage);
		Set<String>      required = new HashSet<>();
		Map<String, Object>   map = new HashMap<>();

		required.add(User.PROP_ID);
		
		final InputProcessor  aip = new InputProcessorNVPairs(map, required, null);

		Action action = new ActionBase(meta.name(), meta.desc(), true)
		{
			@Override
			protected void internal_invoke(Object param)
			{
				Map<String, Object> map = aip.process(param);
				if (map == null)
				{
					String   msg = String.format(meta.name() + ": called with invalid params [%s]. Expecting \"%s:p123\". Abort.", param, User.PROP_ID);
					LOG.warn(msg);
					return;
				}
				
				ProgramComponents      components = DaggerProgramComponents.create();
				SubscriptionService       service = components.subscriptionService();
				String                        uid = (String) map.get(User.PROP_ID);
				Consumer<Subscription>    printer = e -> System.out.println(e);
				List<Subscription>  subscriptions = service.subscriptionsForUser(uid);

				System.out.println("Subscriptions:");
				subscriptions.forEach(printer);
			}
		};
		
		return action;
	}

	static Action buildListSubscriptionsForEventAction()
	{
		String               name = ActionBuilder.SUBSCRIPTION_LIST_4_EVENT;
		String               desc = "Lists the subscriptions related to an event";
		String              usage = "l4e event_id:e123";
		ActionMetadata       meta = new ActionMetadata(name, desc, usage);
		Set<String>      required = new HashSet<>();
		Map<String, Object>   map = new HashMap<>();

		required.add(Event.PROP_ID);
		
		final InputProcessor  aip = new InputProcessorNVPairs(map, required, null);

		Action action = new ActionBase(meta.name(), meta.desc(), true)
		{
			@Override
			protected void internal_invoke(Object param)
			{
				Map<String, Object> map = aip.process(param);
				if (map == null)
				{
					String   msg = String.format(meta.name() + ": called with invalid params [%s]. Expecting \"%s:e123\". Abort.", param, Event.PROP_ID);
					LOG.warn(msg);
					return;
				}
				
				ProgramComponents      components = DaggerProgramComponents.create();
				SubscriptionService       service = components.subscriptionService();
				Consumer<Subscription>    printer = e -> System.out.println(e);
				String                   event_id = (String) map.get(Event.PROP_ID);
				List<Subscription>  subscriptions = service.subscriptionsForEvent(event_id);

				System.out.println("Subscriptions:");
				subscriptions.forEach(printer);
			}
		};
		
		return action;
	}

}
