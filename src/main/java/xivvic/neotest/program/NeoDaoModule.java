package xivvic.neotest.program;

import org.neo4j.graphdb.GraphDatabaseService;

import dagger.Module;
import dagger.Provides;
import xivvic.roost.dao.AddressDao;
import xivvic.roost.dao.EventDao;
import xivvic.roost.dao.GroupDao;
import xivvic.roost.dao.PersonDao;
import xivvic.roost.dao.SubscriptionDao;
import xivvic.roost.dao.UserDao;
import xivvic.roost.dao.neo.AddressDaoNeo;
import xivvic.roost.dao.neo.EventDaoNeo;
import xivvic.roost.dao.neo.GroupDaoNeo;
import xivvic.roost.dao.neo.PersonDaoNeo;
import xivvic.roost.dao.neo.SubscriptionDaoNeo;
import xivvic.roost.dao.neo.UserDaoNeo;

/**
 * Module which provides Neo4j DAO objects
 * for injection.
 * 
 * @author Reid
 */
@Module(includes=Neo4jDatabaseModule.class)
public class NeoDaoModule
{
	@Provides
	public AddressDao provideAddressDao(GraphDatabaseService db)
	{
		AddressDao dao = new AddressDaoNeo(db);
		
		return dao;
	}
	@Provides
	public GroupDao provideGroupDao(GraphDatabaseService db)
	{
		GroupDao dao = new GroupDaoNeo(db);
		
		return dao;
	}
	@Provides
	public PersonDao providePersonDao(GraphDatabaseService db)
	{
		PersonDao dao = new PersonDaoNeo(db);
		
		return dao;
	}
	@Provides
	public EventDao provideEventDao(GraphDatabaseService db)
	{
		EventDao dao = new EventDaoNeo(db);
		
		return dao;
	}
	@Provides
	public SubscriptionDao provideSubscriptionDao(GraphDatabaseService db)
	{
		SubscriptionDao dao = new SubscriptionDaoNeo(db);
		
		return dao;
	}
	@Provides
	public UserDao provideUserDao(GraphDatabaseService db)
	{
		UserDao dao = new UserDaoNeo(db);
		
		return dao;
	}
}
