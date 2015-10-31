package xivvic.roost.service;

import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xivvic.roost.dao.SubscriptionDao;
import xivvic.roost.domain.DomainEntity;
import xivvic.roost.domain.Subscription;

public class SubscriptionService 
	implements DomainEntityContainer
{
	private final static Logger LOG = LoggerFactory.getLogger(SubscriptionService.class.getName());
	private final SubscriptionDao dao;
	
	public SubscriptionService(SubscriptionDao dao)
	{
		LOG.info(this.getClass().getSimpleName() + ": Created.");
		this.dao = dao;
	}
	
	public List<Subscription> list()
	{
		List<Subscription> subs = dao.list();

		return subs;
	}

	/**
	 * Looks up a subscription by the ID property
	 * 
	 * @param subscription the ID of the subscription to search for
	 * @return the subscription if found, or null if not
	 */
	public Subscription findById(String subscription_id)
	{
		if (subscription_id == null) 
			return null;
		
		return dao.findById(subscription_id);
	}


	@Override
	public boolean apply(Consumer<DomainEntity> consumer)
	{
		List<Subscription> subs = dao.list();
		
		subs.forEach(consumer);
		return true;
	}


	public List<Subscription> subscriptionsForEvent(String event_id)
	{
		if (event_id == null)
			return Subscription.EMPTY_LIST;
		
		
		return dao.findByEventId(event_id);
	}

	public List<Subscription> subscriptionsForUser(String user_id)
	{
		if (user_id == null)
			return Subscription.EMPTY_LIST;
		
		return dao.findByUserId(user_id);
	}

}
