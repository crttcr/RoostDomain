package xivvic.roost.dao.neo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;

import xivvic.roost.domain.Group;

public class GroupDaoNeoTest
{
	// The DAO under test
	//
	private GroupDaoNeo g_dao;

	// My Objects that need mocking in this set of tests
	//
	GraphDatabaseService db;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
		db    = new TestGraphDatabaseFactory().newImpermanentDatabase();
		
		if (db == null)
			fail("Unable to create temporary database for testing");
		
		g_dao = new GroupDaoNeo(db);
	}

	@After
	public void tearDown() throws Exception
	{
		if (db != null)
		{
			db.shutdown();
		}
		g_dao = null;
	}

	@Test
	public void testFindByNameWhichDoesNotExist()
	{
		// Arrange
		//
		String cypher = 
				"CREATE                                                 " +
				"(a:GROUP  {group_name:'Alpha', group_id:'g1'}),        " +
				"(b:GROUP  {group_name:'Bravo', group_id:'g2'}),        " +
				"(c:GROUP  {group_name:'Cable', group_id:'g3'})         " +
				"";

		try (Transaction tx = db.beginTx())
		{
			db.execute(cypher);
			tx.success();
		}
		
		// Act
		//
		String name = "Donut";
		Group group = g_dao.findByName(name);
		
		// Assert
		//
		assertNull(group);
	}

	@Test
	public void testFindByNameWhichDoesExist()
	{
		// Arrange
		//
		String cypher = 
				"CREATE                                                 " +
				"(a:GROUP  {group_name:'Alpha', group_id:'g1'}),        " +
				"(b:GROUP  {group_name:'Bravo', group_id:'g2'}),        " +
				"(c:GROUP  {group_name:'Cable', group_id:'g3'})         " +
				"";

		try (Transaction tx = db.beginTx())
		{
			db.execute(cypher);
			tx.success();
		}
		
		// Act
		//
		String name = "Bravo";
		Group group = g_dao.findByName(name);
		
		// Assert
		//
		assertNotNull(group);
		assertTrue(name.equals(group.name()));
		assertTrue("g2".equals(group.id()));
	}

	@Test
	public void testListEmpty()
	{
		// Arrange
		//
		String cypher = 
				"CREATE                                                   " +
				"(a:UNICORN  {group_name:'Alpha', group_id:'g1'}),        " +
				"(b:TELEPOD  {group_name:'Bravo', group_id:'g2'}),        " +
				"(c:UNICORN  {group_name:'Cable', group_id:'g3'})         " +
				"";

		try (Transaction tx = db.beginTx())
		{
			db.execute(cypher);
			tx.success();
		}
		
		// Act
		//
		List<Group> groups = g_dao.list();
		
		// Assert
		//
		assertNotNull(groups);
		assertEquals(0, groups.size());
	}

	@Test
	public void testListOneItem()
	{
		// Arrange
		//
		String cypher = 
				"CREATE                                                   " +
				"(a:PANDA  {group_name:'Alpha', group_id:'g1'}),        " +
				"(b:GROUP  {group_name:'Bravo', group_id:'g2'}),        " +
				"(c:PANDA  {group_name:'Cable', group_id:'g3'})         " +
				"";

		try (Transaction tx = db.beginTx())
		{
			db.execute(cypher);
			tx.success();
		}
		
		// Act
		//
		List<Group> groups = g_dao.list();
		Group        group = groups.get(0);
		
		// Assert
		//
		assertNotNull(groups);
		assertEquals(1, groups.size());
		assertTrue("g2".equals(group.id()));
		assertTrue("Bravo".equals(group.name()));
	}

	@Test
	public void testListMultipleItems()
	{
		// Arrange
		//
		String cypher = 
				"CREATE                                                 " +
				"(a:PANDA  {group_name:'Alpha', group_id:'g1'}),        " +
				"(b:GROUP  {group_name:'Bravo', group_id:'g2'}),        " +
				"(c:PANDA  {group_name:'Cable', group_id:'g3'}),        " +
				"(d:PANDA  {group_name:'Donut', group_id:'g4'}),        " +
				"(e:GROUP  {group_name:'Edify', group_id:'g5'}),        " +
				"(f:GROUP  {group_name:'Gecko', group_id:'g6'}),        " +
				"(g:ZEBRA  )                                            " +
				"";

		try (Transaction tx = db.beginTx())
		{
			db.execute(cypher);
			tx.success();
		}
		
		// Act
		//
		List<Group> groups = g_dao.list();
		Set<String>  names = new HashSet<>();
		Set<String>    ids = new HashSet<>();
		
		for (int i = 0; i < groups.size(); i++)
		{
			Group group = groups.get(i);
			ids.add(group.id());
			names.add(group.name());
		}
		
		// Assert
		//
		assertNotNull(groups);
		assertEquals(3, groups.size());

		assertTrue(ids.contains("g2"));
		assertTrue(ids.contains("g5"));
		assertTrue(ids.contains("g6"));
		
		assertTrue(names.contains("Bravo"));
		assertTrue(names.contains("Edify"));
		assertTrue(names.contains("Gecko"));
	}

