package xivvic.roost.domain;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import xivvic.roost.domain.Event;
import xivvic.roost.domain.EventType;

public class EventTest
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
	public void testFactoryMethod()
	{
		String          id = "ABC312";
		EventType     type = EventType.GRADUATION;
		String        text = "Well done, poopy pants";
		LocalDate     date = LocalDate.now();
		Event            e = Event.create(id, date, null, type, text);
		LocalDate       ld = e.date();
		
		assertNotNull(e);
		assertTrue(type == e.type());
		assertTrue(text.equals(e.text()));
		assertTrue(date.equals(ld));
	}

	@Test
	public void testTodayFactoryMethod()
	{
		String          id = "ABC312";
		EventType     type = EventType.BIRTHDAY;
		String        text = "Today's your birthday";
		Event            e = Event.createForTodayWithoutTimeComponent(id, type, text);
		LocalDate       ld = e.date();
		LocalDate      now = LocalDate.now();
		
		assertNotNull(e);
		assertTrue(type == e.type());
		assertTrue(text.equals(e.text()));
		assertTrue(now.equals(ld));
	}

	@Test(expected = NullPointerException.class)
	public void testNullId()
	{
		LocalDate     today = LocalDate.now();
		LocalTime   now = LocalTime.now();
		EventType      type = EventType.BIRTHDAY;
		Event            e = Event.create(null, today, now, type, "Foo");
		
		assertNull(e);
		fail("Should not get here");
	}

	@Test(expected = NullPointerException.class)
	public void testNullType()
	{
		String           id = "ABC312";
		LocalDate     today = LocalDate.now();
		LocalTime   now = LocalTime.now();
		Event             e = Event.create(id, today, now, null, "Foo");
		
		assertNull(e);
		fail("Should not get here");
	}


}
