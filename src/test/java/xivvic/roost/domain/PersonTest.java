package xivvic.roost.domain;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PersonTest
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


	@Test(expected = IllegalStateException.class)
	public void testBuilderNoFirstName()
	{
		Person.builder()
			.id("a")
			.lastName("b")
			.build();
	}

	@Test(expected = IllegalStateException.class)
	public void testBuilderNoLastName()
	{
		Person.builder()
			.id("a")
			.firstName("b")
			.build();
	}

	@Test(expected = IllegalStateException.class)
	public void testBuilderNoId()
	{
		Person.builder()
			.firstName("a")
			.lastName("b")
			.build();
	}


	@Test
	public void testId()
	{
		String ID = "abc";
		
		Person p = Person.builder()
				.id(ID)
				.firstName("a")
				.lastName("b")
				.build();

		assertTrue(ID.equals(p.id()));
	}

	@Test
	public void testPreferredNameExplicitValue()
	{
		String PREFERRED_NAME = "Slick Willy";
		
		Person p = Person.builder()
				.id("id")
				.firstName("William")
				.middleName("Jefferson")
				.lastName("Clinton")
				.nickname(PREFERRED_NAME)
				.build();

		assertTrue(PREFERRED_NAME.equals(p.preferredName()));
	}


	@Test
	public void testPreferredNameDefaultValue()
	{
		String FIRST_NAME = "Wilson";
		
		Person p = Person.builder()
				.id("id")
				.firstName(FIRST_NAME)
				.middleName("b")
				.lastName("c")
				.build();

		assertTrue(FIRST_NAME.equals(p.preferredName()));
	}

	@Test
	public void testFirstName()
	{
		String FIRST_NAME = "Wilson";
		
		Person p = Person.builder()
				.id("id")
				.firstName(FIRST_NAME)
				.middleName("b")
				.lastName("c")
				.build();

		assertTrue(FIRST_NAME.equals(p.firstName()));
	}

	@Test
	public void testMiddleName()
	{
		String MIDDLE_NAME = "Zachary";
		
		Person p = Person.builder()
				.id("id")
				.firstName("a")
				.lastName("b")
				.middleName(MIDDLE_NAME)
				.build();

		assertTrue(MIDDLE_NAME.equals(p.middleName()));
	}

	@Test
	public void testMiddleNameNotProvided()
	{
		Person p = Person.builder()
				.id("id")
				.firstName("a")
				.lastName("b")
				.build();

		assertNull(p.middleName());
	}

	@Test
	public void testLastName()
	{
		String LAST_NAME = "Wilson";
		
		Person p = Person.builder()
				.id("id")
				.firstName("a")
				.middleName("b")
				.lastName(LAST_NAME)
				.build();

		assertTrue(LAST_NAME.equals(p.lastName()));
	}

}
