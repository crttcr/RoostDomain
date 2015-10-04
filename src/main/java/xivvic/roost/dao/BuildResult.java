package xivvic.roost.dao;

import java.util.Map;

/**
 * Encapsulates the result of attempting to build an object from data.
 * 
 *  
 * 
 * @author reid.dev
 *
 * @param <T>
 */
public interface BuildResult<T>
{
	boolean wasSuccessful();
	
	Map<Field, DataError> getErrorMap();
	Map<Field, String>    getInputValues();
	T getResult();
}
