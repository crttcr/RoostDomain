package xivvic.roost.domain.resolver;


public class EntityResolver<T>
{
	private ObjectRepository object_repo;
	
	public EntityResolver(ObjectRepository repo)
	{
		this.object_repo = repo;
	}
	
	@SuppressWarnings("unchecked")
	public ResolutionResult<T> resolveEntityById(String id)
	{
		T object =  (T) object_repo.getById(id);
		
		if (object == null)
			return new ResolutionResultFailure<T>();
		
		return new ResolutionResultSuccess<T>(object);
			
	}

}
