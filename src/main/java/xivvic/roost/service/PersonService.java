package xivvic.roost.service;

import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xivvic.roost.dao.PersonDao;
import xivvic.roost.domain.DomainEntity;
import xivvic.roost.domain.Person;

public class PersonService 
	implements DomainEntityContainer
{
	private final static Logger LOG = LoggerFactory.getLogger(PersonService.class.getName());
	private final PersonDao dao;
	
	public PersonService(PersonDao dao)
	{
		LOG.info(this.getClass().getSimpleName() + ": Created.");
		this.dao = dao;
	}
	
	public List<Person> list()
	{
		List<Person> people = dao.list();

		return people;
	}

	/**
	 * Looks up a person by the ID property
	 * 
	 * @param person_id the ID of the person to search for
	 * @return the person if found, or null if not
	 */
	public Person findById(String person_id)
	{
		if (person_id == null) return null;
		
		return dao.findById(person_id);
	}

	@Override
	public boolean apply(Consumer<DomainEntity> consumer)
	{
		List<Person> people = dao.list();

		people.forEach(consumer);
		return true;
	}

}
