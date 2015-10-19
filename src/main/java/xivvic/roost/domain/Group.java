package xivvic.roost.domain;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
/**
 * Immutable value type representing a group of individuals
 * 
 * @author reid.dev
 *
 */

@AutoValue
@JsonDeserialize(builder=AutoValue_Group.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Group 
	implements DomainEntity
{
	public static final List<Group> EMPTY_LIST =  Collections.<Group>emptyList();
	public static final String  PROP_ID   = "group_id";
	public static final String  PROP_NAME = "group_name";
	/**
	 * Unique id for each group
	 * 
	 * @return the group's id
	 */
	@JsonProperty("id")
	public abstract String id();
	
	/**
	 * Name of the group
	 * 
	 * @return the group's name
	 */
	@JsonProperty("name")
	public abstract String name();
	
	@AutoValue.Builder
	public abstract static class Builder
	{
		@JsonProperty("id")
		public abstract Builder id(String id);

		@JsonProperty("name")
		public abstract Builder name(String name);

		public abstract Group   build();
	}
	
	public static Builder builder()
	{
		return new AutoValue_Group.Builder();
	}
}
