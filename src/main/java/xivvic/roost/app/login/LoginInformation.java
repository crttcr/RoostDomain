package xivvic.roost.app.login;

import java.time.LocalDateTime;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class LoginInformation
{
	
	private LocalDateTime last;
	public LoginInformation()
	{
		last = LocalDateTime.now();
	}
	
	public abstract String user();

	public abstract String sessionKey();

	public abstract LocalDateTime loginTime();

	public LocalDateTime lastActivity()
	{
		return last;
	}
	
	public void  setLastActivity(LocalDateTime time)
	{
		last = time;
	}
	
	public static Builder builder()
	{
		Builder builder = new AutoValue_LoginInformation.Builder();
		
		LocalDateTime now = LocalDateTime.now();
		builder.loginTime(now);
		
		return builder;
	}
	

	@AutoValue.Builder
	public abstract static class Builder
	{
		public abstract LoginInformation build();
		public abstract Builder user(String user_name);
		public abstract Builder sessionKey(String key);
		public abstract Builder loginTime(LocalDateTime time);
	}

}
