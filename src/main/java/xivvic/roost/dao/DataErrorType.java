package xivvic.roost.dao;

/**
 * Captures the different types of data errors
 * 
 * @author reid.dev
 *
 */
public enum DataErrorType
{
	// Syntactic
	//
	PARSE_FAIL,        // Could not covert text to the expected field type
	LENGTH_FAIL,       // Source value too long or not long enough
	
	// Semantic
	//
	REQUIRED_VALUE_MISSING,   // Xyz
	ENTITY_RESOLUTION_FAIL,   // xx
	ALIGNMENT_FAIL,           // xx
	CONSTRAINT_FAIL,           // xx
	DUPLICATE_VALUE,           // xx
	
}
