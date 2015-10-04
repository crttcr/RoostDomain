package xivvic.roost.dao;

import java.util.List;

import xivvic.roost.domain.User;

public interface UserDao
	extends DomainEntityDao
{
	/**
	 * Return the unique user with the matching (case insensitive) email.
	 * 
	 * @param email the email to match
	 * @return the user with the provided email, or null if none exist.
	 */
	User findByEmail(String email);

	User findByUserName(String u_name);

	/**
	 * Returns all the users in the system.
	 * 
	 * @return list of all users
	 */
	List<User> list();
}
