package xivvic.roost.service;

import java.util.List;
import java.util.logging.Logger;

import xivvic.roost.dao.ValueObjectBuilder;
import xivvic.roost.domain.Address;
import xivvic.roost.domain.Event;
import xivvic.roost.domain.Group;
import xivvic.roost.domain.Person;
import xivvic.roost.domain.resolver.EntityResolver;
import xivvic.roost.domain.resolver.ObjectRepository;
import xivvic.roost.domain.resolver.TabSeparatedStringValueProvider;
import xivvic.roost.domain.resolver.ValueProvider;

public class DomainObjectLoader
{
	private final static Logger LOG = Logger.getLogger(DomainObjectLoader.class.getName()); 

	public DomainObjectLoader()
	{
		
	}
	public void loadDomainObjects()
	{
		loadPeople();
		loadEvents();
		int count = loadGroups();
		
		LOG.fine("Loaded " + count + " groups.");
		
		loadAddresses();
	}

	public void loadAddresses()
	{
		LOG.fine("Loading physical address data");
		
		String              addressData = loadAddressData();
		ValueProvider   addressProvider = new TabSeparatedStringValueProvider(addressData);
		ServiceLocator          locator = ServiceLocator.locator();
		ObjectRepository    object_repo = (ObjectRepository) locator.get(ServiceLocator.REPO_OBJECT);
		ValueObjectBuilder          vob = new ValueObjectBuilder();
		List<Address> addresses = vob.createPhysicalAddresses(addressProvider);
		
		for (Address pa : addresses)
			object_repo.addToRepository(pa.id(), pa);
	}

	public void loadPeople()
	{
		LOG.fine("Loading person data");
		
		String            peopleData = loadPeopleData();
		ValueProvider peopleProvider = new TabSeparatedStringValueProvider(peopleData);
		ServiceLocator       locator = ServiceLocator.locator();
		ObjectRepository object_repo = (ObjectRepository) locator.get(ServiceLocator.REPO_OBJECT);
		ValueObjectBuilder       vob = new ValueObjectBuilder();
		List<Person>          people = vob.createPeople(peopleProvider);
		
		for (Person p : people)
			object_repo.addToRepository(p.id(), p);
	}

	public void loadEvents()
	{
		LOG.fine("Loading event data");
		
		String             eventData = loadEventData();
		ValueProvider  eventProvider = new TabSeparatedStringValueProvider(eventData);
		ServiceLocator       locator = ServiceLocator.locator();
		ObjectRepository object_repo = (ObjectRepository) locator.get(ServiceLocator.REPO_OBJECT);
		ValueObjectBuilder       vob = new ValueObjectBuilder();
		List<Event>           events = vob.createEvents(eventProvider);
		
		for (Event e : events)
			object_repo.addToRepository(e.id(), e);
	}

	public int loadGroups()
	{
		LOG.fine("Loading group data");
		
		String                groupData = loadGroupData();
		ValueProvider     groupProvider = new TabSeparatedStringValueProvider(groupData);
		ServiceLocator          locator = ServiceLocator.locator();
		ObjectRepository    object_repo = (ObjectRepository) locator.get(ServiceLocator.REPO_OBJECT);
		EntityResolver<Person> resolver = new EntityResolver<>(object_repo);
		ValueObjectBuilder          vob = new ValueObjectBuilder();
		List<Group>              groups = vob.createGroups(groupProvider, resolver);
		
		for (Group g : groups)
			object_repo.addToRepository(g.id(), g);
		
		return groups.size();
	}

	private String loadEventData()
	{
		String input = 
				"event_date\tevent_type\tevent_text\n"                          +
				"2015-06-13\tANNIVERSARY\tSomeANIV\n"  +
				"1971-04-28\tBIRTHDAY\tRana's Birthday\n"       +
				"2015-06-13\tBIRTHDAY\tBab's Birthday"          ;
		
		return input;
	}

	private String loadGroupData()
	{
		String input = "group_id\tgroup_name\tgroup_members\n"            +
				"XYZ\tA Test Group\t12345|Ryan|Doug"     ;

		return input;
	}

	private String loadPeopleData()
	{
		String input = "id\tfirstName\tmiddleName\tlastName\tnickname\n" +
				"12345\tCarlton\tReid\tTurner\tReid\n"                     +
				"23456\tRana\t\tLee\t\n"                                   +
				"34567\tDouglas\tChristopher\tTurner\tDoug";

		return input;
	}
	

	private String loadAddressData()
	{
		String input = "id\tlineOne\tlineTwo\tcity\tstate\tzip\n"      +
				"ABC\t1570 Elmwood Ave\tApt. 1004\tEvanston\tIL\t60201\n" +
				"\t2329 N. Leavitt St.\tUnit 3\tChicago\tIL\t\n" +
				"CDE\t2725 Paran Valley Rd.\t\tAtlanta\tGA\t30327";
		return input;
	}
	
	

}
