package client;

import java.io.IOException;
import java.io.OutputStream;

public class ClientWriter extends Thread {
	private ClientMonitor monitor;
	private OutputStream os;

	public ClientWriter(ClientMonitor mon, OutputStream stream) {
		monitor = mon;
		os = stream;
	}

	public void run(){
		while(!isInterrupted()){
			
		}
	}
}
