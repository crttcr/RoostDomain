package xivvic.roost.dao;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import xivvic.roost.domain.Address;
import xivvic.roost.domain.Event;
import xivvic.roost.domain.EventType;
import xivvic.roost.domain.Group;
import xivvic.roost.domain.Person;
import xivvic.roost.domain.resolver.EntityResolver;
import xivvic.roost.domain.resolver.ValueProvider;

public class ValueObjectBuilder
{
	public static final String RECORD_SEPARATOR = "\n";
	public static final String FIELD_SEPARATOR = "\t";

	private final static Logger LOG = Logger.getLogger(ValueObjectBuilder.class.getName()); 
	private final static DateTimeFormatter DATE_FORMAT     = DateTimeFormatter.ISO_DATE;
	private final static DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ISO_DATE_TIME;
	private final static DateTimeFormatter TIME_FORMAT     = DateTimeFormatter.ISO_TIME;
	
	public ValueObjectBuilder()
	{
		// this.entityResolver = er;
	}
	
	
// This was an attempt to build a function table mapping
// Builder's field names to an object that would call the appropriate
// method on a Builder instance.
//
// This approach would create too many object definitions -- one per field in every domain object
//
//
//	private static Map<String, Function<String, Boolean>> functions = new HashMap<>();
//	static
//	{
//		Function<String, Boolean> function = new Function<String, Boolean>() 
//			{
//				private Person.Builder builder;
//
//				public void setBuilder(Person.Builder builder)
//				{
//					this.builder = builder;
//				}
//				
//
//				@Override
//				public Boolean apply(String t)
//				{
//					builder.firstName(t);
//					return Boolean.TRUE;
//				}
//			
//			};
//			
//			functions.put("foo", function);
//		
//	}
	

	/**
	 * Create a person object from a data encoded string
	 * Assumes first line contains field names as a header line
	 * Assumes the subsequent lines contain data consistent with the ordering of the header's fields
	 * 
	 * @param input
	 * @return
	 */
	public List<Person> createPeople(ValueProvider vp)
	{
		if (vp == null)
			throw new NullPointerException("Expecting non-null ValueProvider");
		
		List<Person> result = new ArrayList<>();
		int        dataRows = vp.getDataRowCount();
		
		for (int i = 0; i < dataRows; i++)
		{
			Map<String, String> map = vp.getValueMap(i);
         Person                p = createPerson(map);

         result.add(p);
		}

		return result;
	}

	/**
	 * Create a person object from a data encoded string
	 * Assumes first line contains field names as a header line
	 * Assumes the subsequent lines contain data consistent with the ordering of the header's fields
	 * 
	 * @param input
	 * @return
	 */
	private Person createPerson(Map<String, String> map)
	{
		Person.Builder builder = Person.builder();
		String              id = UUID.randomUUID().toString();
		builder.id(id);
		
		String[] keys = map.keySet().toArray(new String[0]);
		for (String key : keys)
		{
			String value = map.get(key);
			if (value == null)
				continue;
			
			reflectivelyCallBuilderMethod(builder, key, value);
		}

		Person p = builder.build();

		return p;
	}


	private void reflectivelyCallBuilderMethod(Object builder, String field, String value)
	{
      if (value == null || value.length() == 0)
      	return;

     	boolean ok = callMethodByName(builder, field, value);
     	
     	if (ok)
     	{
     		String msg = String.format("%s -> %s", field, value);
     		LOG.fine(msg);
     	}
     	else
     	{
     		String msg = String.format("FAIL (reflective call): field -> %s, value -> %s", field, value);
     		LOG.fine(msg);
     	}
	}

	private boolean callMethodByName(Object builder, String name, String value)
	{
		java.lang.reflect.Method method = null;
		try 
		{
		  method = builder.getClass().getMethod(name, String.class);
		  if (method == null)
			  return false;

		  method.setAccessible(true);
		} 
		catch (SecurityException e) 
		{
			System.err.println(e);
			return false;
		}
		catch (NoSuchMethodException e) 
		{
			System.err.println(e);
			return false;
		}

		try 
		{
			method.invoke(builder, value);
		} 
		catch (IllegalArgumentException e) 
		{
			System.err.println(e);
			return false;
		} 
		catch (IllegalAccessException e) 
		{
			System.err.println(e);
			return false;
		} 
		catch (InvocationTargetException e) 
		{		
			System.err.println(e);
			return false;
		}

		return true;
	}

		
	public List<Address> createPhysicalAddresses(ValueProvider vp)
	{
		if (vp == null)
			throw new NullPointerException("Expecting non-null value provider");
		
		List<Address> result = new ArrayList<>();
		int dataRows = vp.getDataRowCount();
		
		for (int i = 0; i < dataRows; i++)
		{
			Map<String, String> map = vp.getValueMap(i);
         Address      pa = createPhysicalAddress(map);

         result.add(pa);
		}

		return result;
	}

