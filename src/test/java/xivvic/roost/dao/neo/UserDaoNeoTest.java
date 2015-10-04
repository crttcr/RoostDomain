package xivvic.roost.dao.neo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;

import xivvic.neotest.program.RoostNodeType;
import xivvic.roost.domain.Group;
import xivvic.roost.domain.Person;
import xivvic.roost.domain.User;

@RunWith(MockitoJUnitRunner.class)
public class UserDaoNeoTest
{
	// The DAO under test
	//
	private UserDaoNeo u_dao;

	public final Label  label = RoostNodeType.USER;
	
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
		
		u_dao = new UserDaoNeo(db);
	}

	@After
	public void tearDown() throws Exception
	{
		db.shutdown();
		u_dao = null;
	}

	@Test
	public void testFindByEmailNotFound()
	{
		// Arrange
		//
		String cypher = 
				"CREATE                                                                                                 " +
				"(a:USER  {user_username:'crt', user_passhash:'997', user_email:'joe@blow_com', user_id:'abc'}),        " +
				"(b:USER  {user_username:'dct', user_passhash:'993', user_email:'doug@nee.com', user_id:'bcd'}),        " +
				"(g:GROUP {group_name:'group',  group_id:'g123'}),                                                      " +
				"(a)-[:USER_GROUP]->(g)                                                                                 " + 
				"";

		try (Transaction tx = db.beginTx())
		{
			db.execute(cypher);
			tx.success();
		}
		
		// Act
		//
		String email = "foo@bar.com";
		User    user = u_dao.findByEmail(email);
		
		// Assert
		//
		assertNull(user);
	}

	@Test
	public void testFindByEmailFound()
	{
		// Arrange
		//
		String cypher = 
				"CREATE                                                                                                 " +
				"(a:USER  {user_username:'crt', user_passhash:'997', user_email:'joe@blow.com', user_id:'abc'}),        " +
				"(b:USER  {user_username:'dct', user_passhash:'993', user_email:'doug@nee.com', user_id:'bcd'}),        " +
				"(p:PERSON {person_name_first:'Rana', person_name_last:'Lee', person_id:'p123'}),                       " +
				"(g:GROUP {group_name:'group', group_id:'g123'}),                                                       " +
				"(b)-[:USER_PERSON]->(p),                                                                               " + 
				"(b)-[:USER_GROUP]->(g)                                                                                 " + 
				"";

		try (Transaction tx = db.beginTx())
		{
			db.execute(cypher);
			tx.success();
		}
		
		// Act
		//
		String email = "doug@nee.com";
		User    user = u_dao.findByEmail(email);
		
		// Assert
		//
		assertNotNull(user);
	}


	@Test
	public void testListEmpty()
	{
		// Arrange
		//
		String cypher = 
				"CREATE                                                                                                  " +
				"(a:EVENT  {user_username:'crt', user_passhash:'997', user_email:'joe@blow.com', user_id:'abc'}),        " +
				"(b:PERSON {user_username:'dct', user_passhash:'993', user_email:'doug@nee.com', user_id:'bcd'}),        " +
				"(g:GROUP {group_name:'group', group_id:'g123'}),                                                        " +
				"(g)-[:GROUP_MEMBER]->(b)                                                                                  " + 
				"";

		try (Transaction tx = db.beginTx())
		{
			db.execute(cypher);
			tx.success();
		}
		
		// Act
		//
		List<User> users = u_dao.list();
		
		// Assert
		//
		assertNotNull(users);
		assertEquals(0, users.size());
	}

	@Test
	public void testListOneItem()
	{
		// Arrange
		//
		// User -> (:USER_GROUP ) -> Group -> (:GROUP_MEMBER) -> ()
		//      -> (:USER_PERSON) -> Person
		//
		String cypher = 
				"CREATE                                                                                                               " +
				"(a:USER   {user_username:'crt', user_passhash:'997', user_email:'joe@blow.com', user_id:'abc' }),                    " +
				"(g:GROUP  {group_name:'group', group_id:'g1234'}),                                                                   " +
				"(p:PERSON {person_name_first:'Rana', person_name_last:'Lee', person_id:'p123'}),                                     " +
				"(a)-[:USER_GROUP]->(g),                                                                                              " + 
				"(a)-[:USER_PERSON]->(p)                                                                                              " + 
				"";

		try (Transaction tx = db.beginTx())
		{
			db.execute(cypher);
			tx.success();
		}

		// Act
		//
		List<User>  users = u_dao.list();
		User        found = users.get(0);
		Group       group = found.group();
		Person     person = found.person();
		
		// Assert
		//
		assertNotNull(users);
		assertEquals(1, users.size());
		assertNotNull(found);
		assertTrue("abc".equals(found.id()));
		assertTrue("crt".equals(found.username()));
		assertTrue("997".equals(found.passhash()));
		assertTrue("joe@blow.com".equals(found.email()));

		assertNotNull(group);
		assertTrue("g1234".equals(group.id()));
		assertTrue("group".equals(group.name()));

		assertNotNull(person);
		assertTrue("p123".equals(person.id()));
	}

	@Test
	public void testListTwoItems()
	{
		// Arrange
		//
		String cypher = 
			"CREATE                                                                                                 " +
			"(a:USER  {user_username:'crt', user_passhash:'997', user_email:'joe@blow_com', user_id:'abc'}),        " +
			"(b:USER  {user_username:'dct', user_passhash:'993', user_email:'doug@nee.com', user_id:'bcd'}),        " +
			"(p:PERSON {person_name_first:'Rana', person_name_last:'Lee', person_id:'p123'}),                       " +
			"(g:GROUP {group_name:'group',  group_id:'g123'}),                                                      " +
			"(a)-[:USER_GROUP]->(g),                                                                                " + 
			"(b)-[:USER_GROUP]->(g),                                                                                " + 
			"(a)-[:USER_PERSON]->(p),                                                                               " + 
			"(b)-[:USER_PERSON]->(p)                                                                                " + 
			"";

		try (Transaction tx = db.beginTx())
		{
			db.execute(cypher);
			tx.success();
		}
		
		// Act
		//
		List<User>  users = u_dao.list();
		User          one = users.get(0);
		User          two = users.get(1);
		
		// Assert
		//
		assertNotNull(users);
		assertEquals(2, users.size());
		assertNotNull(one);
		assertNotNull(two);
		assertTrue(one.group().equals(two.group()));
	}


