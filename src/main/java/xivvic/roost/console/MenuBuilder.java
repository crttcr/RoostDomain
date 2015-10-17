
package xivvic.roost.console;


import console.menu.Menu;
import console.menu.MenuManager;
import xivvic.console.action.ActionBase;
import xivvic.roost.console.action.ActionBuilder;
import xivvic.roost.console.action.ActionRegistry;


/** 
 * Builds the menu for the application.
 * 
 */

public class MenuBuilder
{
	private MenuBuilder()
	{
		
	}
	
	/**
	 * Adds a help capability to the application.
	 */
	public static void addHelp(final MenuManager manager)
	{
		final String name = "help";
		final String desc = "Provides textual help to the application user.";
		
		ActionBase help = new ActionBase(name, desc, true)
		{
			@Override
			public void internal_invoke(Object param)
			{
				System.out.println("Help called with " + param);

				System.out.println("System commands:");
				String[] commands = manager.commands("");
				String    display = String.join(", ", commands);
				System.out.println(display);

				System.out.println("Current Menu Commands:");
				String prefix = manager.activeMenuPrefix();
				commands = manager.commands(prefix);
				display  = String.join(", ", commands);
				System.out.println(display);
			}
			
		};
		
		manager.addHiddenAction(help.name(), help);
	}
	
	private static Menu buildTopLevelMenu(final MenuManager manager)
	{
		Menu menu = new Menu("Welcome",       "w",  manager);

		menu.addItem("Login",    "l",    ActionRegistry.get(ActionBuilder.LOGIN));
		menu.addItem("Logout",   "lout", ActionRegistry.get(ActionBuilder.LOGOUT));
		menu.addItem("Register", "r",    ActionRegistry.get(ActionBuilder.USER_REGISTER));
		
		menu.addItem(buildHomeMenu(manager));
		menu.addItem(buildAdminMenu(manager));
	
		return menu;
	}
	private static Menu buildAdminMenu(final MenuManager manager)
	{
		Menu menu = new Menu("Admin", "a",  manager);

		menu.addItem(buildAddressMenu(manager));
		menu.addItem(buildEventMenu(manager));
		menu.addItem(buildGroupsMenu(manager));
		menu.addItem(buildPeopleMenu(manager));
		menu.addItem(buildSubscriptionsMenu(manager));
		menu.addItem(buildUserMenu(manager));
		menu.addItem(buildCommandMenu(manager));
		menu.addItem(buildDatabaseManagementMenu(manager));
	
		return menu;
	}

	private static Menu buildAddressMenu(final MenuManager manager)
	{
		Menu menu = new Menu("Address", "addr",  manager);

		menu.addItem("List Addresses",                     "l",    ActionRegistry.get(ActionBuilder.ADDRESS_LIST));
		menu.addItem("Add an Address",                     "a",    ActionRegistry.get(ActionBuilder.ADDRESS_ADD));
		menu.addItem("Delete an Address",                  "d",    ActionRegistry.get(ActionBuilder.ADDRESS_DELETE));
		menu.addItem("Associate Address with Person",  "assoc",    ActionRegistry.get(ActionBuilder.ADDRESS_ASSOCIATE));
	
		return menu;
	}

	private static Menu buildCommandMenu(final MenuManager manager)
	{
		Menu menu = new Menu("Command", "cmd",  manager);
	
		menu.addItem("List commands",       "l",  ActionRegistry.get(ActionBuilder.COMMAND_LIST));
		menu.addItem("Status",           "stat",  ActionRegistry.get(ActionBuilder.COMMAND_STATUS));
		return menu;
	}

	private static Menu buildDatabaseManagementMenu(final MenuManager manager)
	{
		Menu menu = new Menu("DB Management", "d",  manager);
	
		menu.addItem("MATCH (n) RETURN n", "list", ActionRegistry.get(ActionBuilder.ADMIN_NEO_LIST_NODES));
		menu.addItem("MATCH [r] RETURN r", "rels", ActionRegistry.get(ActionBuilder.ADMIN_NEO_LIST_RELS));
		menu.addItem("Drop Neo database",  "drop", ActionRegistry.get(ActionBuilder.ADMIN_NEO_DROP));
		menu.addItem("Delete Node by ID",    "dn", ActionRegistry.get(ActionBuilder.ADMIN_NEO_DELETE_NODE));
	
		return menu;
	}

