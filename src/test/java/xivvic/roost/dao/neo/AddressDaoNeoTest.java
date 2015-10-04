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

import xivvic.roost.domain.Address;

public class AddressDaoNeoTest
{
	// The DAO under test
	//
	private AddressDaoNeo a_dao;

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
		
		a_dao = new AddressDaoNeo(db);
	}

	@After
	public void tearDown() throws Exception
	{
		if (db != null)
		{
			db.shutdown();
		}
		a_dao = null;
	}

	@Test
	public void testFindByIdWhichDoesNotExist()
	{
		// Arrange
		//
		String cypher = 
				"CREATE                                                                                                                                                 " +
				"(a:ADDRESS  {address_id:'a1', address_line_one:'100 Egg St.', address_line_two:'Apt 3', address_city:'Ev',address_state:'IL', address_zip:'60201' }),        " +
				"(b:ADDRESS  {address_id:'a2', address_line_one:'100 Egg St.', address_line_two:'Apt 4', address_city:'Ev',address_state:'IL', address_zip:'60201' }),        " +
				"(c:GROUP    {group_name:'Cable', group_id:'g3'})                                                                                                       " +
				"";

		try (Transaction tx = db.beginTx())
		{
			db.execute(cypher);
			tx.success();
		}
		
		// Act
		//
		String    id = "Donut";
		Address addr = a_dao.findById(id);
		
		// Assert
		//
		assertNull(addr);
	}

	@Test
	public void testFindByIdWhichExists()
	{
		// Arrange
		//
		String cypher = 
				"CREATE                                                                                                                                                 " +
				"(a:ADDRESS  {address_id:'a1', address_line_one:'100 Egg St.', address_line_two:'Apt 3', address_city:'Ev',address_state:'IL', address_zip:'60201' }),        " +
				"(b:ADDRESS  {address_id:'a2', address_line_one:'100 Egg St.', address_line_two:'Apt 4', address_city:'Ev',address_state:'IL', address_zip:'60201' }),        " +
				"(c:GROUP    {group_name:'Cable', group_id:'g3'})                                                                                                       " +
				"";

		try (Transaction tx = db.beginTx())
		{
			db.execute(cypher);
			tx.success();
		}
		
		// Act
		//
		String    id = "a2";
		Address addr = a_dao.findById(id);
		
		// Assert
		//
		assertNotNull(addr);
		assertTrue(id.equals(addr.id()));
		assertTrue("100 Egg St.".equals(addr.lineOne()));
		assertTrue("Apt 4".equals(addr.lineTwo()));
		assertTrue("Ev".equals(addr.city()));
		assertTrue("IL".equals(addr.state()));
		assertTrue("60201".equals(addr.zip()));
	}

	@Test
	public void testList()
	{
		// Arrange
		//
		String cypher = 
				"CREATE                                                                                                                                                 " +
				"(a:ADDRESS  {address_id:'a1', address_line_one:'100 Egg St.', address_line_two:'Apt 3', address_city:'Ev',address_state:'IL', address_zip:'60201' }),        " +
				"(b:ADDRESS  {address_id:'a2', address_line_one:'100 Egg St.', address_line_two:'Apt 4', address_city:'Ev',address_state:'IL', address_zip:'60201' }),        " +
				"(c:GROUP    {group_name:'Cable', group_id:'g3'})                                                                                                       " +
				"";

		try (Transaction tx = db.beginTx())
		{
			db.execute(cypher);
			tx.success();
		}
		
		// Act
		//
		Set<String>  props = new HashSet<>();
		List<Address> list = a_dao.list();
		
		for (Address addr : list)
		{
			props.add(addr.id());
			props.add(addr.lineOne());
			props.add(addr.lineTwo());
			props.add(addr.city());
			props.add(addr.state());
			props.add(addr.zip());
		}
		
		// Assert
		//
		assertNotNull(list);
		assertEquals(2, list.size());
		assertTrue(props.contains("100 Egg St."));
		assertTrue(props.contains("Apt 3"      ));
		assertTrue(props.contains("Apt 4"      ));
		assertTrue(props.contains("Ev"         ));
		assertTrue(props.contains("IL"         ));
		assertTrue(props.contains("60201"      ));
	}

	@Test
	public void testListWhenNoAddressNodesExist()
	{
		// Arrange
		//
		String cypher = 
				"CREATE                                                                                                                                                 " +
				"(a:BANANA  {address_id:'a1', address_line_one:'100 Egg St.', address_line_two:'Apt 3', address_city:'Ev',address_state:'IL', address_zip:'60201' }),        " +
				"(b:RAPHAE  {address_id:'a2', address_line_one:'100 Egg St.', address_line_two:'Apt 4', address_city:'Ev',address_state:'IL', address_zip:'60201' }),        " +
				"(c:SNOOPY  {group_name:'Cable', group_id:'g3'})                                                                                                       " +
				"";

		try (Transaction tx = db.beginTx())
		{
			db.execute(cypher);
			tx.success();
		}
		
		// Act
		//
		List<Address> list = a_dao.list();
		
		// Assert
		//
		assertNotNull(list);
		assertEquals(0, list.size());
	}

}