//	@Test
//	public void testRegisterUser()
//	{
//		// Arrange
//		//
//		String    id = "XYX^^XYX";
//		String email = "alpha@beta.edu";
//		
//		Map<String, String> map = new HashMap<>();
//		map.put(UserDao.PROP_ID   , id);
//		map.put(UserDao.PROP_EMAIL, email);
//		map.put(UserDao.PROP_UNAME, "donkey_boy");
//		map.put(UserDao.PROP_PHASH, "IRKDRKD(#KDR(DKKS(RDJ");
//		map.put(GroupDao.PROP_NAME, "Fred");
//		map.put(PersonDao.PROP_ID,  "p123");
//		
//		RegistrationInformation ri = RegistrationInformation.create(map);
//
//		String cypher = 
//				"CREATE                                                                                                 " +
//				"(a:USER  {user_username:'crt', user_passhash:'997', user_email:'joe@blow_com', user_id:'abc'}),        " +
//				"(b:USER  {user_username:'dct', user_passhash:'993', user_email:'doug@nee.com', user_id:'bcd'}),        " +
//				"(p:PERSON {person_name_first:'Rana', person_name_last:'Lee', person_id:'p123'}),                       " +
//				"(g:GROUP {group_name:'Fred',  group_id:'g123'}),                                                       " +
//				"(a)-[:USER_GROUP]->(g),                                                                                " + 
//				"(b)-[:USER_GROUP]->(g),                                                                                " + 
//				"(a)-[:USER_PERSON]->(p),                                                                               " + 
//				"(b)-[:USER_PERSON]->(p)                                                                                " + 
//				"";
//
//			try (Transaction tx = db.beginTx())
//			{
//				db.execute(cypher);
//				tx.success();
//			}
//		
//
//		// Act
//		//
//		User created = u_dao.registerUser(ri);
//		
//		// Assert
//		//
//		assertNotNull(created);
//		assertNotNull(created.id());
//		assertTrue(id.equals(created.id()));
//		assertTrue(email.equals(created.email()));
//		assertTrue("Fred".equals(created.group().name()));
//	}
//
//	@Test
//	public void testRegisterNull()
//	{
//		// Arrange
//		//
//		RegistrationInformation ri = null;
//		
//		// Act
//		//
//		User user = u_dao.registerUser(ri);
//		
//		// Assert
//		//
//		assertNull(user);
//	}
//
//	@Test
//	public void testDeleteByIdNull()
//	{
//		// Arrange
//		//
//		String id = null;
//		
//		// Act
//		//
//		boolean result = u_dao.deleteById(id);
//		
//		// Assert
//		//
//		assertFalse(result);
//	}
//
//	@Test
//	public void testDeleteByIdOneRelationship()
//	{
//		// Arrange
//		//
//		String    id = "FunkyChicken";
//		String email = "funk@warbird.com";
//		String cypher = 
//				"CREATE                                                                                                               " +
//				"(a:USER   {user_username:'crt', user_passhash:'997', user_email:'funk@warbird.com', user_id:'FunkyChicken' }),       " +
//				"(g:GROUP  {group_name:'group', group_id:'g1234'}),                                                                   " +
//				"(p:PERSON {person_name_first:'Rana', person_name_last:'Lee', person_id:'p123'}),                                     " +
//				"(a)-[:USER_GROUP]->(g),                                                                                              " + 
//				"(a)-[:USER_PERSON]->(p)                                                                                              " + 
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
//		User    before = u_dao.findByEmail(email);
//		boolean result = u_dao.deleteById(id);
//		User     after = u_dao.findByEmail(email);
//		
//		// Assert
//		//
//		assertTrue(result);
//		assertNotNull(before);
//		assertNull(after);
//	}
//
//	@Test
//	public void testDeleteByIdThreeRelationships()
//	{
//		// Arrange
//		//
//		String    id = "FunkyChicken";
//		String email = "funk@warbird.com";
//		String cypher = 
//				"CREATE                                                                                                               " +
//				"(a:USER   {user_username:'crt', user_passhash:'997', user_email:'funk@warbird.com', user_id:'FunkyChicken' }),       " +
//				"(m:MAC    {computer_maker:'apple', operating_system:'OSX'}),                                                         " +
//				"(g:GROUP  {group_name:'group', group_id:'g1234'}),                                                                   " +
//				"(p:PERSON {person_name_first:'Rana', person_name_last:'Lee', person_id:'p123'}),                                     " +
//				"(a)-[:USER_GROUP]->(g),                                                                                              " + 
//				"(a)-[:USER_COMPUTER]->(m),                                                                                         " + 
//				"(a)-[:USER_PERSON]->(p)                                                                                              " + 
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
//		User    before = u_dao.findByEmail(email);
//		boolean result = u_dao.deleteById(id);
//		User     after = u_dao.findByEmail(email);
//		
//		// Assert
//		//
//		assertTrue(result);
//		assertNotNull(before);
//		assertNull(after);
//	}

}
