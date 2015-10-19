package xivvic.roost.domain;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

/**
 * Immutable value object representing 
 * registration of interest in a particular event
 * 
 * @author reid.dev
 */
	
@AutoValue
@JsonDeserialize(builder=AutoValue_Subscription.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
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
	@JsonProperty("id")
	public abstract String id();
	
	@JsonProperty("expiry")
	public abstract SubscriptionExpiry expiry();

	@AutoValue.Builder
	public abstract static class Builder
	{
		@JsonProperty("id")
		public abstract Builder id(String id);

		@JsonProperty("expiry")
		public abstract Builder expiry(SubscriptionExpiry expiry);

		public abstract Subscription build();
	}
	
	public static Builder builder()
	{
		return new AutoValue_Subscription.Builder();
	}
}
