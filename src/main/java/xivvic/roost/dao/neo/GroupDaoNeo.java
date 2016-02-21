package xivvic.roost.dao.neo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xivvic.neotest.program.RoostNodeType;
import xivvic.neotest.program.RoostRelType;
import xivvic.roost.dao.GroupDao;
import xivvic.roost.domain.Group;
import xivvic.roost.domain.Person;

public class GroupDaoNeo
	extends DaoNeo
	implements GroupDao
{
	private final static Logger LOG = LoggerFactory.getLogger(GroupDaoNeo.class.getName()); 
	public static final RoostRelType MEMBER_OF_GROUP = RoostRelType.GROUP_MEMBER;
	
	@Inject
	public GroupDaoNeo(GraphDatabaseService gdb)
	{
		super(gdb);
	}
	
	/**
	 * Returns null if not found.  Otherwise returns a group object
	 * 
	 */
	@Override
	public Group findByName(String name)
	{
		if (name == null)
		{
			String msg = String.format("Name parameter is null");
			LOG.warn(msg);
			return null;
		}
		
		if (name.length() == 0)
			return null;
		
		Label             label = RoostNodeType.GROUP;
		GraphDatabaseService db = db();

		try (Transaction tx = db.beginTx() )
		{
			ResourceIterator<Node> rit = db.findNodes(label);
			
			Group group = null;
			while (rit.hasNext())
			{
				Node    node = rit.next();
				String value = (String) node.getProperty(Group.PROP_NAME);
				if (name.equalsIgnoreCase(value))
				{
					group = node2Group(node);
					tx.success();
					break;
				}
			}
			
			rit.close();
			return group;
		}
	}

	@Override
	public List<Group> list()
	{
		Label              label = RoostNodeType.GROUP;
		GraphDatabaseService  db = db();
		List<Group>        groups = new ArrayList<>();

		try (Transaction tx = db.beginTx() )
		{
			ResourceIterator<Node> rit = db.findNodes(label);
			
			while (rit.hasNext())
			{
				Node    node = rit.next();
				Group  group = node2Group(node);
				
				groups.add(group);
			}

			rit.close();
			tx.success();
		}

		return groups;
	}

	Group node2Group(Node node)
	{
		String id   = (String) node.getProperty(Group.PROP_ID);
		String name = (String) node.getProperty(Group.PROP_NAME);
		
		Group.Builder builder = Group.builder()
				.id(id)
				.name(name);

		Group group = builder.build();
		
		return group;
	}


	@Override
	public Group findById(String group_id)
	{
		Label                label = RoostNodeType.GROUP;
		String                 key = Group.PROP_ID;
		String               value = group_id;
		Node                  node = null;
		GraphDatabaseService    db = db();

		try (Transaction tx = db.beginTx() )
		{
			node = db.findNode(label, key, value);
			
			if (node == null)
			{
				tx.success();
				return null;
			}
			
			Group group = node2Group(node);
			tx.success();
			return group;
		}
	}

	@Override
	public List<Person> findMembers(String group_id)
	{
		if (group_id == null)
			return Person.EMPTY_LIST;
		Label                label = RoostNodeType.GROUP;
		String                 key = Group.PROP_ID;
		String               value = group_id;
		GraphDatabaseService    db = db();

		try (Transaction tx = db.beginTx() )
		{
			Node group = db.findNode(label, key, value);
			if (group == null)
			{
				tx.success();
				return Person.EMPTY_LIST;
			}
			
			PersonDaoNeo p_dao = new PersonDaoNeo(db());
			List<Person>  list = new ArrayList<>();
			
			Iterator<Relationship> it = group.getRelationships(RoostRelType.GROUP_MEMBER).iterator();
			while (it.hasNext())
			{
				Relationship r = it.next();
				Node    p_node = r.getOtherNode(group);
				Person       p = p_dao.node2Person(p_node);
				list.add(p);
			}
			tx.success();
			return list;
		}
	}

}
