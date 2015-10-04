package xivvic.roost.dao.neo;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

import xivvic.neotest.program.RoostNodeType;
import xivvic.roost.domain.Person;

@RunWith(MockitoJUnitRunner.class)
public class PersonDaoNeoTest
{
	// The DAO under test
	//
	private PersonDaoNeo p_dao;

	private final Label  label = RoostNodeType.PERSON;
	
	// My Objects that need mocking in this set of tests
	//
	@Mock private GraphDatabaseService mock_db;
	@Mock private Transaction          mock_tx;
	@Mock private Node                 mock_node;
	@Mock private Node                 mock_node_1;
	@Mock private Node                 mock_node_2;
	@Mock ResourceIterator<Node>       mock_rit; 
	@Mock Iterable<Relationship>       iterable;
	@Mock Iterator<Relationship>       iterator;
	@Mock Relationship                 rship_1;
	@Mock Relationship                 rship_2;

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
		p_dao   = new PersonDaoNeo(mock_db);
	}

	@After
	public void tearDown() throws Exception
	{
		p_dao = null;
	}

	@Test
	public void testFindByIdNotFound()
	{
		// Arrange
		//
		String id = "Foobar";
		when(mock_db.beginTx()).thenReturn(mock_tx);
		when(mock_db.findNode(any(), any(), any())).thenReturn(null);
		
		// Act
		//
		Person person = p_dao.findById(id);
		
		// Assert
		//
		assertThat(person, is(nullValue()));
		verify(mock_db, atLeastOnce()).beginTx();
		verify(mock_db, times(1)).findNode(any(), any(), any());
		verify(mock_tx, atLeastOnce()).success();
		verify(mock_tx, times(1)).close();
	}

	@Test
	public void testFindByIdFound()
	{
		// Arrange
		//
		Person expected = Person.builder().id("ABC123").firstName("Joe").lastName("I").build();
		
		when(mock_node.getProperty(eq(Person.PROP_ID        ))).thenReturn(expected.id());
		when(mock_node.getProperty(eq(Person.PROP_FIRST_NAME))).thenReturn(expected.firstName());
		when(mock_node.getProperty(eq(Person.PROP_LAST_NAME ))).thenReturn(expected.lastName());
		
		when(mock_db.beginTx()).thenReturn(mock_tx);
		when(mock_db.findNode(eq(label), eq(Person.PROP_ID), eq(expected.id()))).thenReturn(mock_node);
		
		// Act
		//
		Person person = p_dao.findById(expected.id());
		
		// Assert
		//
		assertNotNull(person);
		assertTrue(expected.equals(person));
		verify(mock_db, atLeastOnce()).beginTx();
		verify(mock_db, times(1)).findNode(any(), any(), any());
		verify(mock_tx, atLeastOnce()).success();
		verify(mock_tx, times(1)).close();
	}


	@Test
	public void testListEmpty()
	{
		// Arrange
		//
		when(mock_db.beginTx()).thenReturn(mock_tx);
		when(mock_db.findNodes(eq(label))).thenReturn(mock_rit);

		when(mock_rit.hasNext()).thenReturn(false);
		when(mock_rit.next()).thenThrow(new RuntimeException());
		
		// Act
		//
		List<Person> people = p_dao.list();
		
		// Assert
		//
		assertNotNull(people);
		assertEquals(0, people.size());
		verify(mock_db, times(1)).beginTx();
		verify(mock_tx, atLeastOnce()).success();
		verify(mock_tx, times(1)).close();
	}

	@Test
	public void testListOneItem()
	{
		// Arrange
		//
		Person expected = Person.builder().id("5:35MUPN").firstName("Five").lastName("ThirtyFive").middleName("Davis").build();
		
		when(mock_node.getProperty(eq(Person.PROP_ID          ))).thenReturn(expected.id());
		when(mock_node.getProperty(eq(Person.PROP_FIRST_NAME  ))).thenReturn(expected.firstName());
		when(mock_node.getProperty(eq(Person.PROP_MIDDLE_NAME ), eq(null))).thenReturn(expected.middleName());
		when(mock_node.getProperty(eq(Person.PROP_LAST_NAME   ))).thenReturn(expected.lastName());
		
		when(mock_db.beginTx()).thenReturn(mock_tx);
		when(mock_db.findNodes(eq(label))).thenReturn(mock_rit);

		when(mock_rit.hasNext()).thenReturn(true).thenReturn(false);
		when(mock_rit.next()).thenReturn(mock_node).thenThrow(new RuntimeException());
		
		// Act
		//
		List<Person> people = p_dao.list();
		Person        found = people.get(0);
		
		// Assert
		//
		assertNotNull(people);
		assertNotNull(found);
		assertTrue(expected.equals(found));
		assertEquals(1, people.size());
		verify(mock_db, times(1)).beginTx();
		verify(mock_tx, atLeastOnce()).success();
		verify(mock_tx, times(1)).close();
	}

	@Test
	public void testListTwoItems()
	{
		// Arrange
		//
		Person first  = Person.builder().id("#2").firstName("Jackie").lastName("Chan").build();
		Person second = Person.builder().id("#1").firstName("Bruce" ).lastName("Lee" ).middleName("DragonSlayer").build();
		
		when(mock_node_1.getProperty(eq(Person.PROP_ID          ))).thenReturn(first.id());
		when(mock_node_1.getProperty(eq(Person.PROP_FIRST_NAME  ))).thenReturn(first.firstName());
		when(mock_node_1.getProperty(eq(Person.PROP_MIDDLE_NAME ), eq(null))).thenReturn(first.middleName());
		when(mock_node_1.getProperty(eq(Person.PROP_LAST_NAME   ))).thenReturn(first.lastName());
		
		when(mock_node_2.getProperty(eq(Person.PROP_ID          ))).thenReturn(second.id());
		when(mock_node_2.getProperty(eq(Person.PROP_FIRST_NAME  ))).thenReturn(second.firstName());
		when(mock_node_2.getProperty(eq(Person.PROP_MIDDLE_NAME ), eq(null))).thenReturn(second.middleName());
		when(mock_node_2.getProperty(eq(Person.PROP_LAST_NAME   ))).thenReturn(second.lastName());
		
		when(mock_db.beginTx()).thenReturn(mock_tx);
		when(mock_db.findNodes(eq(label))).thenReturn(mock_rit);

		when(mock_rit.hasNext()).thenReturn(true).thenReturn(true).thenReturn(false);
		when(mock_rit.next()).thenReturn(mock_node_1).thenReturn(mock_node_2).thenThrow(new RuntimeException());
		
		// Act
		//
		List<Person> people = p_dao.list();
		Person          one = people.get(0);
		Person          two = people.get(1);
		
		// Assert
		//
		assertNotNull(people);
		assertNotNull(one);
		assertNotNull(two);
		assertTrue(first .equals(one));
		assertTrue(second.equals(two));
		assertEquals(2, people.size());
		verify(mock_db, times(1)).beginTx();
		verify(mock_tx, atLeastOnce()).success();
		verify(mock_tx, times(1)).close();
	}

