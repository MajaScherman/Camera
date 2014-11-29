package client;

import java.io.IOException;
import java.io.OutputStream;

public class ClientWriter extends Thread {
	private ClientMonitor mon;
	private OutputStream os;

	public ClientWriter(ClientMonitor mon, OutputStream stream) {
		this.mon = mon;
		os = stream;
	}

	public void run() {
		while(true){
			int i;
			for(i=0;i<mon.getNbrOfSockets();i++){
			try {
				mon.sendMessageToServer(mon.getCommand(), i);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("failed to send message to server");
				e.printStackTrace();
			}
			}
		}
	}
}
