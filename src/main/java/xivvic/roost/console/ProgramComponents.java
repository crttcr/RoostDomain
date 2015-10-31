package xivvic.roost.console;

import javax.inject.Singleton;

import org.neo4j.graphdb.GraphDatabaseService;

import dagger.Component;
import xivvic.neotest.program.Neo4jDatabaseModule;
import xivvic.neotest.program.NeoDaoModule;
import xivvic.roost.app.login.LoginService;
import xivvic.roost.service.AddressService;
import xivvic.roost.service.EventService;
import xivvic.roost.service.GroupService;
import xivvic.roost.service.PersonService;
import xivvic.roost.service.SubscriptionService;
import xivvic.roost.service.UserService;

@Singleton
@Component(modules = {ServiceModule.class, NeoDaoModule.class, Neo4jDatabaseModule.class})
public interface ProgramComponents
{

	AddressService            addressService();
	EventService                eventService();
	GroupService                groupService();
	PersonService              personService();
	SubscriptionService  subscriptionService();
	UserService                  userService();
	LoginService                loginService();
	GraphDatabaseService     databaseService();

}