	private Address createPhysicalAddress(Map<String, String> map)
	{
		Address.Builder builder = Address.builder();

		String[] keys = map.keySet().toArray(new String[0]);
		for (String key : keys)
		{
			String value = map.get(key);
			if (value == null)
				continue;
			
			reflectivelyCallBuilderMethod(builder, key, value);
		}

		Address pa = builder.build();

     	// System.out.println("Built Physical Address: " + pa);
		return pa;

	}
	
	public Event createEvent(Map<String, String> map)
	{
      String          id = UUID.randomUUID().toString();
     	LocalDate     date = LocalDate.now();
     	LocalTime     time = LocalTime.now();
     	EventType     type = null;
     	String        text = map.get(Event.PROP_TEXT);

     	String s = map.get(id);
     	
     	// ID
     	//
     	if (s != null)
     		id = s;
     	
     	s = map.get(Event.PROP_DATE);
     	if (s != null)
     	{
     		LocalDate ld = LocalDate.parse(s, DATE_FORMAT);
     		if (ld != null)
     			date = ld;
     	}

     	s = map.get(Event.PROP_TIME);
     	if (s != null)
     	{
     		LocalTime lt = LocalTime.parse(s);
     		if (lt != null)
     			time = lt;
     	}

     	s = map.get(Event.PROP_TYPE);
     	if (s != null)
     		type = EventType.valueOf(s.toUpperCase());
     	

      Event e = Event.create(id, date, time, type, text);

     	if (e == null)
     	{
     		String msg = String.format("Unable to obtain temporal coordinate.  Cannot create event");
     		LOG.warning(msg);
     		return null;
     	}

		return e;
	}
	

	public List<Event> createEvents(ValueProvider vp)
	{
		if (vp == null)
			throw new NullPointerException("Expecting non-null input");
		
		List<Event> result = new ArrayList<>();
		int dataRows = vp.getDataRowCount();
		
		for (int i = 0; i < dataRows; i++)
		{
			Map<String, String> map = vp.getValueMap(i);
			Event e = createEvent(map);

			if (e != null)
				result.add(e);
		}

		return result;
	}

	public List<Group> createGroups(ValueProvider vp, EntityResolver<Person> er)
	{
		if (vp == null)
			throw new NullPointerException("Expecting non-null value provider");
		
		List<Group> result = new ArrayList<>();
		int dataRows = vp.getDataRowCount();
		
		for (int i = 0; i < dataRows; i++)
		{
			Map<String, String> map = vp.getValueMap(i);
			Group g = createGroup(map, er);

			if (g != null)
				result.add(g);
		}

		return result;
	}

	private Group createGroup(Map<String, String> map, EntityResolver<Person> personResolver)
	{
     	Group.Builder builder = Group.builder();
      String      id = UUID.randomUUID().toString();
     	builder.id(id);

     	String    name = map.get("name");
     	if (name == null)
     		name = "Name not provided";
     	builder.name(name);

     	String s = map.get("id");
     	// ID
     	//
     	if (s != null)
     		builder.id(s);
     		id = s;
     	
//     	s = map.get("members");
//     	if (s != null)
//      {
//      	String[] person_ref = s.split("\\|");
//      	List<Person> people = new ArrayList<>();
//
//      	for (String person_id : person_ref)
//      	{
//      		ResolutionResult<Person> result = personResolver.resolveEntityById(person_id);
//      		
//      		if (result.wasSuccessful())
//      		{
//      			Person p = result.getResolvedObject();
//      			people.add(p);
//      		}
//      		else
//      		{
//      			String msg = "Unable to resolve person identified by: " + person_id + ".  No addition to Group [" + name + "] members";
//      			LOG.warning(msg);
//      		}
//      	}
//      		
//      	builder.members(people);
//      }
//      
      Group  g = builder.build();
		return g;
	}
	

}
