package xivvic.roost.dao;

import java.util.List;

import xivvic.roost.domain.Subscription;

public interface SubscriptionDao
	extends DomainEntityDao
{
	/**
	 * Return the subscription with given id.
	 * 
	 * @param name the name to match
	 * @return the subscription with the provided name, or null if none exist.
	 */
	Subscription findById(String id);

	/**
	 * Returns all the subscriptions in the system.
	 * 
	 * @return list of all subscriptions
	 */
	List<Subscription> list();

	List<Subscription> findByEventId(String event_id);

	List<Subscription> findByUserId(String user_id);
}

