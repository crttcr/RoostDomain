package xivvic.neotest.program;

import org.neo4j.graphdb.Label;

import xivvic.roost.domain.Address;
import xivvic.roost.domain.Event;
import xivvic.roost.domain.Group;
import xivvic.roost.domain.Person;
import xivvic.roost.domain.Subscription;
import xivvic.roost.domain.User;

public enum RoostNodeType 
	implements Label
{

	// NOTE:  
	// 
	// When adding new node types, be sure to include any unique
	// properties. These will become enforced by the database using
	// Neo4J's schema.
	//
	
	ADDRESS(          Address.PROP_ID),
	EVENT(              Event.PROP_ID),
	GROUP(              Group.PROP_ID ,  Group.PROP_NAME),
	PERSON(            Person.PROP_ID),
	SUBSCRIPTION(Subscription.PROP_ID),
	USER(                User.PROP_ID,   User.PROP_EMAIL, User.PROP_UNAME),
	;

	private String[] unique_properties;
	
	private RoostNodeType(String ... unique)
	{
		this.unique_properties = unique;
	}
	
	public String[] uniqueProperties()
	{
		return unique_properties.clone();
	}

}
