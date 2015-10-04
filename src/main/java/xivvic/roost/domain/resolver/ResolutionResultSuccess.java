package xivvic.roost.domain.resolver;

public class ResolutionResultSuccess<T>
	implements ResolutionResult<T>
{

	private final T resolvedItem;
	
	public ResolutionResultSuccess(T t)
	{
		this.resolvedItem = t;
	}

	@Override
	public boolean wasSuccessful()
	{
		return true;
	}

	@Override
	public T getResolvedObject()
	{
		return resolvedItem;
	}

}
