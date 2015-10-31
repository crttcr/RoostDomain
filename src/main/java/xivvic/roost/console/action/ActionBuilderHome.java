package xivvic.roost.console.action;

import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xivvic.console.action.Action;
import xivvic.console.action.ActionBase;
import xivvic.roost.console.DaggerProgramComponents;
import xivvic.roost.console.ProgramComponents;
import xivvic.roost.console.ProgramState;
import xivvic.roost.domain.Event;
import xivvic.roost.domain.Group;
import xivvic.roost.domain.Person;
import xivvic.roost.domain.Subscription;
import xivvic.roost.domain.User;
import xivvic.roost.service.EventService;
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
	private final static Logger LOG = LoggerFactory.getLogger(ActionBuilderHome.class.getName());
	
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
					LOG.warn(msg);
					System.out.println("No user is logged on.  Nothing to display.");
					return;
				}
				
				User     user = ProgramState.getUser(session_key);
				if (user == null)
				{
					String msg = "Failed to acquire user object from program state. Abort.";
					LOG.warn(msg);
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
		ProgramComponents  components = DaggerProgramComponents.create();
		EventService          service = components.eventService();
		List<Event>             users = service.eventsForPerson(person.id());
		Consumer<Event>          cons = (u) -> System.out.println(u);

		users.forEach(cons);
	}

	protected static void displaySubscriptionsForUser(User user)
	{
		ProgramComponents    components = DaggerProgramComponents.create();
		SubscriptionService     service = components.subscriptionService();
		List<Subscription>     subs = service.subscriptionsForUser(user.id());
		Consumer<Subscription> cons = (s) -> System.out.println(s);

		subs.forEach(cons);
	}

	static void displayUsers()
	{
		ProgramComponents    components = DaggerProgramComponents.create();
		UserService             service = components.userService();
		List<User>                users = service.list();
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
