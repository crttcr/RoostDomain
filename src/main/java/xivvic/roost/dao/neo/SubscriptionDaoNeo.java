package xivvic.roost.dao.neo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

import xivvic.neotest.program.RoostNodeType;
import xivvic.neotest.program.RoostRelType;
import xivvic.roost.dao.SubscriptionDao;
import xivvic.roost.domain.Event;
import xivvic.roost.domain.Subscription;
import xivvic.roost.domain.SubscriptionExpiry;
import xivvic.roost.domain.User;

public class SubscriptionDaoNeo
	extends DaoNeo
	implements SubscriptionDao
{
	private final static Logger LOG = Logger.getLogger(SubscriptionDaoNeo.class.getName()); 
	
	public SubscriptionDaoNeo(GraphDatabaseService gdb)
	{
		super(gdb);
	}
	
	/**
	 * Returns empty list if not found.  Otherwise returns a list of subscriptions
	 * 
	 */

	@Override
	public List<Subscription> findByUserId(String user_id)
	{
		if (user_id == null)
			return Subscription.EMPTY_LIST;
		
		GraphDatabaseService db = db();

		try (Transaction tx = db.beginTx() )
		{
			Node user_node = db.findNode(RoostNodeType.USER, User.PROP_ID, user_id);
			if (user_node == null)
			{
				String msg = String.format("Unable to locate person with id = [%s]", user_id);
				LOG.warning(msg);
				tx.success();
				return Subscription.EMPTY_LIST;
			}
			
			Iterable<Relationship>     links = user_node.getRelationships(RoostRelType.USER_SUBSCRIBE);
			final List<Subscription>  result = new ArrayList<>();
			Consumer<Relationship>   creator = (r) -> 
			{
				Node      sub_node = r.getOtherNode(user_node);
				Subscription   sub = node2Subscription(sub_node);
				result.add(sub);
			};

			links.forEach(creator);
			tx.success();
			
			return result;
		}
	}


	@Override
	public List<Subscription> list()
	{
		Label                     label = RoostNodeType.SUBSCRIPTION;
		GraphDatabaseService         db = db();
		final List<Subscription> result = new ArrayList<>();

		try (Transaction tx = db.beginTx() )
		{
			ResourceIterator<Node> nodes = db.findNodes(label);

			Consumer<Node> action = (n) -> 
			{
				Subscription sub = node2Subscription(n);
				result.add(sub);
			};
			
			nodes.forEachRemaining(action);
			nodes.close();
			tx.success();
		}

		return result;
	}


	Subscription node2Subscription(Node node)
	{
		String id     = (String) node.getProperty(Subscription.PROP_ID);
		String expiry = (String) node.getProperty(Subscription.PROP_EXPIRY);
		
		SubscriptionExpiry sub_exp = SubscriptionExpiry.valueOf(expiry);
		
		if (sub_exp == null)
			return null;
		
		Subscription sub = Subscription.builder().id(id).expiry(sub_exp).build();

		return sub;
	}



	@Override
	public Subscription findById(String subs_id)
	{
		Label                label = RoostNodeType.SUBSCRIPTION;
		String                 key = Subscription.PROP_ID;
		String               value = subs_id;
		Node                  node = null;
		GraphDatabaseService    db = db();

		try (Transaction tx = db.beginTx() )
		{
			node = db.findNode(label, key, value);
			tx.success();
			
			if (node == null)
			{
				tx.success();
				return null;
			}
			
			Subscription sub = node2Subscription(node);
			return sub;
		}
	}

	@Override
	public List<Subscription> findByEventId(String event_id)
	{
		if (event_id == null)
			return Subscription.EMPTY_LIST;
		
		GraphDatabaseService db = db();

		try (Transaction tx = db.beginTx() )
		{
			Node event_node = db.findNode(RoostNodeType.EVENT, Event.PROP_ID, event_id);
			if (event_node == null)
			{
				String msg = String.format("Unable to locate event with id = [%s]", event_id);
				LOG.warning(msg);
				tx.success();
				return Subscription.EMPTY_LIST;
			}
			
			Iterable<Relationship>     links = event_node.getRelationships(RoostRelType.SUBSCRIBED_EVENT);
			final List<Subscription>  result = new ArrayList<>();
			Consumer<Relationship>   creator = (r) -> 
			{
				Node      sub_node = r.getOtherNode(event_node);
				Subscription   sub = node2Subscription(sub_node);
				result.add(sub);
			};

			links.forEach(creator);
			tx.success();
			
			return result;
		}
	}

}
