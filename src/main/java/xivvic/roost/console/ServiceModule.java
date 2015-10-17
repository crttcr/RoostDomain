package xivvic.roost.console;

import dagger.Module;
import dagger.Provides;
import xivvic.neotest.program.NeoDaoModule;
import xivvic.roost.app.login.LoginService;
import xivvic.roost.dao.AddressDao;
import xivvic.roost.dao.EventDao;
import xivvic.roost.dao.GroupDao;
import xivvic.roost.dao.PersonDao;
import xivvic.roost.dao.SubscriptionDao;
import xivvic.roost.dao.UserDao;
import xivvic.roost.service.AddressService;
import xivvic.roost.service.EventService;
import xivvic.roost.service.GroupService;
import xivvic.roost.service.PersonService;
import xivvic.roost.service.SubscriptionService;
import xivvic.roost.service.UserService;

@Module(includes = NeoDaoModule.class)
public class ServiceModule
{
	@Provides
	AddressService provideAddressService(AddressDao dao)
	{
		return new AddressService(dao);
	}
	
	@Provides
	EventService provideEventService(EventDao dao)
	{
		return new EventService(dao);
	}
	
	@Provides
	GroupService provideGroupService(GroupDao dao)
	{
		return new GroupService(dao);
	}
	
	@Provides
	LoginService provideLoginService(UserDao dao)
	{
		return new LoginService(dao);
	}

	@Provides
	PersonService providePersonService(PersonDao dao)
	{
		return new PersonService(dao);
	}
	
	@Provides
	SubscriptionService provideSubscriptionService(SubscriptionDao dao)
	{
		return new SubscriptionService(dao);
	}
	
	@Provides
	UserService provideUserService(UserDao dao)
	{
		return new UserService(dao);
	}
}
