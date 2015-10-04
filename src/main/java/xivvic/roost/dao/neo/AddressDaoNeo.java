package xivvic.roost.dao.neo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

import xivvic.neotest.program.RoostNodeType;
import xivvic.roost.dao.AddressDao;
import xivvic.roost.domain.Address;
import xivvic.roost.service.AddressService;

public class AddressDaoNeo
	extends DaoNeo
	implements AddressDao
{
	private final static Logger LOG = Logger.getLogger(AddressService.class.getName()); 

	public AddressDaoNeo(GraphDatabaseService gdb)
	{
		super(gdb);
	}
	
	/**
	 * Returns null if not found.  Otherwise returns an Address
	 * 
	 */
	@Override
	public Address findById(String id)
	{
		if (id == null)
		{
			String msg = "Id parameter was null.  Abort.";
			LOG.warning(msg);
			return null;
		}
		
		if (id.length() == 0)
			return null;
		
		Label             label = RoostNodeType.ADDRESS;
		GraphDatabaseService db = db();

		try (Transaction tx = db.beginTx() )
		{
			ResourceIterator<Node> rit = db.findNodes(label);
			
			while (rit.hasNext())
			{
				Node    node = rit.next();
				String value = (String) node.getProperty(Address.PROP_ID);
				if (id.equals(value))
				{
					Address address = node2Address(node);
					rit.close();
					tx.success();
					return address;
				}
			}
			
			rit.close();
			tx.success();
			return null;
		}
	}

	@Override
	public List<Address> list()
	{
		Label                label = RoostNodeType.ADDRESS;
		GraphDatabaseService    db = db();
		List<Address>        addrs = new ArrayList<>();

		try (Transaction tx = db.beginTx() )
		{
			ResourceIterator<Node> rit = db.findNodes(label);
			
			while (rit.hasNext())
			{
				Node    node = rit.next();
				Address addr = node2Address(node);
				
				addrs.add(addr);
			}

			rit.close();
			tx.success();
		}

		return addrs;
	}



	private Address node2Address(Node node)
	{
		Address            addr = null;
		GraphDatabaseService db = db();
				
		try (Transaction tx = db.beginTx())
		{
			String    id = (String) node.getProperty(Address.PROP_ID);
			String line1 = (String) node.getProperty(Address.PROP_LINE_ONE);
			String line2 = (String) node.getProperty(Address.PROP_LINE_TWO);
			String  city = (String) node.getProperty(Address.PROP_CITY);
			String state = (String) node.getProperty(Address.PROP_STATE);
			String   zip = (String) node.getProperty(Address.PROP_ZIP);
		
			addr = Address.builder()
				.lineOne(line1)
				.lineTwo(line2)
				.city(city)
				.state(state)
				.zip(zip)
				.build();
		
			String hydrated_id = addr.id();
			
			if (! id.equals(hydrated_id))
			{
				String msg = String.format("Address ID mismatch. Hydrated [%s], computed [%s]", hydrated_id, id);
				LOG.warning(msg);
			}
			tx.success();
			return addr;
		}
	}


}
