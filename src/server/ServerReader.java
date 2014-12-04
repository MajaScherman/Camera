package server;

import java.net.SocketException;


public class ServerReader extends Thread {

	private ServerMonitor mon;

	public ServerReader(ServerMonitor mon) {
		this.mon = mon;

	}

	public void run() {
		while (!isInterrupted()) {
			mon.establishConnection();
			while (mon.isConnected()) {
				try {
					mon.readAndRunCommand();
				} catch (SocketException e) {
					System.out.println("catched the exception that we closed the connection");
					mon.establishConnection();
					
				}catch(Exception e){
					System.out.println(e + "The int at the inputstream wasn't read correctly");
					
				}
				
			}
		}
	}
}
