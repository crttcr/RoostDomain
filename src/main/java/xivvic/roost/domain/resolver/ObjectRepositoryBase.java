package xivvic.roost.domain.resolver;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xivvic.roost.domain.Address;
import xivvic.roost.domain.ContactInformation;
import xivvic.roost.domain.Depr_Relationship;
import xivvic.roost.domain.Event;
import xivvic.roost.domain.Grant;
import xivvic.roost.domain.Group;
import xivvic.roost.domain.Person;
import xivvic.roost.domain.Subscription;

public class ObjectRepositoryBase
	implements ObjectRepository
{
	private final static Logger LOG = LoggerFactory.getLogger(ObjectRepositoryBase.class.getName()); 

	ConcurrentMap<String, Object>           objects = new ConcurrentHashMap<>();
	ConcurrentMap<Class<?>, Set<Object>> class_sets = new ConcurrentHashMap<>();
	
	/* (non-Javadoc)
	 * @see xivvic.roost.domain.resolver.ObjectRepository#getById(java.lang.String)
	 */
	@Override
	public Object getById(String id)
	{
		if (id == null)
			return null;
		
		return objects.get(id);
		
	}

	public Object getPersonById(String id)
	{
		
		Object p = (Object) getById(id);
		
		return p;
	}
	
	/* (non-Javadoc)
	 * @see xivvic.roost.domain.resolver.ObjectRepository#getAllPersonObjects()
	 */
	@Override
	public Collection<Object> getAllObjects()
	{
		Collection<Object> c = objects.values();
		return Collections.unmodifiableCollection(c);
	}
	
	/* (non-Javadoc)
	 * @see xivvic.roost.domain.resolver.ObjectRepository#addToRepository(java.lang.String, java.lang.Object)
	 */
	@Override
	public Object addToRepository(String id, Object o)
	{
		if (id == null) return null;
		if (o  == null) return null;
		
		objects.put(id, o);
		
		Class<?> c = effectiveClassForObject(o);
		Set<Object> set = class_sets.get(c);
		if (set == null)
			set = new HashSet<Object>();
		
		set.add(o);
		class_sets.put(c, set);
		
		return objects.get(id);
	}

	private Class<?> effectiveClassForObject(Object o)
	{
		assert o != null;
		
		if (o instanceof Person             ) return              Person.class;
		if (o instanceof Group              ) return               Group.class;
		if (o instanceof Event              ) return               Event.class;
		if (o instanceof Grant              ) return               Grant.class;
		if (o instanceof Depr_Relationship       ) return        Depr_Relationship.class;
		if (o instanceof Subscription       ) return        Subscription.class;
		if (o instanceof Address            ) return             Address.class;
		if (o instanceof ContactInformation ) return  ContactInformation.class;

		LOG.error("No effective class mapping for object of type: " + o.getClass().toString());
		return Object.class;
	}

	@Override
	public Collection<?> getByClass(Class<?> c)
	{
		Collection<?> result = class_sets.get(c);
		
		if (result == null)
			result = new HashSet<Class<?>>();
			
		return result;
	}

}
