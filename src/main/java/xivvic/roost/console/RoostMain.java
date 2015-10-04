package xivvic.roost.console;

import java.util.Collection;
import java.util.logging.Logger;

import org.neo4j.graphdb.GraphDatabaseService;

import xivvic.neotest.program.NeoUtil;
import xivvic.roost.app.login.LoginService;
import xivvic.roost.dao.UserDao;
import xivvic.roost.dao.neo.UserDaoNeo;
import xivvic.roost.domain.Address;
import xivvic.roost.domain.Group;
import xivvic.roost.domain.Person;
import xivvic.roost.domain.resolver.ObjectRepository;
import xivvic.roost.domain.resolver.ObjectRepositoryBase;
import xivvic.roost.service.DomainObjectLoader;
import xivvic.roost.service.ServiceLocator;

public class RoostMain
{
	private final static Logger LOG = Logger.getLogger(RoostMain.class.getName()); 

	public static void main(String[] args)
	{
		RoostMain app = new RoostMain();
		
		app.initialize();
		app.run();
		app.finish();
	}

	private void initialize()
	{
		LOG.info("Starting application initialize() method");
		ServiceLocator locator = ServiceLocator.locator();
		
		// Create Service Objects
		//
		ObjectRepository     or = new ObjectRepositoryBase();
		GraphDatabaseService db = NeoUtil.acquireAndConfigureDbService();
		UserDao            udao = new UserDaoNeo(db);
		LoginService         ls = new LoginService(udao);

		// Register Service Objects
		//
		locator.add(ServiceLocator.REPO_OBJECT, or);
		locator.add(ServiceLocator.SERVICE_LOGIN, ls);

		DomainObjectLoader loader = new DomainObjectLoader();
		loader.loadDomainObjects();
	}


	@SuppressWarnings("unchecked")
	private void run()
	{
		LOG.info("Starting application run() method");

		ObjectRepository       or = (ObjectRepository)   ServiceLocator.locator().get(ServiceLocator.REPO_OBJECT);
		Collection<Group>  groups = (Collection<Group>)  or.getByClass(Group.class);
		Collection<Person> people = (Collection<Person>) or.getByClass(Person.class);
		Collection<Address> addresses = (Collection<Address>) or.getByClass(Address.class);
		
		for (Person p : people)
		{
			System.out.println(p);
		}
		
		for (Group g : groups)
		{
			System.out.println(g);
		}

		for (Address pa : addresses)
		{
			System.out.println(pa);
		}
	}

	private void finish()
	{
		LOG.info("Starting application finish() method");
	}

}
