package xivvic.roost.service;

import xivvic.roost.domain.User;

public class RegistrationResult
{
	private final boolean   success;
	private final User         user;
	private final String      error;
	
	private RegistrationResult(boolean success, User user, String explanation)
	{
		this.success = success;
		this.user    = user;
		this.error   = explanation;
	}
	
	public final boolean isSuccess()
	{
		return success;
	}
	
	public User user()
	{
		if (! success)
			throw new IllegalStateException("Cannot retrieve ID for failed registration");

		return user;
	}
	
	public String error()
	{
		if (success)
			throw new IllegalStateException("Cannot retrieve error value for successful registration");

		return error;
	}
	
	public static RegistrationResult createSuccess(User user)
	{
		return new RegistrationResult(true, user, null);
	}

	public static RegistrationResult createFailure(String explanation)
	{
		return new RegistrationResult(false, null, explanation);
	}

}