//	@Test
//	public void testSaveNoMembers()
//	{
//		// Arrange
//		//
//		String   id = "x123";
//		String name = "Kewl Kids";
//		Group     g = Group.builder().id(id).name(name).build();
//		
//		// Act
//		//
//		boolean           ok = g_dao.add(g);
//		Group          found = g_dao.findById(id);
//		List<Person> members = found.members();
//		
//		// Assert
//		//
//		assertTrue(ok);
//		assertNotNull(found);
//		assertNotNull(members);
//		assertTrue(id.equals(found.id()));
//		assertTrue(name.equals(found.name()));
//		assertEquals(0, members.size());
//	}

//	@Test
//	public void testDeleteById()
//	{
//		// Arrange
//		//
//		String cypher = 
//				"CREATE                                                 " +
//				"(a:GROUP  {group_name:'Alpha', group_id:'g1'}),        " +
//				"(p1:PERSON {person_id:'p1', person_name_first:'Rana', person_name_last:'Lee', person_nickname:'Ra'}),        " +
//				"(a)-[:GROUP_MEMBER]->(p1),                             " +
//				"(c:GROUP  {group_name:'Cable', group_id:'g3'})         " +
//				"";
//
//		try (Transaction tx = db.beginTx())
//		{
//			db.execute(cypher);
//			tx.success();
//		}
//		
//		
//		// Act
//		//
//		String            id = "g1";
//		boolean           ok = g_dao.deleteById(id);
//		Group          found = g_dao.findById(id);
//		
//		// Assert
//		//
//		assertTrue(ok);
//		assertNull(found);
//	}

//	@Test
//	public void testAddPersonToGroupWithNoMembers()
//	{
//		// Arrange
//		//
//		String cypher = 
//				"CREATE                                                 " +
//				"(a:GROUP  {group_name:'Alpha', group_id:'g1'}),        " +
//				"(c:GROUP  {group_name:'Cable', group_id:'g3'})         " +
//				"";
//
//		try (Transaction tx = db.beginTx())
//		{
//			db.execute(cypher);
//			tx.success();
//		}
//		
//		Person bill = Person.builder().id("Bill").firstName("Bill").lastName("Pruett").nickname("BT/SE").build();
//		Person barb = Person.builder().id("Barb").firstName("Barb").lastName("Beavers").nickname("Moo").build();
//		
//		PersonDao p_dao = new PersonDaoNeo(db);
//		p_dao.add(bill);
//		p_dao.add(barb);
//
//		// Act
//		//
//		String            id = "g3";
//		Group          group = g_dao.findById(id);
//		boolean      bill_ok = g_dao.addPersonToGroup(group, bill);
//		boolean      barb_ok = g_dao.addPersonToGroup(group, barb);
//		Group          delta = g_dao.findById(id);
//		List<Person> members = delta.members();
//		Set<String>    names = new HashSet<>();
//		Set<String>      ids = new HashSet<>();
//		
//		for (int i = 0; i < members.size(); i++)
//		{
//			Person person = members.get(i);
//			ids.add(person.id());
//			names.add(person.firstName());
//			names.add(person.lastName());
//		}
//		
//		// Assert
//		//
//		assertTrue(bill_ok);
//		assertTrue(barb_ok);
//		assertNotNull(delta);
//		assertNotNull(members);
//		assertEquals(2, members.size());
//		assertTrue(ids.contains(bill.id()));
//		assertTrue(ids.contains(barb.id()));
//		assertTrue(names.contains(bill.firstName()));
//		assertTrue(names.contains(barb.firstName()));
//		assertTrue(names.contains(bill.lastName()));
//		assertTrue(names.contains(barb.lastName()));
//	}

