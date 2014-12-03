package client;

public class ClientReader extends Thread {
	private ClientMonitor mon;
	private int serverIndex;

	/**
	 * The reader reads incoming data on the client side.
	 * 
	 * @param m
	 *            The monitor for the client
	 * @param server
	 *            the number of the server that the reader handles
	 */
	public ClientReader(ClientMonitor m, int serverNbr) {
		mon = m;
		serverIndex = serverNbr;
	}

	public void run() {
		System.out
				.println("ClientReader for " + serverIndex + ", is operating");
		while (!isInterrupted()) {
			try {
				
				mon.somethingOnStream(serverIndex);
				mon.listenToServer(serverIndex);
			} catch (Exception e) {
				System.out.println("Listentoserver in monitor is mean :(" + e);
				e.printStackTrace();
			}
		}
	}
}
