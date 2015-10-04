package xivvic.roost.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import xivvic.roost.dao.EventDao;
import xivvic.roost.domain.Event;
import xivvic.roost.domain.EventType;
import xivvic.roost.service.EventService;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceTest
{
	// Class under test
	//
	private EventService service;
	
	private Event event;
	
	@Mock private EventDao mock_dao;
	

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
		service = new EventService(mock_dao);

		String         id = "Foobar";
		String       text = "Help me!";
		EventType    type = EventType.BIRTHDAY;
		LocalDate    date = LocalDate.ofEpochDay(500);
		LocalTime    time = LocalTime.ofNanoOfDay(1000 * 1000);
		            event = Event.create(id, date, time, type, text);
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testListEmpty()
	{
		// Arrange
		//
		when(mock_dao.list()).thenReturn(Event.EMPTY_LIST);
		
		// Act
		//
		List<Event> list = service.list();
		
		// Assert
		//
		assertNotNull(list);
		assertEquals(0, list.size());
	}

	@Test
	public void testListOneItem()
	{
		// Arrange
		//
		List<Event> input = new ArrayList<>();
		input.add(event);

		when(mock_dao.list()).thenReturn(input);
		
		// Act
		//
		List<Event> list = service.list();
		Event      found = list.get(0);
		
		// Assert
		//
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals(event, found);
	}



	@Test
	public void testFindByIdFound()
	{
		// Arrange
		//
		when(mock_dao.findById(notNull(String.class))).thenReturn(event);
				
		
		// Act
		//
		String   id = event.id();
		Event found = service.findById(id);
		
		// Assert
		//
		assertNotNull(found);
		assertEquals(event, found);
	}

	@Test
	public void testFindByIdNotFound()
	{
		// Arrange
		//
		when(mock_dao.findById(notNull(String.class))).thenReturn(null);
				
		
		// Act
		//
		Event found = service.findById("e1");
		
		// Assert
		//
		assertNull(found);
	}

	@Test
	public void testFindByIdNull()
	{
		// Arrange
		//
		when(mock_dao.findById(null)).thenReturn(null);
				
		
		// Act
		//
		Event found = service.findById(null);
		
		// Assert
		//
		assertNull(found);
	}


}
