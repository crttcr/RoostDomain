package xivvic.roost.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import xivvic.roost.domain.Address;
import xivvic.roost.domain.resolver.TabSeparatedStringValueProvider;
import xivvic.roost.domain.resolver.ValueProvider;

public class ValueObjectBuilderPhysicalAddressTest
{
	private static final String HEADER = String.join("\t", "id", "lineOne", "lineTwo", "city", "state", "zip") +"\n";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test(expected = NullPointerException.class)
	public void testVOBwithNullInput()
	{
		ValueObjectBuilder vob = new ValueObjectBuilder();
		List<Address> list = vob.createPhysicalAddresses(null);
		assertNull(list);
		fail("Not expected to survive the previous method call");
	}

	@Test
	public void testBuildOneObject()
	{
		String input = HEADER +
				"XAB\t1570 Elmwood Ave\tApt. 1004\tEvanston\tIL\t60201";

		ValueProvider       vp = new TabSeparatedStringValueProvider(input);
		ValueObjectBuilder vob = new ValueObjectBuilder();
		List<Address>     list = vob.createPhysicalAddresses(vp);
		
		assertNotNull(list);
		assertTrue(1 == list.size());
		
		Address pa = list.get(0);
		assertNotNull(pa);
		assertTrue("60201".equals(pa.zip()));
	}

	@Test
	public void testBuildThreeObjects()
	{
		String input = HEADER                                           +
				"ABC\t1570 Elmwood Ave\tApt. 1004\tEvanston\tIL\t60201\n" +
				"BCE\t2329 N. Leavitt St.\tUnit 3\tChicago\tIL\t\n"          +
				"CDE\t2725 Paran Valley Rd.\t\tAtlanta\tGA\t30327";
		ValueProvider vp = new TabSeparatedStringValueProvider(input);
		
		ValueObjectBuilder vob = new ValueObjectBuilder();
		List<Address> list = vob.createPhysicalAddresses(vp);
		
		assertNotNull(list);
		assertTrue(3 == list.size());
		
		Address pa = list.get(0);
		assertNotNull(pa);
		assertTrue("60201".equals(pa.zip()));
	}

}
