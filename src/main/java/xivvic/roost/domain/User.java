package xivvic.roost.domain;

import java.util.Collections;
import java.util.List;

import com.google.auto.value.AutoValue;

/**
 * Immutable value object representing a user.
 * A user is related to a person, but person can exist
 * without having a user.
 * 
 * @author reid.dev
 *
 */

@AutoValue
public abstract class User 
	implements DomainEntity
{
	public static final List<User> EMPTY_LIST =  Collections.<User>emptyList();
	public static final String    PROP_ID = "user_id";
	public static final String PROP_EMAIL = "user_email";
	public static final String PROP_UNAME = "user_username";
	public static final String PROP_PHASH = "user_passhash";
	public static final String  PROP_SALT = "user_salt";
	/**
	 * Unique id for this user
	 * 
	 */
	public abstract String id();

	/**
	 * Username property for this user.  This does not
	 * have to be unique, as email is the User key.
	 * 
	 * @return user name for this user
	 */
	public abstract String username();

	/**
	 * Email address for the user, should be unique
	 * 
	 * @return the email address
	 */
	public abstract String email();

	/**
	 * Hash of the user's password
	 * 
	 * @return the hashed value of the user's password
	 */
	public abstract String passhash();
	
	/**
	 * The Group that this user belongs to.  
	 * 
	 * @return the user's group object
	 */
	public abstract Group group();
	
	/**
	 * The Person that this User is associated with
	 * 
	 * @return the user's Person object
	 */
	public abstract Person person();
	
	@AutoValue.Builder
	public abstract static class Builder
	{
		public abstract User build();
		public abstract Builder id(String id);
		public abstract Builder username(String username);
		public abstract Builder email(String email);
		public abstract Builder passhash(String passhash);
		public abstract Builder group(Group group);
		public abstract Builder person(Person person);
	}
	
	public static Builder builder()
	{
		return new AutoValue_User.Builder();
	}
}
