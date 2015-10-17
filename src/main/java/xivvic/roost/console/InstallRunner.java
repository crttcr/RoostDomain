package xivvic.roost.console;

import java.util.List;

import javax.inject.Inject;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.schema.ConstraintDefinition;
import org.neo4j.graphdb.schema.Schema;

import dagger.Lazy;
import xivvic.neotest.program.RoostNodeType;
import xivvic.roost.neo.DomainSchema;
import xivvic.roost.neo.NodeSchema;
import xivvic.roost.neo.PropMeta;
import xivvic.roost.neo.PropPredicate;
import xivvic.roost.neo.SchemaManager;

public class InstallRunner
	implements Runnable
{
	private final Lazy<GraphDatabaseService> lazydb;
	
	@Inject
	public InstallRunner(Lazy<GraphDatabaseService> lazydb)
	{
		this.lazydb = lazydb;
	}

	@Override
	public void run()
	{
		System.out.print("Initializing database schema ");
		GraphDatabaseService db = lazydb.get();
		
		try(Transaction tx = db.beginTx())
		{
			Schema schema = db.schema();
			
			RoostNodeType[] types = RoostNodeType.values();
			
			for (RoostNodeType rnt : types)
			{
				// Check to see if there are already constraints for this type.
				// If so, don't recreate them, simple move to the next type.
				//
				Iterable<ConstraintDefinition> iterable = schema.getConstraints(rnt);
				if (iterable.iterator().hasNext())
					continue;
				
				DomainSchema      doms = SchemaManager.getInstance();
				NodeSchema node_schema = doms.getEntitySchema(rnt.getClass());
				List<PropMeta> lpm = node_schema.properties(PropPredicate.predicateUnique());
				
				if (lpm.size() == 0)
					continue;
				
				for (PropMeta prop_def : lpm)
				{
					String key = prop_def.key();

               schema.constraintFor(rnt).assertPropertyIsUnique(key).create();
				}
			}
			
			tx.success();
		}
		try
		{
			for (int i = 0; i < 4; i++)
			{
				Thread.sleep(150);
				System.out.print(".");
			}
		}
		catch (InterruptedException e)
		{
			System.out.print("**INTERRUPTED**");
		}

		System.out.println(" Done.");
	}

}
