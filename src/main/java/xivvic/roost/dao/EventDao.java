package xivvic.roost.dao;

import java.util.List;

import xivvic.roost.domain.Event;
import xivvic.roost.domain.Person;

public interface EventDao
	extends DomainEntityDao
{

	/**
	 * Return the event with given id.
	 * 
	 * @param name the name to match
	 * @return the event with the provided name, or null if none exist.
	 */
	Event findById(String id);

	/**
	 * Returns all the events in the system.
	 * 
	 * @return list of all events
	 */
	List<Event> list();
	
	List<Event> findByPerson(Person person);

	List<Event> findByPersonId(String pid);
	
}

