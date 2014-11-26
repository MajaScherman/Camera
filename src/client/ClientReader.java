package client;

public class ClientReader extends Thread {
	private ClientMonitor monitor;
	private int serverNumber;

	/**
	 * The reader reads incoming data on the client side.
	 * @param m The monitor for the client
	 * @param server the number of the server that the reader handles
	 */
	public ClientReader(ClientMonitor m, int serverNbr) {
		monitor = m;
		serverNumber = serverNbr;
	}

	public void Run() {
		while (!isInterrupted()) {
		try {
			monitor.listenToServer(serverNumber);
		} catch (Exception e) {
			System.out.println("Listentoserver in monitor is mean :(");
			e.printStackTrace();
		}
		}	
	}
}
