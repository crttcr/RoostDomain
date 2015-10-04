package xivvic.roost.dao.neo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.LocalTime;
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

import xivvic.roost.domain.Event;
import xivvic.roost.domain.EventType;

public class EventDaoNeoTest
{
	// The DAO under test
	//
	private EventDaoNeo dao;

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
		
		dao = new EventDaoNeo(db);
	}

	@After
	public void tearDown() throws Exception
	{
		if (db != null)
		{
			db.shutdown();
		}
		dao = null;
	}

	@Test
	public void testFindByIdWhichDoesNotExist()
	{
		// Arrange
		//
		String cypher = "CREATE " +
		"(a:EVENT {event_id:'e1', event_text:'BDay', event_type:'BIRTHDAY', event_date:50000, event_time:520000000}), " +
		"(b:EVENT {event_id:'e2', event_text:'BDay', event_type:'BIRTHDAY', event_date:50001, event_time:520000000}), " +
		"(c:GROUP {group_name:'Cable', group_id:'g1'})"; 

		try (Transaction tx = db.beginTx())
		{
			db.execute(cypher);
			tx.success();
		}
		
		// Act
		//
		String    id = "Donut";
		Event event = dao.findById(id);
		
		// Assert
		//
		assertNull(event);
	}

	@Test
	public void testFindByIdWhichExists()
	{
		// Arrange
		//
		String cypher = "CREATE " +
		"(a:EVENT {event_id:'e1', event_text:'BDay', event_type:'BIRTHDAY', event_date:50000, event_time:520000000}), " +
		"(b:EVENT {event_id:'e2', event_text:'BDay', event_type:'BIRTHDAY', event_date:50001, event_time:525000000}), " +
		"(c:GROUP {group_name:'Cable', group_id:'g1'})"; 

		try (Transaction tx = db.beginTx())
		{
			db.execute(cypher);
			tx.success();
		}

		String             id = "e2";
		LocalDate expect_date = LocalDate.ofEpochDay(50001);
		LocalTime expect_time = LocalTime.ofNanoOfDay(525000000);
		
		// Act
		//
		Event event = dao.findById(id);
		
		// Assert
		//
		assertNotNull(event);
		assertTrue(id.equals(event.id()));
		assertEquals("BDay", event.text());
		assertEquals(EventType.BIRTHDAY, event.type());
		assertEquals(expect_date, event.date());
		assertEquals(expect_time, event.time());
	}

	@Test
	public void testList()
	{
		// Arrange
		//
		String cypher = "CREATE " +
		"(a:EVENT {event_id:'e1', event_text:'BDay', event_type:'BIRTHDAY',    event_date:50000, event_time:520000000}), " +
		"(b:EVENT {event_id:'e2', event_text:'ANiv', event_type:'ANNIVERSARY', event_date:50001, event_time:525000000}), " +
		"(c:GROUP {group_name:'Cable', group_id:'g1'})"; 


		try (Transaction tx = db.beginTx())
		{
			db.execute(cypher);
			tx.success();
		}
		
		// Act
		//
		Set<Object>  props = new HashSet<>();
		List<Event> list = dao.list();
		
		for (Event event : list)
		{
			props.add(event.id());
			props.add(event.text());
			props.add(event.type());
			props.add(event.time());
			props.add(event.date());
		}
		
		// Assert
		//
		assertNotNull(list);
		assertEquals(2, list.size());
		assertTrue(props.contains("e1"));
		assertTrue(props.contains("e2"));
		assertTrue(props.contains("ANiv"));
		assertTrue(props.contains("BDay"));
		assertTrue(props.contains(EventType.ANNIVERSARY));
		assertTrue(props.contains(EventType.BIRTHDAY));
		assertTrue(props.contains(LocalDate.ofEpochDay(50000)));
		assertTrue(props.contains(LocalDate.ofEpochDay(50001)));
		assertTrue(props.contains(LocalTime.ofNanoOfDay(520000000)));
		assertTrue(props.contains(LocalTime.ofNanoOfDay(525000000)));
	}

	@Test
	public void testListWhenNoEventNodesExist()
	{
		// Arrange
		//
		String cypher = "CREATE " +
		"(a:CAT {event_id:'e1', event_text:'BDay', event_type:'BIRTHDAY',    event_date:50000, event_time:520000000}), " +
		"(b:DOG {event_id:'e2', event_text:'ANiv', event_type:'ANNIVERSARY', event_date:50001, event_time:525000000}), " +
		"(c:GROUP {group_name:'Cable', group_id:'g1'})"; 

		try (Transaction tx = db.beginTx())
		{
			db.execute(cypher);
			tx.success();
		}
		
		// Act
		//
		List<Event> list = dao.list();
		
		// Assert
		//
		assertNotNull(list);
		assertEquals(0, list.size());
	}

//	@Test
//	public void testDeleteById()
//	{
//		// Arrange
//		//
//		String cypher = "CREATE " +
//		"(a:EVENT {event_id:'e1', event_text:'BDay', event_type:'BIRTHDAY',    event_date:50000, event_time:520000000}), " +
//		"(b:EVENT {event_id:'e2', event_text:'ANiv', event_type:'ANNIVERSARY', event_date:50001, event_time:525000000}), " +
//		"(c:GROUP {group_name:'Cable', group_id:'g1'})"; 
//
//		try (Transaction tx = db.beginTx())
//		{
//			db.execute(cypher);
//			tx.success();
//		}
//		
//		// Act
//		//
//		boolean         ok_1 = dao.deleteById("e2");
//		List<Event> list_1 = dao.list();
//		boolean         ok_2 = dao.deleteById("e1");
//		List<Event> list_2 = dao.list();
//		
//		// Assert
//		//
//		assertTrue(ok_1);
//		assertTrue(ok_2);
//		assertNotNull(list_1);
//		assertNotNull(list_2);
//		assertEquals(1, list_1.size());
//		assertEquals(0, list_2.size());
//	}

//	@Test
//	public void testDeleteByIdNotFound()
//	{
//		// Arrange
//		//
//		String cypher = "CREATE " +
//		"(a:EVENT {event_id:'e1', event_text:'BDay', event_type:'BIRTHDAY',    event_date:50000, event_time:520000000}), " +
//		"(b:EVENT {event_id:'e2', event_text:'ANiv', event_type:'ANNIVERSARY', event_date:50001, event_time:525000000}), " +
//		"(c:GROUP {group_name:'Cable', group_id:'g1'})"; 
//
//		try (Transaction tx = db.beginTx())
//		{
//			db.execute(cypher);
//			tx.success();
//		}
//		
//		// Act
//		//
//		boolean       ok = dao.deleteById("Pancake");
//		List<Event> list = dao.list();
//		
//		// Assert
//		//
//		assertFalse(ok);
//		assertNotNull(list);
//		assertEquals(2, list.size());
//	}

}
