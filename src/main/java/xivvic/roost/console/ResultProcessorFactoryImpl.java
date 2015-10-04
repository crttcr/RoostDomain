package xivvic.roost.console;

import xivvic.command.ResultProcessor;
import xivvic.command.ResultProcessorFactory;
import xivvic.event.EventBroker;
import xivvic.event.SubscriptionManager;

public class ResultProcessorFactoryImpl
		implements ResultProcessorFactory
{

	@Override
	public ResultProcessor create()
	{
		SubscriptionManager  sub_man = new SubscriptionManager();
		EventBroker        ev_broker = new EventBroker(sub_man);
		CommandResultPublisher   crp = new CommandResultPublisher(ev_broker);
		
		return crp;
	}

}
