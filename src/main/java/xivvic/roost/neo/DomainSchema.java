package xivvic.roost.neo;

import java.util.List;

import org.neo4j.graphdb.RelationshipType;

/**
 * This interface represents any implementation that provides 
 * schema information to the application.
 * 
 * It's an interface because the first version will be hard-coded,
 * but later versions might come from processing a data file or 
 * connecting to a set of schema tables in a database.
 * @author Reid
 *
 */
public interface DomainSchema
{

	public List<Class<?>> entityTypes();

	public NodeSchema getEntitySchema(Class<?> cls);

	public EdgeSchema getEdgeSchema(RelationshipType type);

}
