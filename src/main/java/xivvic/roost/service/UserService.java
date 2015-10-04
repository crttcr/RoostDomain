package xivvic.roost.service;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import xivvic.roost.dao.UserDao;
import xivvic.roost.domain.DomainEntity;
import xivvic.roost.domain.User;

public class UserService
	implements DomainEntityContainer
{
	private final static Logger LOG = Logger.getLogger(UserService.class.getName());
	private final UserDao dao;
	
	public UserService(UserDao dao)
	{
		LOG.fine(this.getClass().getSimpleName() + ": Created.");
		this.dao = dao;
	}
	

	public List<User> list()
	{
		List<User> users = dao.list();

		return users;
	}

	public User findByUserName(String u_name)
	{
		return dao.findByUserName(u_name);
	}

	@Override
	public boolean apply(Consumer<DomainEntity> consumer)
	{
		List<User> users = dao.list();

		users.forEach(consumer);
		return true;
	}

}
