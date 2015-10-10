package xivvic.roost.app.login;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import xivvic.roost.dao.UserDao;
import xivvic.roost.domain.User;
import xivvic.util.PasswordUtil;

public class LoginService
{
	private final static Logger LOG = Logger.getLogger(LoginService.class.getName()); 
	
	private Map<String, LoginInformation> active_users = new HashMap<>();
	private final UserDao dao;
	
	public LoginService(UserDao dao)
	{
		this.dao = dao;
	}

	/**
	 * Attempt to login the given user by evaluating the secret against
	 * the stored secret associated with this user.  If the secret matches,
	 * a session key is provided.
	 * 
	 * 
	 * @param name the user name with which to login
	 * @param secret the authentication token to test against the database
	 * 
	 * @return a session key upon success, and null if failure
	 */
	public String login(String user_name, String password)
	{
		if (user_name == null)
			return null;

		if (password == null)
			return null;

		User user = dao.findByUserName(user_name);
		if (user == null)
		{
			String msg = String.format("User [%s] not found", user_name);
			LOG.info(msg);
			return null;
		}
		
		String stored_secret = user.passhash();
		if (stored_secret == null)
		{
			String msg = String.format("User [%s] not found", user_name);
			LOG.info(msg);
			return null;
		}
		
		boolean match = false;
		try
		{
			match = PasswordUtil.check(password, stored_secret);
		}
		catch (Exception e)
		{
			String msg = String.format("Exception checking password: %s", e.getLocalizedMessage());
			LOG.info(msg);
			return null;
		}
		
		if (! match)
		{
			String msg = String.format("User password failed check.");
			LOG.info(msg);
			return null;
		}
		
		String key = UUID.randomUUID().toString();
		
		LoginInformation li = LoginInformation.builder()
				.user(user_name)
				.sessionKey(key)
				.build();
		
		active_users.put(key, li);
		
		return key;
	}
	
	/**
	 * Update the lastActivity time for a given user
	 * 
	 * @param name
	 * @return
	 */
	public boolean touch(String name)
	{
		if (name == null)
			return false;
		
		LoginInformation li = active_users.get(name);
		
		if (li == null)
			return false;
		
		LocalDateTime now = LocalDateTime.now();
		li.setLastActivity(now);
		return true;
	}
}
