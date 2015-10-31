package xivvic.roost.domain;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
@JsonDeserialize(builder=AutoValue_User.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
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
	@JsonProperty("id")
	public abstract String id();

	/**
	 * Username property for this user.  This does not
	 * have to be unique, as email is the User key.
	 * 
	 * @return user name for this user
	 */
	@JsonProperty("username")
	public abstract String username();

	/**
	 * Email address for the user, should be unique
	 * 
	 * @return the email address
	 */
	@JsonProperty("email")
	public abstract String email();

	/**
	 * Hash of the user's password
	 * 
	 * @return the hashed value of the user's password
	 */
	@JsonProperty("passhash")
	public abstract String passhash();
	
	/**
	 * The Group that this user belongs to.  
	 * 
	 * @return the user's group object
	 */
	@JsonProperty("group")
	public abstract Group group();
	
	/**
	 * The Person that this User is associated with
	 * 
	 * @return the user's Person object
	 */
	@JsonProperty("person")
	public abstract Person person();
	
	@AutoValue.Builder
	public abstract static class Builder
	{
		@JsonProperty("id")
		public abstract Builder id(String id);

		@JsonProperty("username")
		public abstract Builder username(String username);

		@JsonProperty("email")
		public abstract Builder email(String email);

		@JsonProperty("passhash")
		public abstract Builder passhash(String passhash);

		@JsonProperty("group")
		public abstract Builder group(Group group);

		@JsonProperty("person")
		public abstract Builder person(Person person);

		public abstract User build();
	}
	
	public static Builder builder()
	{
		return new AutoValue_User.Builder();
	}
}
