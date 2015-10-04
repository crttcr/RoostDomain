package xivvic.roost.dao.neo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

import xivvic.neotest.program.RoostNodeType;
import xivvic.neotest.program.RoostRelType;
import xivvic.roost.dao.UserDao;
import xivvic.roost.domain.Group;
import xivvic.roost.domain.Person;
import xivvic.roost.domain.User;

public class UserDaoNeo
	extends DaoNeo
	implements UserDao
{
	private final static Logger LOG = Logger.getLogger(UserDaoNeo.class.getName());
	
	public UserDaoNeo(GraphDatabaseService gdb)
	{
		super(gdb);
	}
	
	@Override
	public List<User> list()
	{
		Label             label = RoostNodeType.USER;
		GraphDatabaseService db = db();
		List<User>        users = new ArrayList<>();

		try (Transaction tx = db.beginTx() )
		{
			ResourceIterator<Node> rit = db.findNodes(label);
			
			while (rit.hasNext())
			{
				Node    node = rit.next();
				User    user = node2User(node);
				
				if (user != null)
					users.add(user);
			}

			rit.close();
			tx.success();
		}

		return users;
	}


	// NOTE!
	// This must be called in a transactional context
	//
	private User node2User(Node node)
	{
		String    id = (String) node.getProperty(User.PROP_ID);
		String email = (String) node.getProperty(User.PROP_EMAIL);
		String uname = (String) node.getProperty(User.PROP_UNAME);
		String phash = (String) node.getProperty(User.PROP_PHASH);
		
		RoostRelType r_type = RoostRelType.USER_GROUP;
		Direction       dir = Direction.OUTGOING;
		Relationship  g_rel = node.getSingleRelationship(r_type, dir);
		if (g_rel == null)
		{
			return null;
		}
		
		r_type = RoostRelType.USER_PERSON;
		Relationship  p_rel = node.getSingleRelationship(r_type, dir);
		if (p_rel == null)
		{
			return null;
		}

		GroupDaoNeo        g_dao = new GroupDaoNeo(db());
		Node          group_node = g_rel.getOtherNode(node);
		Group              group = g_dao.node2Group( group_node);

		PersonDaoNeo       p_dao = new PersonDaoNeo(db());
		Node         person_node = p_rel.getOtherNode(node);
		Person            person = p_dao.node2Person(person_node);
		
		User user = User.builder()
				.id(id)
				.email(email)
				.passhash(phash)
				.username(uname)
				.group(group)
				.person(person)
				.build();
		
		return user;
	}

	
	/**
	 * Returns null if not found.  Otherwise returns a user object
	 * 
	 */
	@Override
	public User findByEmail(String email)
	{
		if (email == null) return null;
	
		String    property = User.PROP_EMAIL;
		Label        label = RoostNodeType.USER;
		User          user = findByUniqueProperty(label, property, email);
		
		return user;
	}

	@Override
	public User findByUserName(String u_name)
	{
		if (u_name == null) return null;

		String    property = User.PROP_UNAME;
		Label        label = RoostNodeType.USER;
		User          user = findByUniqueProperty(label, property, u_name);
		
		return user;
	}
	
	public User findByUniqueProperty(Label label, String property, String value)
	{
		if (label    == null) return null;
		if (property == null) return null;
		if (value    == null) return null;
		
		GraphDatabaseService db = db();

		try (Transaction tx = db.beginTx() )
		{
			Node node = db.findNode(label, property, value);
			if (node == null)
			{
				tx.success();
				return null;
			}

			User user = node2User(node);
			if (user == null)
			{
				String msg = "Found user node, but failed to hydrate";
				LOG.warning(msg);
			}
			
			tx.success();
			return user;
		}
	}

}
