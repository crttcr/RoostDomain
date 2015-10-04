package xivvic.roost.console;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import xivvic.console.action.Action;
import xivvic.console.action.ActionBase;
import xivvic.roost.domain.Event;
import xivvic.roost.domain.Group;
import xivvic.roost.domain.Person;
import xivvic.roost.domain.Subscription;
import xivvic.roost.domain.User;
import xivvic.roost.service.EventService;
import xivvic.roost.service.ServiceLocator;
import xivvic.roost.service.SubscriptionService;
import xivvic.roost.service.UserService;

/**
 * Builds actions for the user's home page
 * 
 * NOTE: (IMPORTANT)
 * Be sure to add this class to the build block in the base classes' static block.
 * 
 * @see ActionBuilderBase.
 * 
 * @author Reid
 *
 */
public class ActionBuilderHome
	extends ActionBuilderBase
{
	private final static Logger LOG = Logger.getLogger(ActionBuilderHome.class.getName());
	
	public ActionBuilderHome()
	{
		super();
	}

	static Action buildHomeViewAction()
	{
		String desc = "Displays information for a User's home page.";
		
		Action action = new ActionBase(HOME_VIEW, desc, true)
		{
			@Override
			protected void internal_invoke(Object param)
			{
				String session_key = ProgramState.getSessionKey();
				
				if (session_key == null)
				{
					String msg = "No user is logged on.  Nothing to display.";
					LOG.warning(msg);
					System.out.println("No user is logged on.  Nothing to display.");
					return;
				}
				
				User     user = ProgramState.getUser(session_key);
				if (user == null)
				{
					String msg = "Failed to acquire user object from program state. Abort.";
					LOG.warning(msg);
					return;
				}

				Person person = user.person();
				String    msg = String.format("Session Key: [%s]", session_key);
				System.out.println(msg);

				ActionBuilderHome.displayUsers();
				ActionBuilderHome.displayGroupForUser(user);
				ActionBuilderHome.displayEventsForPerson(person);
				ActionBuilderHome.displaySubscriptionsForUser(user);
			}
		};
		
		return action;
	}

	protected static void displayEventsForPerson(Person person)
	{
		EventService         u_svc = (EventService) ServiceLocator.locator().get(ServiceLocator.SERVICE_USER);
		List<Event>          users = u_svc.list();
		Consumer<Event>       cons = (u) -> System.out.println(u);

		users.forEach(cons);
	}

	protected static void displaySubscriptionsForUser(User user)
	{
		SubscriptionService     svc = (SubscriptionService) ServiceLocator.locator().get(ServiceLocator.SERVICE_SUBSCRIPTION);
		List<Subscription>     subs = svc.list();
		Consumer<Subscription> cons = (s) -> System.out.println(s);

		subs.forEach(cons);
	}

	static void displayUsers()
	{
		UserService         u_svc = (UserService) ServiceLocator.locator().get(ServiceLocator.SERVICE_USER);
		List<User>          users = u_svc.list();
		Consumer<User>       cons = (u) -> System.out.println(u);

		users.forEach(cons);
	}

	static void displayGroupForUser(User user)
	{
		Group group = user.group();
		
		System.out.println("User belongs to this group:");
		System.out.println(group);
		System.out.println();
	}
	
}
