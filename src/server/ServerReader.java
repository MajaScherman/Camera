package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerReader extends Thread {

	private ServerMonitor mon;
	public ServerReader(ServerMonitor mon) {
		this.mon = mon;

	}

	public void run() {
		mon.acceptClient();
		while (true) {//until terminate
			
			mon.synchStreams();
			mon.readRequest();
			mon.readHeader();
			//TODO closegrej eventuellt	
		}
		//close
		//terminate

	}
}
