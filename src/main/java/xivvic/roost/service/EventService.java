package xivvic.roost.service;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import xivvic.roost.dao.EventDao;
import xivvic.roost.domain.DomainEntity;
import xivvic.roost.domain.Event;

public class EventService 
	implements DomainEntityContainer
{
	private final static Logger LOG = Logger.getLogger(EventService.class.getName());
	private final EventDao dao;
	
	public EventService(EventDao dao)
	{
		LOG.fine(this.getClass().getSimpleName() + ": Created.");
		this.dao = dao;
	}
	

	public List<Event> list()
	{
		List<Event> events = dao.list();

		return events;
	}

	/**
	 * Looks up a event by the ID property
	 * 
	 * @param event the ID of the event to search for
	 * @return the event if found, or null if not
	 */
	public Event findById(String event_id)
	{
		if (event_id == null) 
			return null;
		
		return dao.findById(event_id);
	}


	@Override
	public boolean apply(Consumer<DomainEntity> consumer)
	{
		List<Event> events = dao.list();
		
		events.forEach(consumer);
		return true;
	}


	public List<Event> eventsForPerson(String pid)
	{
		if (pid == null)
			return Event.EMPTY_LIST;
		
		
		return dao.findByPersonId(pid);
	}

}