//	@Test
//	public void testAddPersonToGroupWithExistingMembers()
//	{
//		// Arrange
//		//
//		String cypher = 
//				"CREATE                                                 " +
//				"(a:GROUP  {group_name:'Alpha', group_id:'g1'}),        " +
//				"(p1:PERSON {person_id:'p1', person_name_first:'Rana', person_name_last:'Lee', person_nickname:'Ra'}),        " +
//				"(a)-[:GROUP_MEMBER]->(p1),                             " +
//				"(c:GROUP  {group_name:'Cable', group_id:'g3'})         " +
//				"";
//
//		try (Transaction tx = db.beginTx())
//		{
//			db.execute(cypher);
//			tx.success();
//		}
//		
//		Person bill = Person.builder().id("Bill").firstName("Bill").lastName("Pruett").nickname("BT/SE").build();
//		
//		PersonDao p_dao = new PersonDaoNeo(db);
//		p_dao.add(bill);
//		
//		// Act
//		//
//		String            id = "g1";
//		Group          group = g_dao.findById(id);
//		boolean      bill_ok = g_dao.addPersonToGroup(group, bill);
//		Group          delta = g_dao.findById(id);
//		List<Person> members = delta.members();
//		Set<String>    names = new HashSet<>();
//		Set<String>      ids = new HashSet<>();
//		
//		for (int i = 0; i < members.size(); i++)
//		{
//			Person person = members.get(i);
//			ids.add(person.id());
//			names.add(person.firstName());
//			names.add(person.lastName());
//		}
//		
//		// Assert
//		//
//		assertTrue(bill_ok);
//		assertNotNull(delta);
//		assertNotNull(members);
//		assertEquals(2, members.size());
//		assertTrue(names.contains("Bill"));
//		assertTrue(names.contains("Pruett"));
//		assertTrue(names.contains("Rana"));
//		assertTrue(names.contains("Lee"));
//		assertTrue(ids.contains("p1"));
//		assertTrue(ids.contains(bill.id()));
//	}

//	@Test
//	public void testRemovePersonFromGroup()
//	{
//		// Arrange
//		//
//		String cypher = 
//				"CREATE                                                 " +
//				"(a:GROUP  {group_name:'Alpha', group_id:'g1'}),        " +
//				"(p1:PERSON {person_id:'p1', person_name_first:'Rana', person_name_last:'Lee', person_nickname:'Ra'}),        " +
//				"(a)-[:GROUP_MEMBER]->(p1),                             " +
//				"(c:GROUP  {group_name:'Cable', group_id:'g3'})         " +
//				"";
//
//		try (Transaction tx = db.beginTx())
//		{
//			db.execute(cypher);
//			tx.success();
//		}
//		
//		Person person = Person.builder().id("p1").firstName("Bill").lastName("Pruett").nickname("BT/SE").build();
//		
//		
//		// Act
//		//
//		String            id = "g1";
//		Group          group = g_dao.findById(id);
//		boolean           ok = g_dao.removePersonFromGroup(group, person);
//		Group          delta = g_dao.findById(id);
//		List<Person> members = delta.members();
//		
//		// Assert
//		//
//		assertTrue(ok);
//		assertNotNull(delta);
//		assertNotNull(members);
//		assertEquals(0, members.size());
//	}

	@Test
	public void testFindByIdWhichDoesNotExist()
	{
		// Arrange
		//
		String cypher = 
				"CREATE                                                 " +
				"(a:GROUP  {group_name:'Alpha', group_id:'g1'}),        " +
				"(b:GROUP  {group_name:'Bravo', group_id:'g2'}),        " +
				"(c:GROUP  {group_name:'Cable', group_id:'g3'})         " +
				"";

		try (Transaction tx = db.beginTx())
		{
			db.execute(cypher);
			tx.success();
		}
		
		// Act
		//
		String   id = "Pickle";
		Group group = g_dao.findById(id);
		
		// Assert
		//
		assertNull(group);
	}

