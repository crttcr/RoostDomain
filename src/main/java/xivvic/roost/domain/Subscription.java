package xivvic.roost.domain;

import java.util.Collections;
import java.util.List;

import com.google.auto.value.AutoValue;

/**
 * Immutable value object representing 
 * registration of interest in a particular event
 * 
 * @author reid.dev
 */
	
@AutoValue
public abstract class Subscription 
	implements DomainEntity
{
	public static final List<Subscription> EMPTY_LIST = Collections.<Subscription>emptyList();
	public static final String        PROP_ID = "subs_id";
	public static final String    PROP_EXPIRY = "subs_expiry";

	/**
	 * Unique id for this subscription
	 * 
	 * @return
	 */
	public abstract String id();
	
	public abstract SubscriptionExpiry expiry();

	@AutoValue.Builder
	public abstract static class Builder
	{
		public abstract Subscription build();
		public abstract Builder id(String id);
		public abstract Builder expiry(SubscriptionExpiry expiry);
	}
	
	public static Builder builder()
	{
		return new AutoValue_Subscription.Builder();
	}
}
