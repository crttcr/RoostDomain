package xivvic.roost.neo.task;

import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xivvic.command.Command;
import xivvic.command.CommandHandler;
import xivvic.command.CommandHandlerFactory;

public class NeoCommandHandlerFactory
		implements CommandHandlerFactory
{
	private final static Logger LOG = LoggerFactory.getLogger(NeoCommandHandlerFactory.class.getName());
	
	// Factory Instance Variables
	//
	private final GraphDatabaseService db;	
	private final Map<String, Handlers> intent2handler;
	
	public NeoCommandHandlerFactory(GraphDatabaseService db, Map<String, Handlers> handler_map)
	{
		this.db = db;
		this.intent2handler = handler_map; 
	}

	@Override
	public CommandHandler handler(Command command)
	{
		String intent = command.intent();
		
		if (intent == null)
			return null;
		
		CommandHandler ch = null;
		Handlers        h = intent2handler.get(intent);
		
		if (h == null)
		{
			String msg = String.format("Failed to find a handler for intent [%s]", intent);
			LOG.error(msg);
			return null;
		}
		
		switch(h)
		{
		case NODE_CREATE:
			ch = new NodeCreate(db, command);
			break;
		case NODE_CREATE_AND_LINK:
			ch = new NodeCreateAndLink(db, command);
			break;
		case NODE_CREATE_AND_MULTILINK:
			ch = new NodeCreateAndMultiLink(db, command);
			break;
		case NODE_DELETE:
			ch = new NodeDelete(db, command);
			break;
		case NODE_MODIFY:
			ch = new NodeModify(db, command);
			break;
		case LINK_CREATE:
			ch = new Link(db, command);
			break;
		case LINK_DELETE:
			ch = new Unlink(db, command);
			break;
		case LINK_MODIFY:
			ch = new LinkModify(db, command);
			break;
		case REGISTER_USER:
			ch = new RegisterUser(db, command);
			break;
		default:
			String msg = String.format("Failed to find a case stantement for value: %s", h);
			LOG.warn(msg);
		};
		
		return ch;

	}



}
