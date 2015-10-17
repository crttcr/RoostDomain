package xivvic.neotest.program;

import javax.inject.Singleton;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;

import dagger.Module;
import dagger.Provides;

/**
 * Module which provides GraphDatabaseService objects
 * for injection.
 * 
 * @author Reid
 */
@Module
public class Neo4jDatabaseModule
{
	public static final String PERSISTENT = "persistent";
	public static final String  TRANSIENT = "transient";
	
	@Provides
	@Singleton
//	@Named(PERSISTENT)
	public GraphDatabaseService providePersistentDb()
	{
		GraphDatabaseService graphDb = new GraphDatabaseFactory()
				.newEmbeddedDatabaseBuilder("db/graphDB")
				.setConfig(GraphDatabaseSettings.node_keys_indexable, "name")
				.setConfig(GraphDatabaseSettings.relationship_keys_indexable, "name")
				.setConfig(GraphDatabaseSettings.node_auto_indexing, "true")
				.setConfig(GraphDatabaseSettings.relationship_auto_indexing, "true")
				.newGraphDatabase();
		
		// Register a shutdown hook 
		//
		Thread t = new Thread()
		{
			@Override
			public void run()
			{
				graphDb.shutdown();
			}
		};

		Runtime.getRuntime().addShutdownHook(t);
		
		return graphDb;
	}

//	@Provides
//	@Named(TRANSIENT)
//	public GraphDatabaseService provideTransientDb()
//	{
//		GraphDatabaseService db = new TestGraphDatabaseFactory().newImpermanentDatabase();
//		
//		return db;
//	}
	

}
