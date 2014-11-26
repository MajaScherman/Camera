package server;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestServer {
	ServerMonitor mon;
	@Before
	public void setUp() throws Exception {
		mon = new ServerMonitor(6876, 1);
	}

	@After
	public void tearDown() throws Exception {
		mon = null;
	}



	@Test
	public void testSetCloseConnection() {
		
		fail("Not yet implemented");
	}

	@Test
	public void testShouldCloseConnection() {
		fail("Not yet implemented");
	}

	@Test
	public void testCloseConnection() {
		fail("Not yet implemented");
	}

	@Test
	public void testSendPackage() {
		fail("Not yet implemented");
	}

	@Test
	public void testPackageImage() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetServerSocket() {
		fail("Not yet implemented");
	}

	@Test
	public void testAcceptClient() {
		fail("Not yet implemented");
	}

	@Test
	public void testSynchStreams() {
		fail("Not yet implemented");
	}

	@Test
	public void testReadMessage() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateValuesFromMessage() {
		fail("Not yet implemented");
	}

	@Test
	public void testReadRequest() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRequest() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetClientSocket() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetInputStream() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetOutputStream() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCameraNbr() {
		assertEquals("Wrong camera nbr",1,mon.getCameraNbr());
	
	}

	@Test
	public void testSetMovieMode() {
		
		
		fail("Not yet implemented");
	}

	@Test
	public void testGetMovieMode() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetPort() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPort() {
		assertEquals("hej",mon.getPort(), 6876);
		
		
	}

}
