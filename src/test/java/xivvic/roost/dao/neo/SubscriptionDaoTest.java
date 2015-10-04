package xivvic.roost.dao.neo;

import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.neo4j.graphdb.GraphDatabaseService;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionDaoTest
{

	@Mock private GraphDatabaseService mock_db;
	
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

}
