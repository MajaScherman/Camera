package server;

public class ServerWriter extends Thread {

	private ServerMonitor mon;

	public ServerWriter(ServerMonitor mon) {
		this.mon = mon;
	}

	public void run() {
		System.out.println("ServerWriter started");
		while (!isInterrupted()) {
			mon.write();
		}
	}
}
