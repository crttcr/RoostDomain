package xivvic.neotest.program;

import org.neo4j.graphdb.RelationshipType;

/**
 * IDs the type of relationships
 * 
 * @author Reid
 *
 */
public enum RoostRelType 
	implements RelationshipType
{
	EVENT_PROGENITOR,  // The person who is the reason for an event occuring

	GROUP_MEMBER,      // A person is a member of a group

	USER_GROUP  ,      // A user belongs to a group {Different type from person}
	USER_PERSON ,      // A user is related to a person (People have events, Users have subscriptions)

	PERSON_ADDRESS,    // Relationship indicates that a person has a specified address Address as their home address

	USER_SUBSCRIBE,      // (User)        --[:USER_S] -->(Subscription)
	SUBSCRIBED_EVENT,    // (Subscription)--[:S_EVENT]-->(Event)
}
