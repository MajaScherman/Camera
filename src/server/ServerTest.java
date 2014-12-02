package server;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.lth.cs.eda040.fakecamera.AxisM3006V;

public class ServerTest {

	ServerMonitor mon;
	@Before
	public void setUp() throws Exception {
		AxisM3006V camera = new AxisM3006V();
		mon = new ServerMonitor(8925,"argus-1.student.lth.se",1,camera);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testServerMonitor() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsConnected() {
		assertFalse("already connected",mon.isConnected());
		//mon.establishConnection();
		//assertTrue("not connected",mon.isConnected());
	}

	@Test
	public void testGetServerSocket() {
		fail("Not yet implemented");
	}

	@Test
	public void testEstablishConnection() {
		fail("Not yet implemented");
	}

	@Test
	public void testCloseConnection() {
		fail("Not yet implemented");
	}

	@Test
	public void testReadAndRunCommand() {
		fail("Not yet implemented");
	}

	@Test
	public void testWrite() {
		fail("Not yet implemented");
	}

}
