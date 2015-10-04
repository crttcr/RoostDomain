package xivvic.roost.console;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import xivvic.command.Command;
import xivvic.command.ResultProcessor;
import xivvic.event.EventBroker;
import xivvic.event.EventMetadata;
import xivvic.util.identity.RandomString;

/**
 * This class needs to do multiple things:
 * 
 * It needs to do housekeeping activities for the CommandProcessor when a command completes
 *    Take it out of the actives queue
 *    Perhaps keep a count
 *    
 * It needs to publish the completion of successful commands to the rest of the program
 *    
 *    
 * Clearly the name is wrong and perhaps the responsibilities too.
 * I don't want the CommandProcessor to be aware (tightly coupled) to the EventPublishing
 * capability because you want to be able to use it without events.
 * 
 * The way to make that happen is to have an that provided from a module higher up in the
 * stack.  However, how should housekeeping be handled because I have a single task that gets
 * invoked by the ExecutorCompletionService.  It seems that that task will need to have
 * multiple things to do in different domains.
 * 
 * DESIGN: How should this work?
 * Also:  The Cleanup task will only have Access to the CommandResult.
 *        It will likely need the Command which is only available from the CommandProcessor and
 *        so how does it get the access.  As an anonymous inner class created by the CP, it was 
 *        easy to look into the Command storage, but that doesn't work when the class is created
 *        at a higher level.
 *        
 * 
 * @author Reid
 *
 */
public class CommandResultPublisher
	implements ResultProcessor
{
	private final static Logger LOG = Logger.getLogger(CommandResultPublisher.class.getName());
	
	private final EventBroker broker;
	private Command cmd;

	CommandResultPublisher(EventBroker broker)
	{
		if (broker == null)
			throw new IllegalArgumentException("Publisher cannot publish with null broker");

		this.broker = broker;
	}

	@Override
	public void run()
	{
		if (cmd == null)
		{
			String msg = "Publisher's run() method called but Command is null. Abort.";
			LOG.warning(msg);
			return;
		}
		
		String                topic = cmd.intent();
		Map<String, Object> headers = new HashMap<>();
		
		RandomString r_str = new RandomString(12);
		String correlation = r_str.nextString();
			
		EventMetadata meta = broker.submitMessage(topic, headers, correlation, cmd.id());
		String msg = String.format("Publish result: [%s].", meta);
		System.out.println(msg);
	}

	@Override
	public void setCommand(Command cmd)
	{
		this.cmd = cmd;
	}

}
