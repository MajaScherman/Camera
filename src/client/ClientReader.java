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
		System.out.println("Client readers constructor method is running lol");
	}

	public void run() {
		System.out.println("Client readers run method is running lol");
		while (!isInterrupted()) {
			try {
				System.out.println("precis här faktiskt");
				monitor.listenToServer(serverIndex);
			} catch (Exception e) {
				System.out.println("Listentoserver in monitor is mean :(" + e);
				e.printStackTrace();
			}
		}
	}
}
