package xivvic.roost.domain;

import java.util.function.Function;

public enum SubscriptionExpiry 
{
	NEVER,                 // Subscription doesn't expire
	AFTER_EVENT_TRIGGER,   // Expires when the event occurs
	ON_SPECIFIC_DATE,      // Date specified
	
	;
	
	public static Function<String, SubscriptionExpiry> converterFunction()
	{
		Function<String, SubscriptionExpiry> f = (s) -> 
		{ 
			if (s == null) 
				return null; 
			
			if (s.isEmpty())
				return null;
			
			return SubscriptionExpiry.valueOf(s);
		};
		
		return f;
	}
}
