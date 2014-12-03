package server;


public class ServerReader extends Thread {

	private ServerMonitor mon;

	public ServerReader(ServerMonitor mon) {
		this.mon = mon;

	}

	public void run() {
		while (!isInterrupted()) {
			mon.establishConnection();
			while (mon.isConnected()) {
				mon.readAndRunCommand();
				
			}
		}
	}
}
