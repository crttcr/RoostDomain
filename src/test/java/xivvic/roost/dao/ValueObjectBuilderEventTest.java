package xivvic.roost.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import xivvic.roost.domain.Event;
import xivvic.roost.domain.EventType;
import xivvic.roost.domain.resolver.TabSeparatedStringValueProvider;
import xivvic.roost.domain.resolver.ValueProvider;


public class ValueObjectBuilderEventTest
{
	private static final String HEADER = String.join("\t", Event.PROP_ID, Event.PROP_DATE, Event.PROP_TYPE, Event.PROP_TEXT) + "\n";

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
		List<Event>       list = vob.createEvents(null);
		assertNull(list);
		fail("Not expected to survive the previous method call");
	}

	@Test
	public void testBuildOneObject()
	{
		String input = HEADER + "XXA\t2015-06-13\tBirthday\tBab's Birthday";
		
		ValueProvider       vp = new TabSeparatedStringValueProvider(input);
		ValueObjectBuilder vob = new ValueObjectBuilder();
		List<Event>       list = vob.createEvents(vp);
		
		assertNotNull(list);
		assertTrue(1 == list.size());
		
		Event e = list.get(0);
		assertNotNull(e);
		
		LocalDate ld = e.date();
		
		assertNotNull(ld);
//		assertEquals(0, ld.getHour());
//		assertEquals(1, ld.getMinute());
		assertEquals(2015, ld.getYear());
		assertEquals(Month.JUNE, ld.getMonth());
		assertEquals(13, ld.getDayOfMonth());
		
		assertNotNull(e.id());
		assertEquals(EventType.BIRTHDAY, e.type());
		assertTrue("Bab's Birthday".equals(e.text()));
	}

	@Test
	public void testBuildThreeObjects()
	{
		String input = 
				HEADER                                                      +
				"XAA\t2015-06-13\tGraduation\tGrace's Graduation\n"         +
				"XAB\t1971-04-28\tBirthday\tRana's Birthday\n"            +
				"XAC\t2015-06-13\tBirthday\tBab's Birthday"               ;
		
		ValueProvider       vp = new TabSeparatedStringValueProvider(input);
		ValueObjectBuilder vob = new ValueObjectBuilder();
		List<Event>       list = vob.createEvents(vp);
		
		assertNotNull(list);
		assertTrue(3 == list.size());
		
		Event e = list.get(2);
		assertNotNull(e);
	}

}
