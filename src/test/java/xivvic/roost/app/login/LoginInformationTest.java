package xivvic.roost.app.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import xivvic.roost.app.login.LoginInformation;

public class LoginInformationTest
{
	private static final String user = "u_1";
	private static final String  key = "key.1";
	private LoginInformation li;

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
		li = LoginInformation.builder()
				.user(user)
				.sessionKey(key)
				.build();
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void builderConstruction()
	{
		assertNotNull(li);
	}

	@Test
	public void testUser()
	{
		String s = li.user();
		assertTrue(user.equals(s));
	}

	@Test
	public void testSessionKey()
	{
		String s = li.sessionKey();
		assertTrue(key.equals(s));
	}

	@Test
	public void testLoginTime()
	{
		LocalDateTime then = li.loginTime();
		LocalDateTime  now = LocalDateTime.now();
		LocalDateTime after = then.plusSeconds(1L);
		
		int compare = after.compareTo(now);
		
		// Checking to make sure it was within 1 second of the start of this test.
		// Not very scientific, but if it fails, something strange is going on.
		assertTrue(compare > 0);
	}

	@Test
	public void testLastActivity()
	{
		LocalDateTime now = LocalDateTime.now();
		li.setLastActivity(now);
		
		LocalDateTime last = li.lastActivity();
		
		assertEquals(now, last);
	}

}
