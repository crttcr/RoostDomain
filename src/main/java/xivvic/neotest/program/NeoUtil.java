package xivvic.neotest.program;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;

/**
 * Utility class for Neo4j helper methods
 * 
 * @author Reid
 *
 */
public class NeoUtil
{
	private static GraphDatabaseService GDB_SVC;
	
	public static GraphDatabaseService acquireAndConfigureDbService()
	{
		if (GDB_SVC != null)
			return GDB_SVC;
		
		// GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( "db/graphDB" );
		
		GraphDatabaseService graphDb = new GraphDatabaseFactory()
			.newEmbeddedDatabaseBuilder("db/graphDB")
			.setConfig(GraphDatabaseSettings.node_keys_indexable, "name")
			.setConfig(GraphDatabaseSettings.relationship_keys_indexable, "name")
			.setConfig(GraphDatabaseSettings.node_auto_indexing, "true")
			.setConfig(GraphDatabaseSettings.relationship_auto_indexing, "true")
			.newGraphDatabase();

		registerShutdownHook(graphDb);
		
		GDB_SVC = graphDb;
		return graphDb;
	}


	private static void registerShutdownHook(final GraphDatabaseService graphDb)
	{
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				graphDb.shutdown();
			}
		});
	}

}
