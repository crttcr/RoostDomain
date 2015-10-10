package xivvic.neotest.program;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import xivvic.roost.domain.Address;
import xivvic.roost.domain.Group;
import xivvic.roost.domain.Person;
import xivvic.roost.domain.User;

public class RoostNodeTypeTest
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
	public void testGroup()
	{
		// Arrange
		//
		RoostNodeType type = RoostNodeType.GROUP;
		

		// Act
		//
		String[]     props = type.uniqueProperties();
		
		// Assert
		//
		assertNotNull(props);
		assertEquals(2, props.length);
		assertTrue(Group.PROP_ID.equals(props[0]));
		assertTrue(Group.PROP_NAME.equals(props[1]));
	}

	@Test
	public void testAddress()
	{
		// Arrange
		//
		RoostNodeType type = RoostNodeType.ADDRESS;
		

		// Act
		//
		String[]     props = type.uniqueProperties();
		
		// Assert
		//
		assertNotNull(props);
		assertEquals(1, props.length);
		assertTrue(Address.PROP_ID.equals(props[0]));
	}

	@Test
	public void testUser()
	{
		// Arrange
		//
		RoostNodeType type = RoostNodeType.USER;
		

		// Act
		//
		String[]     props = type.uniqueProperties();
		Set<String>    set = new HashSet<>();
		
		set.add(props[0]);
		set.add(props[1]);
		set.add(props[2]);
		
		// Assert
		//
		assertEquals(3, props.length);
		assertTrue(set.contains(User.PROP_ID));
		assertTrue(set.contains(User.PROP_EMAIL));
		assertTrue(set.contains(User.PROP_UNAME));
	}

	@Test
	public void testPerson()
	{
		// Arrange
		//
		RoostNodeType type = RoostNodeType.PERSON;
		

		// Act
		//
		String[]     props = type.uniqueProperties();
		
		// Assert
		//
		assertNotNull(props);
		assertEquals(1, props.length);
		assertTrue(Person.PROP_ID.equals(props[0]));
	}

//	@Test
//	public void testPerson()
//	{
//		// Arrange
//		//
//		RoostNodeType type = RoostNodeType.PERSON;
//		
//
//		// Act
//		//
//		String[]     props = type.uniqueProperties();
//		
//		// Assert
//		//
//		assertNotNull(props);
//		assertEquals(1, props.length);
//		assertTrue(PersonDao.PROP_ID.equals(props[0]));
//	}

	@Test
	public void testModifyArray()
	{
		// Arrange
		//
		RoostNodeType type = RoostNodeType.PERSON;
		
		// Act
		//
		String[] s1 = type.uniqueProperties();
		s1[0] = "MyLittlePony";
		String[] s2 = type.uniqueProperties();
		
		// Assert
		//
		assertNotNull(s2);
		assertTrue(Person.PROP_ID.equals(s2[0]));
	}

}