//	@Test
//	public void testFindByIdWhichDoesExistNoMembers()
//	{
//		// Arrange
//		//
//		String cypher = 
//				"CREATE                                                 " +
//				"(a:GROUP  {group_name:'Alpha', group_id:'g1'}),        " +
//				"(b:GROUP  {group_name:'Bravo', group_id:'g2'}),        " +
//				"(c:GROUP  {group_name:'Cable', group_id:'g3'})         " +
//				"";
//
//		try (Transaction tx = db.beginTx())
//		{
//			db.execute(cypher);
//			tx.success();
//		}
//		
//		// Act
//		//
//		String            id = "g2";
//		Group          group = g_dao.findById(id);
//		List<Person> members = group.members();
//		
//		// Assert
//		//
//		assertNotNull(group);
//		assertNotNull(members);
//		assertEquals(0, members.size());
//	}

//	@Test
//	public void testFindByIdWhichDoesExistAndHasMembers()
//	{
//		// Arrange
//		//
//		String cypher = 
//			"CREATE                                                                                                       " +
//			"(a:GROUP  {group_name:'Alpha', group_id:'g1'}),                                                              " +
//			"(p1:PERSON {person_id:'p1', person_name_first:'Rana', person_name_last:'Lee', person_nickname:'Ra'}),        " +
//			"(a)-[:GROUP_MEMBER{level:1}]->(p1),                                                                          " + 
//			"(b:GROUP  {group_name:'Bravo', group_id:'g2'}),                                                              " +
//			"(p2:PERSON {person_id:'p2', person_name_first:'Ryan', person_name_last:'Turner'}),                           " +
//			"(c:GROUP  {group_name:'Cable', group_id:'g3'}),                                                              " +
//			"(p3:PERSON {person_id:'p3', person_name_first:'Ryan', person_name_last:'McDowell', person_nickname:'Junk'}), " +
//			"(c)-[:GROUP_MEMBER{level:1}]->(p1),                                                                          " + 
//			"(c)-[:GROUP_MEMBER{level:2}]->(p2)                                                                           " + 
//			"";
//
//		try (Transaction tx = db.beginTx())
//		{
//			db.execute(cypher);
//			tx.success();
//		}
//		
//		// Act
//		//
//		String            id = "g3";
//		Group          group = g_dao.findById(id);
//		List<Person> members = group.members();
//		Set<String>    names = new HashSet<>();
//		Set<String>      ids = new HashSet<>();
//		
//		for (int i = 0; i < members.size(); i++)
//		{
//			Person person = members.get(i);
//			ids.add(person.id());
//			names.add(person.firstName());
//			names.add(person.lastName());
//		}
//		
//		// Assert
//		//
//		assertNotNull(group);
//		assertNotNull(members);
//		assertEquals(2, members.size());
//
//		assertTrue(ids.contains("p1"));
//		assertTrue(ids.contains("p2"));
//		
//		assertTrue(names.contains("Rana"));
//		assertTrue(names.contains("Lee"));
//		assertTrue(names.contains("Ryan"));
//		assertTrue(names.contains("Turner"));
//
//		assertFalse(names.contains("McDowell"));
//	}
}
