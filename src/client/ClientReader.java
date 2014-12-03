package client;

public class ClientReader extends Thread {
	private ClientMonitor monitor;
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
		monitor = m;
		serverIndex = serverNbr;
	}

	public void run() {
		System.out.println("ClientReader for "+serverIndex+", is operating");
		while (!isInterrupted()) {
			try {
				monitor.listenToServer(serverIndex);
			} catch (Exception e) {
				System.out.println("Listentoserver in monitor is mean :(" + e);
				e.printStackTrace();
			}
		}
	}
}
