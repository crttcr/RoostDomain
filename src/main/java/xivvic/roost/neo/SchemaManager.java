package xivvic.roost.neo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.RelationshipType;

import xivvic.neotest.program.RoostNodeType;
import xivvic.neotest.program.RoostRelType;
import xivvic.roost.domain.Address;
import xivvic.roost.domain.Event;
import xivvic.roost.domain.Group;
import xivvic.roost.domain.Person;
import xivvic.roost.domain.Subscription;
import xivvic.roost.domain.User;

public class SchemaManager
	implements DomainSchema
{
	private Map<RelationshipType, EdgeSchema> edges = new HashMap<>();
	private Map<Class<?>, NodeSchema> entities = new HashMap<>();
	private static SchemaManager INSTANCE = new SchemaManager();
	private SchemaManager()
	{
		init();
	}
	
	public static DomainSchema getInstance()
	{
		return INSTANCE;
	}

	@Override
	public List<Class<?>> entityTypes()
	{
		return new ArrayList<Class<?>>(entities.keySet());
	}

	@Override
	public EdgeSchema getEdgeSchema(RelationshipType type)
	{
		if (type == null)
			return null;
		
		return edges.get(type);
	}

	@Override
	public NodeSchema getEntitySchema(Class<?> cls)
	{
		if (cls == null)
			return null;
		
		return entities.get(cls);
	}

	private void init()
	{
		buildAddress();
		buildEvent();
		buildGroup();
		buildPerson();
		buildSubscription();
		buildUser();
		
		buildEdges();
	}

	private void buildEdges()
	{
		List<PropMeta> p_list = new ArrayList<>();

		// Don't have any edge properties defined at the moment
		// Except for Type on address
		//
		RoostRelType[] edge_types = RoostRelType.values();
		
		for (RoostRelType e_type : edge_types)
		{
			if (e_type == RoostRelType.PERSON_ADDRESS)
			{
				PropMeta a = PropMetaBase.builder()
						.type(String.class)
						.name("type")
						.key("address_type")
						.unique(false)
						.required(true)
						.build();

				List<PropMeta> a_list = new ArrayList<>();
				a_list.add(a);
				EdgeSchema es = new EdgeSchemaImpl(e_type, a_list);
				edges.put(e_type, es);
				continue;
			}

			EdgeSchema schema = new EdgeSchemaImpl(e_type, p_list);
			
			edges.put(e_type, schema);
		}
	}
	
	private void buildAddress()
	{
		List<PropMeta> p_list = new ArrayList<>();
		
		PropMeta a = PropMetaBase.builder()
				.type(String.class)
				.name("id")
				.key("address_id")
				.unique(true)
				.required(true)
				.build();
		PropMeta b = PropMetaBase.builder()
				.type(String.class)
				.name("line one")
				.key("address_line_one")
				.unique(false)
				.required(true)
				.build();
		PropMeta c = PropMetaBase.builder()
				.type(String.class)
				.name("line two")
				.key("address_line_two")
				.unique(false)
				.required(false)
				.build();
		PropMeta d = PropMetaBase.builder()
				.type(String.class)
				.name("city")
				.key("address_city")
				.unique(false)
				.required(false)
				.build();
		PropMeta e = PropMetaBase.builder()
				.type(String.class)
				.name("state")
				.key("address_state")
				.unique(false)
				.required(false)
				.build();
		PropMeta f = PropMetaBase.builder()
				.type(String.class)
				.name("zip")
				.key("address_zip")
				.unique(false)
				.required(false)
				.build();
			
		p_list.add(a);
		p_list.add(b);
		p_list.add(c);
		p_list.add(d);
		p_list.add(e);
		p_list.add(f);
		
		NodeSchema schema = new NodeMetaImmutable(RoostNodeType.ADDRESS, p_list);
		
		entities.put(Address.class, schema);
	}

	private void buildEvent()
	{
		List<PropMeta> p_list = new ArrayList<>();
		
		PropMeta a = PropMetaBase.builder()
				.type(String.class)
				.name("id")
				.key("event_id")
				.unique(true)
				.required(true)
				.build();
		PropMeta b = PropMetaBase.builder()
				.type(String.class)
				.name("type")
				.key("event_type")
				.unique(false)
				.required(true)
				.build();
		PropMeta c = PropMetaBase.builder()
				.type(String.class)
				.name("text")
				.key("event_text")
				.unique(false)
				.required(true)
				.build();
		PropMeta d = PropMetaBase.builder()
				.type(String.class)
				.name("date")
				.key("event_date")
				.unique(false)
				.required(false)
				.build();
		PropMeta e = PropMetaBase.builder()
				.type(String.class)
				.name("time")
				.key("event_time")
				.unique(false)
				.required(false)
				.build();
			
		p_list.add(a);
		p_list.add(b);
		p_list.add(c);
		p_list.add(d);
		p_list.add(e);
		
		NodeSchema schema = new NodeMetaImmutable(RoostNodeType.EVENT, p_list);
		
		entities.put(Event.class, schema);
	}

	private void buildGroup()
	{
		List<PropMeta> p_list = new ArrayList<>();
		
		PropMeta a = PropMetaBase.builder()
				.type(String.class)
				.name("id")
				.key("group_id")
				.unique(true)
				.required(true)
				.build();
		PropMeta b = PropMetaBase.builder()
				.type(String.class)
				.name("name")
				.key("group_name")
				.unique(true)
				.required(true)
				.build();
			
		p_list.add(a);
		p_list.add(b);
		
		NodeSchema schema = new NodeMetaImmutable(RoostNodeType.GROUP, p_list);
		
		entities.put(Group.class, schema);
	}

	private void buildSubscription()
	{
		List<PropMeta> p_list = new ArrayList<>();
		
		PropMeta a = PropMetaBase.builder()
				.type(String.class)
				.name("id")
				.key("subs_id")
				.unique(true)
				.required(true)
				.build();
		PropMeta b = PropMetaBase.builder()
				.type(String.class)
				.name("expiry")
				.key("subs_expiry")
				.unique(true)
				.required(true)
				.build();
			
		p_list.add(a);
		p_list.add(b);
		
		NodeSchema schema = new NodeMetaImmutable(RoostNodeType.SUBSCRIPTION, p_list);
		
		entities.put(Subscription.class, schema);
	}

	public static final String    PROP_ID = "user_id";
	public static final String PROP_EMAIL = "user_email";
	public static final String PROP_UNAME = "user_username";
	public static final String PROP_PHASH = "user_passhash";
	public static final String  PROP_SALT = "user_salt";

	private void buildUser()
	{
		List<PropMeta> p_list = new ArrayList<>();
		
		PropMeta a = PropMetaBase.builder()
				.type(String.class)
				.name("id")
				.key("user_id")
				.unique(true)
				.required(true)
				.build();
		PropMeta b = PropMetaBase.builder()
				.type(String.class)
				.name("email")
				.key("user_email")
				.unique(true)
				.required(true)
				.build();
		PropMeta c = PropMetaBase.builder()
				.type(String.class)
				.name("user name")
				.key("user_username")
				.unique(true)
				.required(true)
				.build();
		PropMeta d = PropMetaBase.builder()
				.type(String.class)
				.name("password hash")
				.key("user_passhash")
				.unique(true)
				.required(true)
				.build();
		PropMeta e = PropMetaBase.builder()
				.type(String.class)
				.name("salt")
				.key("user_salt")
				.unique(true)
				.required(true)
				.build();
			
		p_list.add(a);
		p_list.add(b);
		p_list.add(c);
		p_list.add(d);
		p_list.add(e);
		
		NodeSchema schema = new NodeMetaImmutable(RoostNodeType.USER, p_list);
		
		entities.put(User.class, schema);
	}


	private void buildPerson()
	{
		List<PropMeta> p_list = new ArrayList<>();
		
		PropMeta a = PropMetaBase.builder()
				.type(String.class)
				.name("id")
				.key("person_id")
				.unique(true)
				.required(true)
				.build();

		PropMeta b = PropMetaBase.builder()
				.type(String.class)
				.name("first name")
				.key("person_name")
				.unique(false)
				.required(true)
				.build();
		PropMeta c = PropMetaBase.builder()
				.type(String.class)
				.name("last name")
				.key("person_name_last")
				.unique(false)
				.required(true)
				.build();
		PropMeta d = PropMetaBase.builder()
				.type(String.class)
				.name("middle name")
				.key("person_name_middle")
				.unique(false)
				.required(false)
				.build();
		PropMeta e = PropMetaBase.builder()
				.type(String.class)
				.name("nickname")
				.key("person_nickname")
				.unique(false)
				.required(false)
				.build();
			
		p_list.add(a);
		p_list.add(b);
		p_list.add(c);
		p_list.add(d);
		p_list.add(e);
		
		NodeSchema schema = new NodeMetaImmutable(RoostNodeType.PERSON, p_list);
		
		entities.put(Person.class, schema);
	}
}
