package xivvic.roost.domain;

import com.google.auto.value.AutoValue;

/**
 * Immutable value type describing a relationship between two people.
 * Is directional.
 * 
 * @author reid.dev
 *
 */
@AutoValue
public abstract class Depr_Relationship 
{

	/**
	 * The person originating this relationship.  
	 * Origination could be read for a ''RelationshipType.PARENT'' 
	 * as **Adolphus is the parent of Bitsy** where Adolphus is the "from" person.
	 * 
	 * @return the left side, or x, of the relationship, x is the R of y
	 */
	public abstract Person from();

	/**
	 * The person being related to or destination of the relationship
	 * Destination could be read for a ''RelationshipType.PARENT'' 
	 * as **Adolphus is the parent of Bitsy** where Bitsy is the "to" person.
	 * 
	 * @return the left side, or x, of the relationship, x is the R of y
	 */
	public abstract Person to();
	
	
	/**
	 * Categorizes the type of this relationship.
	 * 
	 * @return the relationship type
	 */
	public abstract Depr_RelationshipType type();
}
