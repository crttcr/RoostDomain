package xivvic.roost.dao.neo;

import org.neo4j.graphdb.GraphDatabaseService;

public abstract class DaoNeo
{
	private final GraphDatabaseService neo;
	
	protected DaoNeo(GraphDatabaseService gdb)
	{
		this.neo = gdb;
	}

	protected GraphDatabaseService db()
	{
		return neo;
	}


}
