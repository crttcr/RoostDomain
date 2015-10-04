package xivvic.roost.neo.task;


public enum Handlers
{
	NODE_CREATE,
	NODE_CREATE_AND_LINK,
	NODE_CREATE_AND_MULTILINK,
	NODE_DELETE,
	NODE_MODIFY,
	LINK_CREATE,
	LINK_DELETE,
	LINK_MODIFY,

	REGISTER_USER,   // Special handler to perform do all the work for registering users.
	;
}
