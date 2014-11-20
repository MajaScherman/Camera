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
		//while true, outer while loop which handles the connection and disconnection
		mon.acceptClient();
		
		
		while (true) {//until terminate, inner while loop which handles the actions which should be performed when
					  // there is a connection running.
					  //terminate: a commando from the client which changes a boolean which let's us leave the inner loop.
			
			mon.synchStreams();
			mon.readRequest();
			mon.readHeader();
			//TODO closegrej eventuellt	
		}
		//close
		//terminate

	}
}
