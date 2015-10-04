package xivvic.roost.dao;

import java.util.List;

import xivvic.roost.domain.Address;

public interface AddressDao
	extends DomainEntityDao
{
	/**
	 * Return the unique address with the matching (case sensitive) id.
	 * 
	 * @param id the id to match
	 * @return the address with the provided id, or null if none exist.
	 */
	Address findById(String id);

	/**
	 * Returns all the addresses in the system.
	 * 
	 * @return list of all addresses
	 */
	List<Address> list();

}
