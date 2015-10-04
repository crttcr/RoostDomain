package xivvic.roost.domain;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import xivvic.roost.domain.Group;

public class GroupTest
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
	public void testBuilderNameNotProvided()
	{
		Group.builder()
			.id("a")
			.build();
	}

	@Test(expected = IllegalStateException.class)
	public void testBuilderIdNotProvided()
	{
		Group.builder()
			.name("a")
			.build();
	}

	@Test(expected = IllegalStateException.class)
	public void testBuilderNullId()
	{
		Group.builder()
			.id(null)
			.name("b")
			.build();
	}

	@Test
	public void testId()
	{
		String ID = "abc";
		
		Group g = Group.builder()
				.id(ID)
				.name("a")
				.build();

		assertTrue(ID.equals(g.id()));
	}

}
