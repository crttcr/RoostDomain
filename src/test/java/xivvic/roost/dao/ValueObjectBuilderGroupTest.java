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
import xivvic.roost.domain.Group;
import xivvic.roost.domain.Person;
import xivvic.roost.domain.resolver.EntityResolver;
import xivvic.roost.domain.resolver.ObjectRepository;
import xivvic.roost.domain.resolver.ObjectRepositoryBase;
import xivvic.roost.domain.resolver.TabSeparatedStringValueProvider;
import xivvic.roost.domain.resolver.ValueProvider;


public class ValueObjectBuilderGroupTest
{
	private ValueObjectBuilder vob;
	private EntityResolver<Person> er;

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
		Person a = Person.builder().id("Reid").firstName("Reid").lastName("Turner").build();
		Person b = Person.builder().id("Doug").firstName("Doug").lastName("Turner").build();
		Person c = Person.builder().id("Ryan").firstName("Ryan").lastName("Turner").build();
		Person d = Person.builder().id("Dave").firstName("Dave").lastName("Turner").build();
		
		ObjectRepository or = new ObjectRepositoryBase();
		
		or.addToRepository(a.id(), a);
		or.addToRepository(b.id(), b);
		or.addToRepository(c.id(), c);
		or.addToRepository(d.id(), d);
		
		er  = new EntityResolver<Person>(or);
		vob = new ValueObjectBuilder();
		
	}

	@After
	public void tearDown() throws Exception
	{
		vob = null;
	}

	@Test(expected = NullPointerException.class)
	public void testVOBwithNullInput()
	{
		List<Group> list = vob.createGroups(null, er);
		assertNull(list);
		fail("Not expected to survive the previous method call");
	}

	@Test
	public void testBuildOneObject()
	{
		// Arrange
		//
		String input = "id\tname\tmembers\n"            +
				"XYZ\tA Test Group\tReid|Ryan|Doug"     ;
		ValueProvider vp = new TabSeparatedStringValueProvider(input);
		
		// Act
		//
		List<Group> list = vob.createGroups(vp, er);
		
		// Assert
		//
		assertNotNull(list);
		assertTrue(1 == list.size());
		
		Group e = list.get(0);
		assertNotNull(e);
	}

}