package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerReader extends Thread {

	private ServerSocket serverSocket;
	private ServerMonitor mon;
	private Socket clientSocket;
	private volatile InputStream is;
	private volatile OutputStream os;

	public ServerReader(ServerSocket serverSocket, ServerMonitor mon) {
		this.serverSocket = serverSocket;
		this.mon = mon;

	}

	public void run() {
		while (true) {
			try {
				/**
				 * I believe that this can't be in the monitor, so it have to be
				 * in a thread, I hope this thread is the right one Maybe I have
				 * done a big concurrent error here?!? Or am I safe with the
				 * volatile?
				 */
				// The 'accept' method waits for a client to connect, then
				// returns a socket connected to that client.
				clientSocket = serverSocket.accept();
			} catch (IOException e) {
				System.out
						.print("Error, no ClientSocket??");
			}
			mon.setClientSocket(clientSocket);
			//our input is the clients output, right?
			os = mon.getOutputStream();
			//What do we do with the stream, do it here!
			
			
			
		}

	}
}
