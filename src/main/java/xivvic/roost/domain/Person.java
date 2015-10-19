package xivvic.roost.domain;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import xivvic.roost.Nullable;

/**
 * Immutable value object representing a person
 * 
 * @author reid.dev
 *
 */

@AutoValue
@JsonDeserialize(builder=AutoValue_Person.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Person 
	implements DomainEntity
{
	public static final List<Person> EMPTY_LIST =  Collections.<Person>emptyList();
	public static final String PROP_ID          = "person_id";
	public static final String PROP_FIRST_NAME  = "person_name_first";
	public static final String PROP_MIDDLE_NAME = "person_name_middle";
	public static final String PROP_LAST_NAME   = "person_name_last";
	public static final String PROP_NICKNAME    = "person_name_nickname";
	
	/**
	 * A unique ID for this person.
	 */
	@JsonProperty("id")
	public abstract String id();
	
	/** 
	 * This person's nickname.  Not required.  If provided, it
	 * will be the source of "preferredName" otherwise firstName will
	 * 
	 * @return the nickname
	 */
	@Nullable
	@JsonProperty("nickname")
	public abstract String nickname();

	/** This is the name that a person is called by, for instance "Bill" for "William"
	 * or perhaps "Slick Willy" for "William"
	 * 
	 * @return the preferred name for this person
	 */
	public final String preferredName()
	{
		String nn = nickname();
		
		if (nn != null) return nn;
		
		return firstName();
	}
	
	
	/**
	 * The person's first name
	 * @return first name of the person
	 */
	@JsonProperty("firstName")
	public abstract String firstName();
	
	/**
	 * The person's middle name
	 * @return middle name of the person
	 */
	@Nullable
	@JsonProperty("middleName")
	public abstract String middleName();
	
	/**
	 * The person's last name
	 * @return last name of the person
	 */
	@JsonProperty("lastName")
	public abstract String lastName();
	
	@AutoValue.Builder
	public abstract static class Builder
	{
		@JsonProperty("id")
		public abstract Builder id(String id);

		@JsonProperty("firstName")
		public abstract Builder firstName(String firstName);

		@JsonProperty("middleName")
		public abstract Builder middleName(String middleName);

		@JsonProperty("lastName")
		public abstract Builder lastName(String lastName);

		@JsonProperty("nickname")
		public abstract Builder nickname(String nickname);

		public abstract Person build();
	}
	
	public static Builder builder()
	{
		return new AutoValue_Person.Builder();
	}
}
