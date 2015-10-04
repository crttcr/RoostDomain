package xivvic.roost.service;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import xivvic.roost.dao.AddressDao;
import xivvic.roost.domain.Address;
import xivvic.roost.domain.DomainEntity;

public class AddressService
	implements DomainEntityContainer
{	
	private final static Logger LOG = Logger.getLogger(AddressService.class.getName()); 
	private final AddressDao dao;
	
	public AddressService(AddressDao dao)
	{
		LOG.fine(this.getClass().getSimpleName() + ": Created.");
		this.dao = dao;
	}
	
	public List<Address> list()
	{
		List<Address> addresses = dao.list();

		return addresses;
	}


	/**
	 * Looks up an Address by its ID property
	 * 
	 * @param id the ID of the Address to search for
	 * @return the Address if found, or null if not
	 */
	public Address findById(String id)
	{
		if (id == null) 
			return null;
		
		return dao.findById(id);
	}

	@Override
	public boolean apply(Consumer<DomainEntity> function)
	{
		List<Address> addresses = list();
		
		addresses.forEach(function);
		
		return true;
	}
}
