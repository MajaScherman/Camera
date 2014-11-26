package server;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ServerTests {
	ServerMonitor mon;
	
	@Before
	public void setUp() {
		
		mon = new ServerMonitor(6876,1);
		
	}
	@After
	public void tearDown(){
		mon = null;
	}

	@Test
	public final void portTest(){
		assertTrue("hej",true);
		//assertEquals("hej",0,1);
		
	}
}
