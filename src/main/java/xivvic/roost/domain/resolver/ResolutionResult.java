package xivvic.roost.domain.resolver;


public interface ResolutionResult<T>
{
	boolean wasSuccessful();
	
	T getResolvedObject();
}
