package xivvic.roost.console.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import xivvic.console.action.Action;
import xivvic.console.action.ActionBase;
import xivvic.console.action.ActionMetadata;
import xivvic.console.input.InputProcessor;
import xivvic.console.input.InputProcessorSingleInteger;
import xivvic.roost.console.ProgramState;
import xivvic.util.text.StringUtil;

/**
 * Builds configuration actions for this application.
 * 
 * NOTE: (IMPORTANT)
 * Be sure to add this class to the build block in the base classes' static block.
 * @see ActionBuilderBase.
 * 
 * @author Reid
 *
 */
public class ActionBuilderConfiguration
	extends ActionBuilderBase
{
	private final static Logger LOG = Logger.getLogger(ActionBuilderConfiguration.class.getName());
	
	public ActionBuilderConfiguration()
	{
		super();
	}

	static Action buildTraceAction()
	{
		String desc = "Toggles trace output";
		
		Action action = new ActionBase(ADMIN_TRACE, desc, false)
		{
			@Override
			protected void internal_invoke(Object param)
			{
				boolean b = ProgramState.isTraceEnabled();
				
				if (b)
				{
					String msg = "TRACE disabled";
					LOG.fine(msg);
				}
				else
				{
					String msg = "TRACE disabled";
					LOG.fine(msg);
				}
				
				ProgramState.setTraceEnabled(! b);
			}
		};
		
		return action;
	}

	static Action buildNeoDropAction()
	{
		String         desc = "Purges all Neo data";
		String       cypher = "MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n, r";
		String action_token = ActionBuilder.ADMIN_NEO_DROP;
		Action       action = ActionBuilderBase.buildNeoExecCypherAction(action_token, cypher, desc);
	
		return action;
	}

/*
	MATCH (p:Person) where ID(p)=1
	OPTIONAL MATCH (p)-[r]-() //drops p's relations
	DELETE r,p
*/
	static Action buildNeoDeleteNodeByIdAction()
	{
		String         name = ActionBuilder.ADMIN_NEO_DELETE_NODE;
		String         desc = "Deletes a node with a specified id";
		String        usage = "dn 12";
		String       target = StringUtil.convertToTemplateTarget(InputProcessorSingleInteger.KEY);
		String       cypher = String.format("MATCH (n) WHERE ID(n) = %s OPTIONAL MATCH (n)-[r]-() DELETE r, n", target);
		ActionMetadata meta = new ActionMetadata(name, desc, usage, null);
		InputProcessor   ip = new InputProcessorSingleInteger(true);
		Action       action = ActionBuilderBase.buildNeoExecDynamicCypherAction(meta, cypher, desc, ip);
	
		return action;
	}

	static Action buildNeoListNodeAction()
	{
		String               name = ActionBuilder.ADMIN_NEO_LIST_NODES;
		String              usage = "list";
		String               desc = "Lists All Neo Nodes";
		ActionMetadata       meta = new ActionMetadata(name, desc, usage);
		String               cypher = "MATCH (n) RETURN n";
		
		Consumer<Map<String, Object>> function = m -> dump((Node) m.get("n"));
		
		Action action = ActionBuilderBase.buildNeoExecCypherItemQuery(meta, cypher, function);
		return action;
	}
	
	static Action buildNeoListLinkAction()
	{
		String               name = ActionBuilder.ADMIN_NEO_LIST_RELS;
		String              usage = "list";
		String               desc = "Lists All Neo Relationships";
		ActionMetadata       meta = new ActionMetadata(name, desc, usage);
		String               cypher = "MATCH (n)-[r]-() RETURN r";
		
		Consumer<Map<String, Object>> function = m -> dump((Relationship) m.get("r"));
		
		Action action = ActionBuilderBase.buildNeoExecCypherItemQuery(meta, cypher, function);
		return action;
	}
	
	static void dump(Node n)
	{
		if (n == null)
		{
			System.out.println("null");
			return;
		}
		
		System.out.println("Node: " + n);
		Iterable<String> keys = n.getPropertyKeys();
		
		Consumer<String> print = s ->
		{
			Object prop = n.getProperty(s);
			String  fmt = "%15s -> %s";
			String line = String.format(fmt, s, prop);
			System.out.println(line);
		};
		
		keys.forEach(print);
		System.out.println("");
	}

	static void dump(Relationship r)
	{
		if (r == null)
		{
			System.out.println("null");
			return;
		}
		
		System.out.println("R: " + r);
		Node  start = r.getStartNode();
		Node    end = r.getEndNode();
		String slab = stringifyLabels(start);
		String elab = stringifyLabels(end);
		
		String     fmt = "(%d:%s)--[%s]->(%d:%s)";
		String picture = String.format(fmt, start.getId(), slab, r.getType().name(), end.getId(), elab);
		System.out.println(picture);
		
		Iterable<String> keys = r.getPropertyKeys();
		
		keys.forEach(s -> System.out.println("\t" + s + " -> " + r.getProperty(s)));
		System.out.println("");
	}
	
	static String stringifyLabels(Node node)
	{
		if (node == null)
			return "null";
		
		Iterable<Label> iterable = node.getLabels();
		Iterator<Label> iterator = iterable.iterator();
		
		List<String> list = new ArrayList<>();
		
		while (iterator.hasNext())
		{
			Label l = iterator.next();
			list.add(l.name());
		}
		
		return String.join(", ", list);
	}

}
