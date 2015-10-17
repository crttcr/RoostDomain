package xivvic.roost.console.action;


import java.util.HashMap;
import java.util.HashSet;
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
import xivvic.roost.domain.Address;
import xivvic.roost.domain.DomainEntity;
import xivvic.roost.domain.Person;
import xivvic.roost.neo.EdgeSchema;
import xivvic.roost.neo.NodeFinder;
import xivvic.roost.neo.NodeFinderEmpty;
import xivvic.roost.neo.NodeSchema;
import xivvic.roost.neo.PropMeta;
import xivvic.roost.neo.PropPredicate;
import xivvic.roost.neo.SchemaManager;
import xivvic.roost.neo.task.NeoTaskInfo;
import xivvic.roost.service.AddressService;

/**
 * Builds Address actions for the application.
 * 
 * @author Reid
 *
 */
public class ActionBuilderAddress
	extends ActionBuilderBase
{
	private final static Logger LOG = Logger.getLogger(ActionBuilderAddress.class.getName());
	
	public ActionBuilderAddress()
	{
	}
				
	static Action buildListAction()
	{
		String               name = ActionBuilder.ADDRESS_LIST;
		String               desc = "Displays all addresses";
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
				AddressService       service = components.addressService();
				if (service == null)
				{
					String   msg = String.format(name + ": DI returned null service");
					LOG.severe(msg);
					return;
				}

				Consumer<DomainEntity>   printer = e -> System.out.println(e);
				System.out.println("addresses");
				service.apply(printer);
			}
		};
		
		return action;
	}
	
	static Action buildDeleteAction()
	{
		String               name = ActionBuilder.ADDRESS_DELETE;
		String               desc = "Deletes an address";
		String              usage = "delete id";
		ActionMetadata       meta = new ActionMetadata(name, desc, usage);
		Set<String>      required = new HashSet<>();
		Map<String, Object>   map = new HashMap<>();

		NodeSchema schema = SchemaManager.getInstance().getEntitySchema(Address.class);
		PropMeta   p_prop = schema.property(Address.PROP_ID);
		NodeFinder finder = NodeFinderEmpty.create(schema, p_prop);
		map.put(NeoTaskInfo.NODE_ONE_LOCATOR, finder);
		
		InputProcessor   ip = new InputProcessorNVPairs(map, required, null);
		Action       action = ActionBuilderBase.buildActionForDeleteCommand(meta, ip);
		
		return action;
	}
		
	static Action buildAddAction()
	{
		String               name = ActionBuilder.ADDRESS_ADD;
		String               desc = "Adds a new addr. Required params in name:value list";
		String              usage = "a address_line_one:{1570 Elmwood Ave.} address_line_two:{Apt 1004} address_city:Evanston address_state:IL address_zip:60201";
		ActionMetadata       meta = new ActionMetadata(name, desc, usage);
		Set<String>      required = new HashSet<>();
		Map<String, Object>   map = new HashMap<>();

		NodeSchema schema = SchemaManager.getInstance().getEntitySchema(Address.class);

		for (PropMeta pm : schema.properties(PropPredicate.predicateRequired()))
			required.add(pm.key());

		map.put(NeoTaskInfo.NODE_ONE_SCHEMA, schema);
	
		InputProcessor   ip = new InputProcessorNVPairs(map, required, null);
		Action       action = ActionBuilderBase.buildActionForAddCommand(meta, ip);
		
		return action;
	}

	static Action buildLinkPersonToAddressAction()
	{
		String               name = ActionBuilder.ADDRESS_ASSOCIATE;
		String               desc = "Associates a Person with an Address";
		String              usage = "assoc person_id:p123 address_id:2AE0377AFC97D26D6393798E0312AEFF assoc_type:HOME_ADDRESS";
		ActionMetadata       meta = new ActionMetadata(name, desc, usage);
		Set<String>      required = new HashSet<>();
		Map<String, Object>   map = new HashMap<>();

		required.add(Person.PROP_ID);
		required.add(Address.PROP_ID);

		NodeSchema  schema_person = SchemaManager.getInstance().getEntitySchema(Person.class);
		NodeSchema schema_address = SchemaManager.getInstance().getEntitySchema(Address.class);
		PropMeta           p_prop = schema_person.property(Person.PROP_ID);
		PropMeta           a_prop = schema_address.property(Address.PROP_ID);
		NodeFinder  finder_person = NodeFinderEmpty.create(schema_person,  p_prop);
		NodeFinder finder_address = NodeFinderEmpty.create(schema_address, a_prop);

		map.put(NeoTaskInfo.NODE_ONE_LOCATOR, finder_person);
		map.put(NeoTaskInfo.NODE_TWO_LOCATOR, finder_address);

		EdgeSchema     edge_schema = SchemaManager.getInstance().getEdgeSchema(RoostRelType.PERSON_ADDRESS);
		map.put(NeoTaskInfo.RELATIONSHIP_SCHEMA, edge_schema);
		
/*
		// Here we need to handle the special case of converting the name of an association type
		// into the RoostRelType object.
		//
		Map<String, Function<String, ?>> converters = getAddressTypeDecoder();
*/
		
		InputProcessor  ip = new InputProcessorNVPairs(map, required, null);
		Action      action = ActionBuilderBase.buildActionForLinkCommand(meta, ip);
		
		return action;
	}

//	// This method returns a function that can handle the special case of converting the name 
//	// of an address type into the RoostRelType object.
//	//
//	private static Map<String, Function<String, ?>> getAddressTypeDecoder()
//	{
//		Map<String, Function<String, ?>>  result = new HashMap<>();
//		Function<String, Object> address_type_decoder = new Function<String, Object>()
//		{
//			@Override
//			public Object apply(String t)
//			{
//				if (t == null || t.length() == 0)
//				{
//					String msg = "Unable to decode null value into valide address type. Using unspecified link";
//					LOG.warning(msg);
//					return RoostRelType.ADDRESS_UNSPECIFIED;
//				}
//				
//				if (t.equalsIgnoreCase(RoostRelType.ADDRESS_WORK.toString()))
//					return RoostRelType.ADDRESS_WORK;
//				
//				if (t.equalsIgnoreCase(RoostRelType.ADDRESS_HOME.toString()))
//					return RoostRelType.ADDRESS_HOME;
//				
//				String msg = String.format("Unable to decode address type: %s. Using unspecified link", t);
//				LOG.warning(msg);
//				return RoostRelType.ADDRESS_UNSPECIFIED;
//			}
//		
//		};
//			
//		result.put("assoc_type", address_type_decoder);
//
//		return result;
//	}
}
