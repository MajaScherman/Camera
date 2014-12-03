package client;

import java.io.IOException;
import java.io.OutputStream;

public class ClientWriter extends Thread {
	private ClientMonitor mon;

	public ClientWriter(ClientMonitor mon) {
		this.mon = mon;
	}

	public void run() {
		System.out.println("ClientWriter operating");
		while (!isInterrupted()) {
			try {
				mon.sendMessageToServer();
			} catch (IOException e) {
				System.out.println(e);
				e.printStackTrace();
			}
		}
	}
}
