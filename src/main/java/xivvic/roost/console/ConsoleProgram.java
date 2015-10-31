
package xivvic.roost.console;


import java.io.PrintStream;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import console.menu.BatchAction;
import console.menu.MenuManager;
import xivvic.command.CommandHandlerFactory;
import xivvic.command.CommandProcessor;
import xivvic.command.CommandProcessorImpl;
import xivvic.command.ResultProcessorFactory;
import xivvic.console.ConsoleApp;
import xivvic.console.action.Action;
import xivvic.console.action.ActionBase;
import xivvic.neotest.program.NeoUtil;
import xivvic.roost.console.action.ActionBuilder;
import xivvic.roost.console.action.ActionBuilderBase;
import xivvic.roost.domain.resolver.ObjectRepository;
import xivvic.roost.domain.resolver.ObjectRepositoryBase;
import xivvic.roost.neo.task.Handlers;
import xivvic.roost.neo.task.NeoCommandHandlerFactory;
import xivvic.roost.service.ServiceLocator;


/** 
 * ExampleApp demonstrates an application using the console menu
 * infrastructure.
 */

public class ConsoleProgram
{
   private static final Logger logger = LoggerFactory.getLogger(ConsoleProgram.class.getName());
	/**
	 * This controls where all menu and action output will go.
	 */
	
	private static PrintStream out = System.out;

	public ConsoleProgram()
	{
		logger.debug("Constructing program object");
	}

	
	public static void main(String[] args)
	{
		logger.debug("Starting main() function");
		ConsoleProgram   consoleProgram = new ConsoleProgram();
		Action       init = consoleProgram.buildInit();
		BatchAction batch = consoleProgram.buildBatch();
		Action       menu = consoleProgram.buildPostMenuAction();
		Action       done = consoleProgram.buildCompleteAction();

		ConsoleApp a = new ConsoleApp();

		a.setPrintStream(out);
		a.registerInitAction(     init);
		a.registerBatch(          batch);
		a.registerPostMenuAction( menu);
		a.registerCompleteAction( done);

		a.doLifecycle();
	}

	
	private Action buildInit()
	{
		String name = "app.initialization";
		String desc = "Action to initialize the application";
		
		Action init = new ActionBase(name, desc, true)
		{
			@Override
			protected void internal_invoke(Object param)
			{
				// Services
				//
				initializeServices();
				
				// Actions
				//
				ActionBuilderBase.buildAndRegisterActions();

				// Menu
				//
				MenuManager manager = MenuManager.getInstance();
				MenuBuilder.buildMenuSystem(manager);
				MenuBuilder.addHelp(manager);
			}
			
		};
		
		return init;
	}

	protected void initializeServices()
	{
		System.out.println("Initializing application services.");
		ServiceLocator locator = ServiceLocator.locator();
		
		// Create Service Objects
		//
		ObjectRepository    or = new ObjectRepositoryBase();

//		// Here's where we tie in the SPI implementation.  The domain services take an interface
//		// implemented by our back end storage provider, in this case Neo.
//		//
		GraphDatabaseService gdb = NeoUtil.acquireAndConfigureDbService();
//
//		AddressDao         adao = new AddressDaoNeo(gdb);
//		AddressService  address = new AddressService(adao);
//
//		EventDao           edao = new EventDaoNeo(gdb);
//		EventService     event  = new EventService(edao);
//
//		GroupDao           gdao = new GroupDaoNeo(gdb);
//		GroupService      group = new GroupService(gdao);
//		
//		PersonDao          pdao = new PersonDaoNeo(gdb);
//		PersonService    person = new PersonService(pdao);
//
//		SubscriptionDao      sdao = new SubscriptionDaoNeo(gdb);
//		SubscriptionService  subs = new SubscriptionService(sdao);
//
//		UserDao              udao = new UserDaoNeo(gdb);
//		UserService          user = new UserService(udao);
//		LoginService           ls = new LoginService(udao);

		// Deal with commands
		//
		Map<String, Handlers>  h_map = ActionBuilder.getIntentMap();
		CommandHandlerFactory    chf = new NeoCommandHandlerFactory(gdb, h_map);
		ResultProcessorFactory   rpf = new ResultProcessorFactoryImpl();
		CommandProcessor       cproc = new CommandProcessorImpl(chf, rpf);
//		public CommandProcessorImpl(CommandHandlerFactory ch_factory, EventPublisher publisher, ResultProcessorFactory rpf)
		

		// Register Service Objects
		//
		locator.add(ServiceLocator.REPO_OBJECT,               or);
//		locator.add(ServiceLocator.SERVICE_ADDRESS,      address);
//		locator.add(ServiceLocator.SERVICE_EVENT,          event);
//		locator.add(ServiceLocator.SERVICE_GROUP,          group);
//		locator.add(ServiceLocator.SERVICE_LOGIN,             ls);
//		locator.add(ServiceLocator.SERVICE_PERSON,        person);
//		locator.add(ServiceLocator.SERVICE_SUBSCRIPTION,    subs);
//		locator.add(ServiceLocator.SERVICE_USER,            user);
		locator.add(ServiceLocator.COMMAND_PROCESSOR,      cproc);
//
//		DomainObjectLoader loader = new DomainObjectLoader();
//		loader.loadDomainObjects();
	}


	/** 
	 * This method builds an action that can be invoked to perform
	 * batch execution.  The commands to perform are provided in the
	 * constructor as an array of strings, and the invoke method is called
	 * on each of them in turn.
	 * 
	 * @return An action
	 */
	private BatchAction buildBatch()
	{
		String[] commands = { "l reid abc", }; 
		BatchAction    ba = new BatchAction(commands)
		{
			@Override
			public void internal_invoke(Object input)
			{
				out.println("Batch input: " + input);
			}
		};
		
		return ba;
	}

	private Action buildPostMenuAction()
	{
		return null;
	}

	private Action buildCompleteAction()
	{
		return null;
	}
}

