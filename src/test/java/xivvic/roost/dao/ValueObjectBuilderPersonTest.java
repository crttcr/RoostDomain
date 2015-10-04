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

import xivvic.roost.dao.ValueObjectBuilder;
import xivvic.roost.domain.Person;
import xivvic.roost.domain.resolver.TabSeparatedStringValueProvider;
import xivvic.roost.domain.resolver.ValueProvider;

public class ValueObjectBuilderPersonTest
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

	@Test(expected = NullPointerException.class)
	public void testVOBwithNullInput()
	{
		ValueObjectBuilder vob = new ValueObjectBuilder();
		List<Person>      list = vob.createPeople(null);
		assertNull(list);
		fail("Not expected to survive the previous method call");
	}

	@Test
	public void testBuildOneObject()
	{
		String input = "id\tfirstName\tmiddleName\tlastName\tnickname\n" +
				"12345\tCarlton\tReid\tTurner\tReid";
		ValueProvider vp = new TabSeparatedStringValueProvider(input);
		
		
		ValueObjectBuilder vob = new ValueObjectBuilder();
		List<Person> list = vob.createPeople(vp);
		
		assertNotNull(list);
		assertTrue(1 == list.size());
		
		Person p = list.get(0);
		assertNotNull(p);
		assertTrue("Reid".equals(p.preferredName()));
	}

	@Test
	public void testBuildThreeObjects()
	{
		String input = "id\tfirstName\tmiddleName\tlastName\tnickname\n" +
				"12345\tCarlton\tReid\tTurner\tReid\n"                     +
				"23456\tRana\t\tLee\t\n"                                   +
				"34567\tDouglas\tChristopher\tTurner\tDoug";
		ValueProvider vp = new TabSeparatedStringValueProvider(input);
		
		ValueObjectBuilder vob = new ValueObjectBuilder();
		List<Person> list = vob.createPeople(vp);
		
		assertNotNull(list);
		assertTrue(3 == list.size());
		
		Person p = list.get(2);
		assertNotNull(p);
		assertTrue("Doug".equals(p.preferredName()));
	}

}
