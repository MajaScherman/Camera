package server;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestServer {
	ServerMonitor mon;
	@Before
//	public void setUp() throws Exception {
//		mon = new ServerMonitor(6876, 1);
//	}

	@After
	public void tearDown() throws Exception {
		mon = null;
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
	public void testSetStreams() {
		fail("Not yet implemented");
	}

	@Test
	public void testReadAndUnpackCommand() {
		fail("Not yet implemented");
	}

	@Test
	public void testRunCommand() {
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

//	@Test
//	public void testGetCameraNbr() {
//		assertEquals("Wrong camera nbr",1,mon.getCameraNbr());
//	
//	}

//	@Test
//	public void testSetAndGetMovieMode() {
//		mon.setMovieMode(true);
//		assertTrue("Movie move not correct", mon.getMovieMode());
//	}


//	@Test
//	public void testSetAndGetPort() {
//		mon.setPort(6789);
//		assertEquals("Wrong port nbr",6789,mon.getPort());
//		
//		
//	}

}
