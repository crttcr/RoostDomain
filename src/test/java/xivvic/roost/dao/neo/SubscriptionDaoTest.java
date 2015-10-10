package xivvic.roost.dao.neo;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
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

import org.junit.AfterClass;
import org.junit.Before;
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
import xivvic.roost.domain.Subscription;
import xivvic.roost.domain.SubscriptionExpiry;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionDaoTest
{
	// The DAO under test
	//
	private SubscriptionDaoNeo s_dao;

	private final Label  label = RoostNodeType.SUBSCRIPTION;

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
	
	
	@Before
	public void setUp() throws Exception
	{
		s_dao   = new SubscriptionDaoNeo(mock_db);
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
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
		Subscription subs = s_dao.findById(id);
		
		// Assert
		//
		assertThat(subs, is(nullValue()));
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
		String id = "ABCDEF";
		SubscriptionExpiry  expiry = SubscriptionExpiry.NEVER;
		Subscription expected = Subscription.builder().id(id).expiry(expiry).build();
		
		when(mock_node.getProperty(eq(Subscription.PROP_ID    ))).thenReturn(expected.id());
		when(mock_node.getProperty(eq(Subscription.PROP_EXPIRY))).thenReturn(expected.expiry().toString());
		
		when(mock_db.beginTx()).thenReturn(mock_tx);
		when(mock_db.findNode(eq(label), eq(Subscription.PROP_ID), eq(expected.id()))).thenReturn(mock_node);
		
		// Act
		//
		Subscription subs = s_dao.findById(id);
		
		// Assert
		//
		assertNotNull(subs);
		assertTrue(expected.equals(subs));
		verify(mock_db, atLeastOnce()).beginTx();
		verify(mock_db, times(1)).findNode(any(), any(), any());
		verify(mock_tx, atLeastOnce()).success();
		verify(mock_tx, times(1)).close();
	}

}
