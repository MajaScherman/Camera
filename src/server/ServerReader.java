package server;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerReader extends Thread {

	private ServerSocket serverSocket;

	public ServerReader(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;

	}

	public void run() {
		try {
			// The 'accept' method waits for a client to connect, then
			// returns a socket connected to that client.
			Socket clientSocket = serverSocket.accept();

		} catch (Exception e) {

		}
	}
}
