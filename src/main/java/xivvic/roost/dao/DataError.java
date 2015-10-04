package xivvic.roost.dao;

/**
 *  
 *  Possible problems
 * 
 * Syntactic Errors:
 * 
 *  Values that do not parse to expected field type
 *  Values that exceed expected field length
 *  
 * Semantic Errors:
 *  Missing required values 
 *  Failure to resolve references to other entities (Entity lookup failure)
 *  Attribute values that are not part of the expected set (Alignment lookup failure)
 *  Attribute values that fail constraints (e.g. negative age)
 *  Duplicate values that are expected to be unique
 *  

 * @author reid.dev
 *
 */
public interface DataError
{
	/**
	 * The textual value of the item that gave rise to this data error.
	 * 
	 * @return the text of the data item
	 */
	String sourceValue();
	
	/**
	 * Provides the class of error.
	 * 
	 * @return the DataErrorClass for this DataError
	 */
	DataErrorClass errorClass();
	

}
