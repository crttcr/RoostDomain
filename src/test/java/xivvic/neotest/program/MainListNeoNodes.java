package xivvic.neotest.program;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

public class MainListNeoNodes
{

	public static void main(String args[])
	{
		GraphDatabaseService graphDb = NeoUtil.acquireAndConfigureDbService();

		try (Transaction tx = graphDb.beginTx())
		{
		
			GlobalGraphOperations ggo = GlobalGraphOperations.at(graphDb);
			ResourceIterable<Node> nodes = ggo.getAllNodes();
			
			for (Node n : nodes)
			{
				System.out.println(n.toString());
			}
			tx.success();
		}
	
	}

//	Label label = DynamicLabel.label( "User" );
//	int idToFind = 45;
//	String nameToFind = "user" + idToFind + "@neo4j.org";
//	try ( Transaction tx = graphDb.beginTx() )
//	{
//	    try ( ResourceIterator<Node> users =
//	            graphDb.findNodes( label, "username", nameToFind ) )
//	    {
//	        ArrayList<Node> userNodes = new ArrayList<>();
//	        while ( users.hasNext() )
//	        {
//	            userNodes.add( users.next() );
//	        }
//
//	        for ( Node node : userNodes )
//	        {
//	            System.out.println( "The username of user " + idToFind + " is " + node.getProperty( "username" ) );
//	        }
//	    }
//	}	
	

	public static enum RelTypes implements RelationshipType
	{
		RELATE_TO;
	}

}
