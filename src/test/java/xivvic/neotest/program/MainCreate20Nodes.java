package xivvic.neotest.program;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.AutoIndexer;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.index.ReadableIndex;

public class MainCreate20Nodes
{

	public static void main(String args[])
	{
		GraphDatabaseService graphDb = NeoUtil.acquireAndConfigureDbService();

		populateDatabase(graphDb);

		IndexManager imgr = graphDb.index();
		AutoIndexer<Node> indexer = imgr.getNodeAutoIndexer();
		try (Transaction tx = graphDb.beginTx())
		{
			ReadableIndex<Node> index = indexer.getAutoIndex();
			String name = index.getName();

			System.out.println(name);

			tx.success();
		}
	
	}

	private static void populateDatabase(GraphDatabaseService graphDb)
	{
		Relationship relation;
		Node node1;
		Node node2;

		// build 20 nodes, 2 related nodes in each iteration
		for (int i = 0; i < 10; i++)
		{
			try (Transaction tx = graphDb.beginTx())
			{
				node1 = graphDb.createNode();
				node2 = graphDb.createNode();

				// name:node-[1,2...10]-1
				node1.setProperty("name", "node-" + (i + 1) + "-1");
				// name:node-[1,2...10]-2
				node2.setProperty("name", "node-" + (i + 1) + "-2");

				relation = node1.createRelationshipTo(node2, RelTypes.RELATE_TO);

				// name: relation: [1,2,..10]
				relation.setProperty("name", "relation: " + (i + 1));

				tx.success();
			}
		}
	}

	public static enum RelTypes implements RelationshipType
	{
		RELATE_TO;
	}

}
