package xivvic.roost.app.login;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import xivvic.roost.dao.UserDao;

public class LoginService
{
	private final UserDao dao;
	
	private Map<String, LoginInformation> active_users = new HashMap<>();
	private static Map<String, String> user_map = new HashMap<>();
	
	static
	{
		// TODO: Need to have a robust approach to user identity
		//
		user_map.put("reid", "abc");
		user_map.put("rana", "abc");
	}
	
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
	public String login(String user_name, String secret)
	{
		if (user_name == null)
			return null;

		if (secret == null)
			return null;

		String stored_secret = user_map.get(user_name);
		
		if (stored_secret == null)
			return null;
		
		if (! secret.equals(stored_secret))
			return null;
		
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
