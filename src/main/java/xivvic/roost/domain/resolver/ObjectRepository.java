package xivvic.roost.domain.resolver;

import java.util.Collection;

public interface ObjectRepository
{

	public Object getById(String id);

	public Collection<Object> getAllObjects();

	public Object addToRepository(String id, Object item);

	public Collection<?> getByClass(Class<?> c);

}