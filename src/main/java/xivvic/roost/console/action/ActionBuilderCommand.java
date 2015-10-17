package xivvic.roost.console.action;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

import xivvic.command.Command;
import xivvic.command.CommandProcessor;
import xivvic.command.CommandStatus;
import xivvic.console.action.Action;
import xivvic.console.action.ActionBase;
import xivvic.console.action.ActionMetadata;
import xivvic.console.input.InputProcessor;
import xivvic.console.input.InputProcessorSingleInteger;
import xivvic.console.input.InputProcessorSingleString;
import xivvic.roost.service.ServiceLocator;

/**
 * Builds command actions for this application.
 * 
 * NOTE: (IMPORTANT)
 * Be sure to add this class to the build block in the base classes' static block.
 * @see ActionBuilderBase.
 * 
 * @author Reid
 *
 */
public class ActionBuilderCommand
	extends ActionBuilderBase
{
	private final static Logger LOG = Logger.getLogger(ActionBuilderCommand.class.getName());
	
	public ActionBuilderCommand()
	{
		super();
	}

	static Action buildCommandListAction()
	{
		String               name = ActionBuilder.COMMAND_LIST;
		String               desc = "Lists last n commands";
		String              usage = "l 10";
		ActionMetadata       meta = new ActionMetadata(name, desc, usage);
		InputProcessor         ip = new InputProcessorSingleInteger(10);

		Action action = new ActionBase(meta.name(), meta.desc(), true)
		{
			@Override
			protected void internal_invoke(Object param)
			{
				Map<String, Object>   map = ip.process(param);
				
				Integer count = (Integer) map.get(InputProcessorSingleInteger.KEY);
				if (count == null)
				{
					int     dv = 5;
					String msg = String.format("Using default count of %s", dv);
					LOG.fine(msg);
					count      = dv;
				}
				
				CommandProcessor   c_proc = (CommandProcessor) ServiceLocator.locator().get(ServiceLocator.COMMAND_PROCESSOR);
				List<Command>        list = c_proc.last(count);
				Consumer<Command>   print = c -> System.out.println(c);
				list.forEach(print);
			}
		};
		
		return action;
	}

	static Action buildCommandStatusAction()
	{
		String               name = ActionBuilder.COMMAND_STATUS;
		String               desc = "Shows status for a command";
		String              usage = "stat B4I8qFYDdJ3vsRwm";
		ActionMetadata       meta = new ActionMetadata(name, desc, usage);
		InputProcessor         ip = new InputProcessorSingleString(true);

		Action action = new ActionBase(meta.name(), meta.desc(), true)
		{
			@Override
			protected void internal_invoke(Object param)
			{
				Map<String, Object>   map = ip.process(param);
				if (map == null)
				{
					String msg = String.format("Status request requires an id");
					LOG.warning(msg);
					return;
				}
				
				String id = (String) map.get(InputProcessorSingleString.KEY);
				if (id == null)
				{
					String msg = String.format("Status request requires an id");
					LOG.warning(msg);
					return;
				}
				
				CommandProcessor   c_proc = (CommandProcessor) ServiceLocator.locator().get(ServiceLocator.COMMAND_PROCESSOR);
				CommandStatus      status = c_proc.getStatusForCommand(id);
				System.out.println(status);
			}
		};
		
		return action;
	}

}
