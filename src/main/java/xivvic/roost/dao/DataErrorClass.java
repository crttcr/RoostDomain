package xivvic.roost.dao;

/**
 * Captures the different types of data errors
 * 
 * @author reid.dev
 *
 */
public enum DataErrorClass
{
	SYNTACTIC,      // Failure to be of the correct textual form
	SEMANTIC,       // Failure to have a value with a correct meaning for the environment
}