//
//	@Test
//	public void testSave2Node()
//	{
//		// Arrange
//		//
//		Person person = Person.builder().id("id").firstName("fn").lastName("ln")
//				.middleName("mn").nickname("nn").build();
//		
//		when(mock_db.beginTx()).thenReturn(mock_tx);
//		when(mock_db.createNode(label)).thenReturn(mock_node);
//
//		// Act
//		//
//		Node n = p_dao.save2Node(person);
//		
//		// Assert
//		//
//		assertNotNull(n);
//		verify(mock_db, times(1)).beginTx();
//		verify(mock_tx, times(1)).close();
//		verify(mock_node, atLeastOnce()).setProperty(eq(PersonDao.PROP_ID         ), eq(person.id()));
//		verify(mock_node, atLeastOnce()).setProperty(eq(PersonDao.PROP_FIRST_NAME ), eq(person.firstName()));
//		verify(mock_node, atLeastOnce()).setProperty(eq(PersonDao.PROP_MIDDLE_NAME), eq(person.middleName()));
//		verify(mock_node, atLeastOnce()).setProperty(eq(PersonDao.PROP_LAST_NAME  ), eq(person.lastName()));
//		verify(mock_node, atLeastOnce()).setProperty(eq(PersonDao.PROP_NICKNAME   ), eq(person.nickname()));
//	}
//
//	@Test
//	public void testSaveNull()
//	{
//		// Arrange
//		//
//		Person p = null;
//		
//		// Act
//		//
//		boolean ok = p_dao.add(p);
//		
//		// Assert
//		//
//		assertFalse(ok);
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
//		boolean result = p_dao.deleteById(id);
//		
//		// Assert
//		//
//		assertFalse(result);
//	}
//
//	@Test
//	public void testDeleteByIdNoRelationships()
//	{
//		// Arrange
//		//
//		String id = "FunkyChicken";
//		
//		when(mock_db.beginTx()).thenReturn(mock_tx);
//		when(mock_db.findNode(label, PersonDao.PROP_ID, id)).thenReturn(mock_node);
//		when(mock_node.getRelationships()).thenReturn(iterable);
//		when(iterable.iterator()).thenReturn(iterator);
//		when(iterator.hasNext()).thenReturn(false);
//		when(iterator.next()).thenThrow(new RuntimeException());
//
//		// Act
//		//
//		boolean result = p_dao.deleteById(id);
//		
//		// Assert
//		//
//		assertTrue(result);
//		verify(mock_db, times(1)).beginTx();
//		verify(mock_tx, atLeastOnce()).success();
//		verify(mock_tx, times(1)).close();
//		verify(mock_node, times(1)).delete();
//	}
//
//	@Test
//	public void testDeleteByIdTwoRelationships()
//	{
//		// Arrange
//		//
//		String id = "StinkyChicken";
//		
//		when(mock_db.beginTx()).thenReturn(mock_tx);
//		when(mock_db.findNode(label, PersonDao.PROP_ID, id)).thenReturn(mock_node);
//		when(mock_node.getRelationships()).thenReturn(iterable);
//		when(iterable.iterator()).thenReturn(iterator);
//		when(iterator.hasNext()).thenReturn(true).thenReturn(true).thenReturn(false);
//		when(iterator.next()).thenReturn(rship_1).thenReturn(rship_2).thenThrow(new RuntimeException());
//
//		// Act
//		//
//		boolean result = p_dao.deleteById(id);
//		
//		// Assert
//		//
//		assertTrue(result);
//		verify(mock_db, times(1)).beginTx();
//		verify(mock_tx, atLeastOnce()).success();
//		verify(mock_tx, times(1)).close();
//		verify(mock_node, times(1)).delete();
//		verify(rship_1, times(1)).delete();
//		verify(rship_2, times(1)).delete();
//	}

}