	private static Menu buildEventMenu(final MenuManager manager)
	{
		Menu menu = new Menu("Events", "ev",  manager);
	
		menu.addItem("List events",             "l",    ActionRegistry.get(ActionBuilder.EVENT_LIST));
		menu.addItem("List events for person",  "lep",  ActionRegistry.get(ActionBuilder.EVENT_LIST_FOR_PERSON));
		menu.addItem("Add an event",            "a",    ActionRegistry.get(ActionBuilder.EVENT_ADD));
		menu.addItem("Delete event",            "d",    ActionRegistry.get(ActionBuilder.EVENT_DELETE));
	
		return menu;
	}

	private static Menu buildGroupsMenu(final MenuManager manager)
	{
		Menu menu = new Menu("Groups",        "g",  manager);
	
		menu.addItem("List groups",                "l",    ActionRegistry.get(ActionBuilder.GROUP_LIST));
		menu.addItem("List group members",         "lgm",  ActionRegistry.get(ActionBuilder.GROUP_LIST_MEMBERS));
		menu.addItem("Add a new group",            "a",    ActionRegistry.get(ActionBuilder.GROUP_ADD));
		menu.addItem("Add member to group",        "addm", ActionRegistry.get(ActionBuilder.GROUP_MEMBER_ADD));
		menu.addItem("Remove member from group",   "remm", ActionRegistry.get(ActionBuilder.GROUP_MEMBER_DELETE));
		menu.addItem("Delete group",               "d",    ActionRegistry.get(ActionBuilder.GROUP_DELETE));
	
		return menu;
	}

	private static Menu buildHomeMenu(final MenuManager manager)
	{
		Menu menu = new Menu("Home", "h",  manager);
	
		menu.addItem("Home Page View",        "h",  ActionRegistry.get(ActionBuilder.HOME_VIEW));
		menu.addItem(buildProfileMenu(manager));
		menu.addItem(buildSettingsMenu(manager));
		menu.addItem(buildSubscriptionsMenu(manager));
		menu.addItem(buildGroupsMenu(manager));
	
		return menu;
	}

	private static Menu buildPeopleMenu(final MenuManager manager)
	{
		Menu menu = new Menu("People",        "p",  manager);
	
		menu.addItem("List people",        "l",  ActionRegistry.get(ActionBuilder.PERSON_LIST));
		menu.addItem("Add a new person",   "a",  ActionRegistry.get(ActionBuilder.PERSON_ADD));
		menu.addItem("Delete a person",    "d",  ActionRegistry.get(ActionBuilder.PERSON_DELETE));
	
		return menu;
	}

	private static Menu buildProfileMenu(final MenuManager manager)
	{
		Menu menu = new Menu("Profile",       "p",  manager);

		return menu;
	}

	private static Menu buildSettingsMenu(final MenuManager manager)
	{
		Menu menu = new Menu("Settings",      "se", manager);
	
		return menu;
	}

	private static Menu buildSubscriptionsMenu(final MenuManager manager)
	{

		Menu  menu = new Menu("Subscriptions", "sub", manager);
		
		menu.addItem("List subscriptions",       "l",    ActionRegistry.get(ActionBuilder.SUBSCRIPTION_LIST));
		menu.addItem("Subscriptions for event",  "l4e",  ActionRegistry.get(ActionBuilder.SUBSCRIPTION_LIST_4_EVENT));
		menu.addItem("Subscriptions for user",   "l4u",  ActionRegistry.get(ActionBuilder.SUBSCRIPTION_LIST_4_USER));
		menu.addItem("Add a new subscription",   "a",    ActionRegistry.get(ActionBuilder.SUBSCRIPTION_ADD));
		menu.addItem("Delete subscription",      "d",    ActionRegistry.get(ActionBuilder.SUBSCRIPTION_DELETE));

		return menu;
	}

	private static Menu buildUserMenu(final MenuManager manager)
	{
		Menu menu = new Menu("User", "u",  manager);
	
		menu.addItem("List users",       "l",  ActionRegistry.get(ActionBuilder.USER_LIST));
		menu.addItem("Register user",    "r",    ActionRegistry.get(ActionBuilder.USER_REGISTER));
		menu.addItem("Delete user",      "d",  ActionRegistry.get(ActionBuilder.USER_DELETE));
	
		return menu;
	}

	public static void buildMenuSystem(final MenuManager manager)
	{
		// Create the application's menu structure
		//
		manager.reset();
		
		Menu top = buildTopLevelMenu(manager);
		
		manager.addStartMenu(top);
		manager.addCoreActions();
	}
}

