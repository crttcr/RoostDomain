package xivvic.roost.service;

import java.util.HashMap;
import java.util.Map;

public class ServiceLocator
{
	public static final String COMMAND_PROCESSOR   = "command.processor";
	
	// TODO: Now that I have DAOs.  Do I need an Object Repo?
	public static final String REPO_OBJECT          = "repo.object";
	public static final String SERVICE_ADDRESS      = "service.address";
	public static final String SERVICE_EVENT        = "service.event";
	public static final String SERVICE_LOGIN        = "service.login";
	public static final String SERVICE_USER         = "service.user";
	public static final String SERVICE_GROUP        = "service.group";
	public static final String SERVICE_PERSON       = "service.person";
	public static final String SERVICE_SUBSCRIPTION = "service.subscription";

	private ServiceLocator()
	{
	}

	private static ServiceLocator INSTANCE = new ServiceLocator();
	
	private static Map<String, Object> services = new HashMap<>();
	
	public static ServiceLocator locator()
	{
		return INSTANCE;
	}
	
	
	public void add(String handle, Object service)
	{
		ServiceLocator.services.put(handle, service);
	}
	
	public Object get(String handle)
	{
		return ServiceLocator.services.get(handle);
	}

}
