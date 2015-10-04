package xivvic.roost.neo.task;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

import org.neo4j.graphdb.ConstraintViolationException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;

import xivvic.command.Command;
import xivvic.command.CommandHandler;
import xivvic.roost.neo.EdgeSchema;
import xivvic.roost.neo.NodeSchema;
import xivvic.roost.neo.PropMeta;

public abstract class NeoCommandHandler
implements CommandHandler
{
	protected final static Logger       LOG = Logger.getLogger(NeoCommandHandler.class.getName());
	protected final GraphDatabaseService db;
	protected final Command             cmd;

	protected NeoCommandHandler(GraphDatabaseService db, Command command)
	{
		this.db  = db;
		this.cmd = command;
	}
	
	protected int SetPropertiesOnNode(Node node, NodeSchema schema, Map<String, Object> values)
	{
		int            count = 0;
		boolean        error = false;
		List<PropMeta> props = schema.properties(null);
	
		for (PropMeta p_meta : props)
		{
			String     key = p_meta.key();
			Object   value = values.get(key);
			String  result = setNeoProperty(p_meta, value, node);
			
			if (result == null)
			{
				count++;
			}
			else
			{
				error = true;
				LOG.info(result);
			}
		}

		if (error)
			return -1;

		return count;
	}
	
	protected int SetPropertiesOnLink(Relationship link, EdgeSchema schema, Map<String, Object> values)
	{
		int            count = 0;
		boolean        error = false;
		List<PropMeta> props = schema.properties(null);
	
		for (PropMeta p_meta : props)
		{
			String     key = p_meta.key();
			Object   value = values.get(key);
			String result = setNeoProperty(p_meta, value, link);
			
			if (result == null)
			{
				count++;
			}
			else
			{
				error = true;
				LOG.info(result);
			}
		}

		if (error)
			return -1;
		
		return count;
	}

	
	/**
	 * This method sets a property on either a Node or an Edge.  If there is a failure, an explanation
	 * is provided as the return value of the function. This function returns null upon success.
	 * 
	 * @param schema information about the property to set
	 * @param value the value to be associated with the property
	 * @param target the Node or Relationship that is to receive the new property
	 * 
	 * @return null if successful, otherwise a string explaining the failure.
	 * 
	 */
	private String setNeoProperty(PropMeta schema, Object value, PropertyContainer target)
	{
		if (schema == null)
		{
			String msg = String.format("Property information was null.  This is a program error. Target: [%s]", target);
			return msg;
		}
		
		if (target == null)
		{
			String msg = String.format("Cannot set a property on a null target: Property: [%s].", schema);
			return msg;
		}
		
		if (value == null)
		{
			String msg = String.format("Cannot set a property on a null target: Property: [%s].", schema);
			return msg;
		}
		

		String     key = schema.key();
		
		Function<Object, Object> converter = schema.object2NeoConverter();
		if (converter != null)
		{
			value = converter.apply(value);
		}

		
		try
		{
         target.setProperty(key, value);
		}
		catch (ConstraintViolationException e)
		{
			String           fmt = "FAIL: Set property on (:%s) -- property=[%s], value=[%s]. Exception: [%s]";
			String           msg = String.format(fmt, target, key, value, e.getLocalizedMessage());
			LOG.warning(msg);
			return msg;
		}
		
		return null;
	}

}
