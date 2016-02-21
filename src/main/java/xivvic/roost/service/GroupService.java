package xivvic.roost.service;

import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xivvic.roost.dao.GroupDao;
import xivvic.roost.domain.DomainEntity;
import xivvic.roost.domain.Group;
import xivvic.roost.domain.Person;

public class GroupService
	implements DomainEntityContainer
{
	private final static Logger LOG = LoggerFactory.getLogger(GroupService.class.getName()); 
	private final GroupDao dao;
	
	@Inject
	public GroupService(GroupDao dao)
	{
		LOG.info(this.getClass().getSimpleName() + ": Created.");
		this.dao = dao;
	}
	

	public List<Group> list()
	{
		List<Group> groups = dao.list();

		return groups;
	}

	public List<Person> listMembers(String group_id)
	{
		List<Person> members = dao.findMembers(group_id);

		return members;
	}


	/**
	 * Looks up a group by its ID property
	 * 
	 * @param group_id the ID of the group to search for
	 * @return the group if found, or null if not
	 */
	public Group findById(String group_id)
	{
		if (group_id == null) return null;
		
		return dao.findById(group_id);
	}

	public Group findByName(String name)
	{
		if (name == null) return null;

		return dao.findByName(name);
	}


	@Override
	public boolean apply(Consumer<DomainEntity> consumer)
	{
		List<Group> groups = dao.list();

		groups.forEach(consumer);
		return true;
	}


		
}
