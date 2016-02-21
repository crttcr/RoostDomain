package xivvic.roost.service;

import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xivvic.roost.dao.UserDao;
import xivvic.roost.domain.DomainEntity;
import xivvic.roost.domain.User;

public class UserService
	implements DomainEntityContainer
{
	private final static Logger LOG = LoggerFactory.getLogger(UserService.class.getName());
	private final UserDao dao;
	
	@Inject
	public UserService(UserDao dao)
	{
		LOG.info(this.getClass().getSimpleName() + ": Created.");
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
