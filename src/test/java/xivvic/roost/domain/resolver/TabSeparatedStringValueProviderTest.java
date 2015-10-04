package xivvic.roost.domain.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import xivvic.roost.domain.resolver.TabSeparatedStringValueProvider;
import xivvic.roost.domain.resolver.ValueProvider;

public class TabSeparatedStringValueProviderTest
{

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

	@Test
	public void testGetFieldNamesHeaderOnlyRecordSeparator()
	{
		// Assemble
		//
		String input = "A\tB\tC\nOne\tTwo\tThree";
		ValueProvider vp = new TabSeparatedStringValueProvider(input);
		
		// Act
		//
		String[] fnames = vp.getFieldNames();
		
		assertNotNull(fnames);
		assertEquals(3, fnames.length);
		assertTrue("A".equals(fnames[0]));
		assertTrue("B".equals(fnames[1]));
		assertTrue("C".equals(fnames[2]));
	}

	@Test
	public void testGetFieldNamesHeaderOnlyNoRecordSeparator()
	{
		// Assemble
		//
		String input = "A\tB\tC";
		ValueProvider vp = new TabSeparatedStringValueProvider(input);
		
		// Act
		//
		String[] fnames = vp.getFieldNames();
		
		assertNotNull(fnames);
		assertEquals(3, fnames.length);
		assertTrue("A".equals(fnames[0]));
		assertTrue("B".equals(fnames[1]));
		assertTrue("C".equals(fnames[2]));
	}

	@Test
	public void testGetValueMapSingleRowMatchingHeaders()
	{
		// Assemble
		//
		String input = "A\tB\tC\nApple\tBakery\tCherry";
		ValueProvider vp = new TabSeparatedStringValueProvider(input);
		
		// Act
		//
		Map<String, String> map = vp.getValueMap(0);
		String                a = map.get("A");
		String                b = map.get("B");
		String                c = map.get("C");
		
		// Assert
		//
		assertTrue("Apple".equals(a));
		assertTrue("Bakery".equals(b));
		assertTrue("Cherry".equals(c));
	}

	@Test
	public void testGetDataRowCountHeaderEmptyDataRow()
	{
		// Assemble
		//
		String input = "A\tB\tC\n";
		ValueProvider vp = new TabSeparatedStringValueProvider(input);
		
		// Act
		//
		int rc = vp.getDataRowCount();
		
		// Assert
		//
		assertEquals(0, rc);
	}

	@Test
	public void testGetDataRowCountHeaderOnlyNoSeparator()
	{
		// Assemble
		//
		String input = "A\tB\tC";
		ValueProvider vp = new TabSeparatedStringValueProvider(input);
		
		// Act
		//
		int rc = vp.getDataRowCount();
		
		// Assert
		//
		assertEquals(0, rc);
	}

	@Test
	public void testGetOriginalInput()
	{
		// Assemble
		//
		String input = "A\tB\tC";
		TabSeparatedStringValueProvider vp = new TabSeparatedStringValueProvider(input);
		
		// Act
		//
		String oi = vp.getOriginalInput();
		
		
		// Assert
		//
		assertTrue(input.equals(oi));
	}

}
