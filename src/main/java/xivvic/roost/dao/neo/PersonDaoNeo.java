package xivvic.roost.dao.neo;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

import xivvic.neotest.program.RoostNodeType;
import xivvic.roost.dao.PersonDao;
import xivvic.roost.domain.Person;

public class PersonDaoNeo
	extends DaoNeo
	implements PersonDao
{

	public PersonDaoNeo(GraphDatabaseService gdb)
	{
		super(gdb);
	}
	
	@Override
	public List<Person> list()
	{
		Label                label = RoostNodeType.PERSON;
		GraphDatabaseService    db = db();
		List<Person>        people = new ArrayList<>();

		try (Transaction tx = db.beginTx() )
		{
			ResourceIterator<Node> rit = db.findNodes(label);
			
			while (rit.hasNext())
			{
				Node    node = rit.next();
				Person  person = node2Person(node);
				
				people.add(person);
			}

			rit.close();
			tx.success();
		}

		return people;
	}
	

	// NOTE!
	// This must be called in a transactional context
	//
	Person node2Person(Node node)
	{
		String    id = (String) node.getProperty(Person.PROP_ID);
		String fname = (String) node.getProperty(Person.PROP_FIRST_NAME);
		String lname = (String) node.getProperty(Person.PROP_LAST_NAME);
		String mname = (String) node.getProperty(Person.PROP_MIDDLE_NAME, null);
		String nname = (String) node.getProperty(Person.PROP_NICKNAME, null);
		
		Person person = Person.builder()
				.id(id)
				.firstName(fname)
				.middleName(mname)
				.lastName(lname)
				.nickname(nname)
				.build();
		
		return person;
	}


	/**
	 * Returns null if not found.  Otherwise returns a person object
	 * 
	 */
	@Override
	public Person findById(String person_id)
	{
		if (person_id == null)
			return null;
		
		if (person_id.length() == 0)
			return null;
		
		Label                label = RoostNodeType.PERSON;
		String                 key = Person.PROP_ID;
		String               value = person_id;
		GraphDatabaseService    db = db();
		Node                  node = null;
		Person              person = null;

		try (Transaction tx = db.beginTx() )
		{
			node   = db.findNode(label, key, value);
			if (node == null)
			{
				tx.success();
				return null;
			}
			person = node2Person(node);
			tx.success();
		}
			
		return person;
	}


}
