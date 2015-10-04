package xivvic.roost.domain;

import java.util.Collections;
import java.util.List;

import com.google.auto.value.AutoValue;
/**
 * Immutable value type representing a group of individuals
 * 
 * @author reid.dev
 *
 */

@AutoValue
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
	public abstract String id();
	
	/**
	 * Name of the group
	 * 
	 * @return the group's name
	 */
	public abstract String name();
	
	@AutoValue.Builder
	public abstract static class Builder
	{
		public abstract Group   build();
		public abstract Builder id(String id);
		public abstract Builder name(String name);
	}
	
	public static Builder builder()
	{
		return new AutoValue_Group.Builder();
	}
}
