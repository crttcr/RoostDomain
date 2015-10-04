package xivvic.roost.console;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xivvic.console.action.Action;
import xivvic.roost.neo.task.Handlers;

public interface ActionBuilder
{
	// The ActionBuilder needs a build method so that it can build all of its actions.
	//
	List<Action> build();
	
	
	// This is a definitive list of the actions available within the program.
	// Each should has an Action object that implements its logic.
	//
	//
	public static final String LOGIN                  = "login";
	public static final String LOGOUT                 = "logout";
	public static final String ADDRESS_LIST           = "address.list";
	public static final String ADDRESS_ADD            = "address.add";
	public static final String ADDRESS_DELETE         = "address.delete";
	public static final String ADDRESS_ASSOCIATE      = "address.associate";
	public static final String ADMIN_TRACE            = "admin.trace.toggle";
	public static final String ADMIN_NEO_DROP         = "admin.neo.drop";
	public static final String ADMIN_NEO_DELETE_NODE  = "admin.neo.delete.node";
	public static final String ADMIN_NEO_LIST_NODES   = "admin.neo.node.list";
	public static final String ADMIN_NEO_LIST_RELS    = "admin.neo.rel.list";
	public static final String COMMAND_LIST           = "cmd.list";
	public static final String COMMAND_STATUS         = "cmd.status";
	public static final String EVENT_LIST             = "event.list";
	public static final String EVENT_LIST_FOR_PERSON  = "event.list.4.person";
	public static final String EVENT_ADD              = "event.add";
	public static final String EVENT_DELETE           = "event.delete";
	public static final String GROUP_ADD              = "group.add";
	public static final String GROUP_DELETE           = "group.delete";
	public static final String GROUP_LIST             = "group.list";
	public static final String GROUP_LIST_MEMBERS     = "group.list.members";
	public static final String GROUP_MEMBER_ADD       = "group.member.add";
	public static final String GROUP_MEMBER_DELETE    = "group.member.delete";
	public static final String HOME_VIEW              = "home.view";
	public static final String PERSON_LIST            = "person.list";
	public static final String PERSON_ADD             = "person.add";
	public static final String PERSON_DELETE          = "person.delete";
	public static final String SUBSCRIPTION_LIST      = "subs.list";
	public static final String SUBSCRIPTION_LIST_4_EVENT   = "subs.list.4.event";
	public static final String SUBSCRIPTION_LIST_4_USER    = "subs.list.4.user";
	public static final String SUBSCRIPTION_ADD       = "subs.add";
	public static final String SUBSCRIPTION_DELETE    = "subs.delete";
	public static final String USER_LIST              = "user.list";
	public static final String USER_LIST_FOR_GROUP    = "user.list.4.group";
	public static final String USER_REGISTER          = "user.register"; 
	public static final String USER_DELETE            = "user.delete";
	

	
	// This map associates SOME of the actions with a command handler
	// that will modify the application's permanent state.  These actions
	// are the programs commands.  
	//
	// Other actions, such as "person.list" do not modify permanent state and
	// represent queries.  These are separate using the CQRS approach.
	//
	// The reason this map is needed is that the CommandProcessor implementation is a reusable library
	// component that doesn't have knowledge of our domain, so it cannot be home to this mapping.
	// Rather, this map is provided to it as an injectable element.
	//
	// TODO Some class needs to tie these together.  Not sure of the best place for this function.
	// Also, it's uncool having to tie these two lists together manually.  However, unless the
	// Action name becomes an object with a Class method attached to it, I don't see it changing soon.
	//
	static Map<String, Handlers> getIntentMap()
	{
		Map<String, Handlers> h_map = new HashMap<>();
		
		
		h_map.put (ADDRESS_ADD        , Handlers.NODE_CREATE);
		h_map.put (ADDRESS_ASSOCIATE  , Handlers.LINK_CREATE);
		h_map.put (ADDRESS_DELETE     , Handlers.NODE_DELETE);
		h_map.put (EVENT_ADD          , Handlers.NODE_CREATE_AND_LINK);
		h_map.put (EVENT_DELETE       , Handlers.NODE_DELETE);
		h_map.put (GROUP_ADD          , Handlers.NODE_CREATE);
		h_map.put (GROUP_DELETE       , Handlers.NODE_DELETE);
		h_map.put (GROUP_MEMBER_ADD   , Handlers.LINK_CREATE);
		h_map.put (GROUP_MEMBER_DELETE, Handlers.LINK_DELETE);
		h_map.put (PERSON_ADD         , Handlers.NODE_CREATE);
		h_map.put (PERSON_DELETE      , Handlers.NODE_DELETE);
		h_map.put (SUBSCRIPTION_ADD   , Handlers.NODE_CREATE_AND_MULTILINK);
		h_map.put (SUBSCRIPTION_DELETE, Handlers.NODE_DELETE);
		h_map.put (USER_DELETE        , Handlers.NODE_DELETE);
		h_map.put (USER_REGISTER      , Handlers.REGISTER_USER);

		return h_map;
	}
}
