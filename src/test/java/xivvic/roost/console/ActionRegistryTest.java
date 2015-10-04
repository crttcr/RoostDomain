package xivvic.roost.console;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import xivvic.console.action.Action;
import xivvic.console.action.DummyAction;

public class ActionRegistryTest
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
	public void testPutNull()
	{
		Action out = ActionRegistry.put(null);
		assertNull(out);
	}

	@Test
	public void testPutNullName()
	{
		Action in = new DummyAction(null, "Dummy Description");
		Action out = ActionRegistry.put(in);
		assertNull(out);
	}

	@Test
	public void testGetNull()
	{
		Action out = ActionRegistry.get(null);
		
		assertNull(out);
	}

	@Test
	public void testDisableAction()
	{
		String name = "ANDY";
		Action   in = new DummyAction(name, "Some action", true);

		ActionRegistry.put(in);
		
		assertTrue(in.isEnabled());
		ActionRegistry.setActionEnablement(name, false);
		
		assertFalse(in.isEnabled());
	}

	@Test
	public void testEnableAction()
	{
		String name = "ANDY";
		Action   in = new DummyAction(name, "Some action");

		in.disable();
		assertFalse(in.isEnabled());

		ActionRegistry.put(in);
		ActionRegistry.setActionEnablement(name, true);
		
		assertTrue(in.isEnabled());
		
	}

	@Test
	public void testGet()
	{
		Action in = new DummyAction("Dummy", "Dummy Description");
		
		ActionRegistry.put(in);
		Action out = ActionRegistry.get(in.name());
		
		assertEquals(in, out);
	}


}
