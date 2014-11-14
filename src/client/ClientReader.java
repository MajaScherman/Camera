package client;

import java.io.InputStream;
import java.net.Socket;

public class ClientReader extends Thread {
	private ClientMonitor monitor;
	private InputStream inputstream;

	public ClientReader(ClientMonitor m, InputStream in) {
		monitor = m;
		inputstream = in;
	}

	public void Run(){
		
	}
	
}
