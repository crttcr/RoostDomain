package xivvic.roost.console;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import xivvic.console.action.Action;

public class ActionRegistry
{
	private final static Logger LOG = Logger.getLogger(ActionRegistry.class.getName()); 
	
	
	private static Map<String, Action> registry = new HashMap<>();
	
	
	public synchronized static Action get(String name)
	{
		if (name == null)
		{
			String msg = "Attempt to retrieve action using a null name";
			LOG.warning(msg);
			return null;
		}
		
		return registry.get(name);
	}
	
	/**
	 * Attempts to set a registered action's "is_enabled" property to the provided boolean value.
	 * Returns true if successful, false otherwise.
	 * 
	 * 
	 * @param new_state the true/false value to set on the registered action
	 * @return true upon successfully setting the value, false if the value could not be set
	 * 
	 */
	public synchronized static boolean setActionEnablement(String name, boolean new_state)
	{
		Action a = get(name);
		
		if (a == null)
			return false;
		
		if (new_state == true)
			a.enable();
		else
			a.disable();
		
		return true;
	}
	
	/**
	 * Stores an action in the registry.  If one already exists, it is returned
	 * by this method. Actions are stored by their name, so it is expected that
	 * they are unique.
	 * 
	 * 
	 * @param name the name to associate with this action.
	 * @param action
	 * @return any previous action associated with the name of the current action
	 */
	public synchronized static Action put(Action action)
	{
		if (action == null)
		{
			String msg = "Attempt to store a null action";
			LOG.warning(msg);
			return null;
		}
		
		String name = action.name();
		if (name == null)
		{
			String msg = "Action to be stored has a null name.  Not storing";
			LOG.warning(msg);
			return null;
		}
		
		Action previous = registry.get(name);
		
		registry.put(name, action);

		return previous;
	}

}
