package xivvic.roost.domain;

import com.google.auto.value.AutoValue;

/**
 * Permission to perform an operation such as view a profile.
 * 
 * @author reid.dev
 *
 */
@AutoValue
public abstract class Grant 
{

	/**
	 * Who is giving permission
	 * 
	 * @return the permission giver
	 */
	public abstract Person grantor();

	/**
	 * Who is receiving permission
	 * 
	 * @return the permission receiver
	 */
	public abstract Person receiver();

	/**
	 * What operation is being granted
	 * 
	 * @return the permitted operation
	 */
	public abstract Operation operation();

	/**
	 * What resource is involved
	 * 
	 * @return the resource
	 */
	public abstract String  resource();
	
	@AutoValue.Builder
	abstract static class Builder
	{
		abstract Grant  build();
		abstract Builder grantor(Person grantor);
		abstract Builder receiver(Person receiver);
		abstract Builder operation(Operation op);
		abstract Builder resource(String resource);
	}
}
