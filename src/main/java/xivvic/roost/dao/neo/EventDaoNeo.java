package xivvic.roost.dao.neo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

import xivvic.neotest.program.RoostNodeType;
import xivvic.neotest.program.RoostRelType;
import xivvic.roost.dao.EventDao;
import xivvic.roost.domain.Event;
import xivvic.roost.domain.EventType;
import xivvic.roost.domain.Person;

public class EventDaoNeo
	extends DaoNeo
	implements EventDao
{
	private final static Logger LOG = Logger.getLogger(EventDaoNeo.class.getName()); 
	
	public EventDaoNeo(GraphDatabaseService gdb)
	{
		super(gdb);
	}
	
	/**
	 * Returns empty list if not found.  Otherwise returns a list of events
	 * 
	 */
	@Override
	public List<Event> findByPerson(Person person)
	{
		if (person == null)
			return Event.EMPTY_LIST;
		
		return findByPersonId(person.id());
	}

	/**
	 * Returns empty list if not found.  Otherwise returns a list of events
	 * 
	 */

	@Override
	public List<Event> findByPersonId(String pid)
	{
		if (pid == null)
			return Event.EMPTY_LIST;
		
		Label      person_label = RoostNodeType.PERSON;
		RelationshipType r_type = RoostRelType.EVENT_PROGENITOR;
		GraphDatabaseService db = db();

		try (Transaction tx = db.beginTx() )
		{
			Node p_node = db.findNode(person_label, Person.PROP_ID, pid);
			if (p_node == null)
			{
				String msg = String.format("Unable to locate person with id = [%s]", pid);
				LOG.warning(msg);
				tx.success();
				return Event.EMPTY_LIST;
			}
			
			Iterable<Relationship> links_to_events = p_node.getRelationships(r_type);

			// What to do with each found Event node?
			// Stuff it in a list
			//
			final List<Event>             result = new ArrayList<>();
			Consumer<Relationship> event_creator = (r) -> 
			{
				Node e_node = r.getOtherNode(p_node);
				Event event = node2Event(e_node);
				result.add(event);
			};

			links_to_events.forEach(event_creator);
			tx.success();
			
			return result;
		}
	}


	@Override
	public List<Event> list()
	{
		Label                   label = RoostNodeType.EVENT;
		GraphDatabaseService       db = db();
		final List<Event>      result = new ArrayList<>();

		try (Transaction tx = db.beginTx() )
		{
			ResourceIterator<Node> nodes = db.findNodes(label);

			Consumer<Node> action = (n) -> 
			{
				Event event = node2Event(n);
				result.add(event);
			};
			
			nodes.forEachRemaining(action);
			nodes.close();
			tx.success();
		}

		return result;
	}


	Event node2Event(Node node)
	{
		String id   = (String) node.getProperty(Event.PROP_ID);
		String text = (String) node.getProperty(Event.PROP_TEXT);
		String type = (String) node.getProperty(Event.PROP_TYPE);
		Long   time = (Long)   node.getProperty(Event.PROP_TIME, null);
		Long   date = (Long)   node.getProperty(Event.PROP_DATE, null);
		
		EventType e_type = EventType.valueOf(type);
		LocalTime e_time = time == null ? null : LocalTime.ofNanoOfDay(time);
		LocalDate e_date = date == null ? null : LocalDate.ofEpochDay(date);
		
		Event event = Event.create(id, e_date, e_time, e_type, text);

		return event;
	}



	@Override
	public Event findById(String event_id)
	{
		Label                label = RoostNodeType.EVENT;
		String                 key = Event.PROP_ID;
		String               value = event_id;
		Node                  node = null;
		GraphDatabaseService    db = db();

		try (Transaction tx = db.beginTx() )
		{
			node = db.findNode(label, key, value);
			tx.success();
			
			if (node == null)
			{
				tx.success();
				return null;
			}
			
			Event event = node2Event(node);
			return event;
		}
	}

}
