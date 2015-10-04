package xivvic.roost.dao;

import java.util.List;

import xivvic.roost.domain.Person;

public interface PersonDao
	extends DomainEntityDao
{

	/**
	 * Returns all the people in the system.
	 * 
	 * @return list of all people
	 */
	List<Person> list();

	/**
	 * Looks up a person by the ID property
	 * 
	 * @param person_id the ID of the person to search for
	 * @return the person if found, or null if not
	 */
	public Person findById(String person_id);

}
