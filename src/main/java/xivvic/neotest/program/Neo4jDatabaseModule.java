package xivvic.neotest.program;

import javax.inject.Singleton;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static Logger LOG = LoggerFactory.getLogger(Neo4jDatabaseModule.class.getName());

	public static final String PERSISTENT = "persistent";
	public static final String  TRANSIENT = "transient";
	
	boolean called = false;
	
	@Provides
	@Singleton
//	@Named(PERSISTENT)
	public GraphDatabaseService providePersistentDb()
	{
		if (called)
		{
			LOG.error("Provide persistent DB has been called before. Should be singleton instance.");
			System.exit(-1);
		}
		else
		{
			LOG.warn("DB Construction Called.");
			called = true;
		}

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
