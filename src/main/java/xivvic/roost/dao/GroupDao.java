package xivvic.roost.dao;

import java.util.List;

import xivvic.roost.domain.Group;
import xivvic.roost.domain.Person;

public interface GroupDao
	extends DomainEntityDao
{

	/**
	 * Return the unique group with the matching (case insensitive) name.
	 * 
	 * @param name the name to match
	 * @return the group with the provided name, or null if none exist.
	 */
	Group findByName(String name);

	/**
	 * Returns all the groups in the system.
	 * 
	 * @return list of all groups
	 */
	List<Group> list();

	/**
	 * Looks up a Group by the ID property
	 * 
	 * @param group_id the ID of the person to search for
	 * @return the group if found, or null if not
	 */
	public Group findById(String group_id);
	
	// Retrieve the related people for the specified group
	//
	public List<Person> findMembers(String group_id);
}

