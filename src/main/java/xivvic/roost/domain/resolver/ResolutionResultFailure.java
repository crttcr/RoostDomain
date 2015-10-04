package xivvic.roost.domain.resolver;

public class ResolutionResultFailure<T>
	implements ResolutionResult<T>
{
	public ResolutionResultFailure()
	{
	}

	@Override
	public boolean wasSuccessful()
	{
		return false;
	}

	@Override
	public T getResolvedObject()
	{
		return null;
	}

}
