package server;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		ServerMonitor theServer = new ServerMonitor(Integer.parseInt(args[0]));
		try {
			theServer.handleRequests();
		} catch (IOException e) {
			System.out.println("Error!");
			//theServer.destroy();
			System.exit(1);
		}
	}

}
