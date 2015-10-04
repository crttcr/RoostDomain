package xivvic.roost.console;

import java.util.HashMap;
import java.util.Map;

import xivvic.roost.domain.User;

public class ProgramState
{
	private static String session_key;
	private static boolean trace_enabled;
	
	private static Map<String, Map<String, Object>> sessionMap = new HashMap<>();

	public static boolean isTraceEnabled()
	{
		return trace_enabled;
	}

	public static void setTraceEnabled(boolean trace_enabled)
	{
		ProgramState.trace_enabled = trace_enabled;
	}

	public static String getSessionKey()
	{
		return session_key;
	}
	
	public static void setSessionKey(String key)
	{
		session_key = key;
	}
	
	public static User getUser(String session_key)
	{
		Map<String, Object> session = sessionMap.get(session_key);
		
		if (session == null)
			return null;
		
		User user = (User) session.get(User.class.getCanonicalName());
		
		return user;
	}

	public static User setUser(String session_key, User user)
	{
		Map<String, Object> session = sessionMap.get(session_key);
		
		if (session == null)
		{
			return null;
			
		}
		
		String    key = User.class.getCanonicalName();
		User previous = (User) session.get(key);
		session.put(key, user);
		
		return previous;
	}
	
}
