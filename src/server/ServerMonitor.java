package server;

/*
 * Real-time and concurrent programming
 *
 * Minimalistic HTTP server solution.
 *
 * Package created by Patrik Persson, maintained by klas@cs.lth.se
 * Adapted for Axis cameras by Roger Henriksson 
 */

import java.net.*; // Provides ServerSocket, Socket
import java.io.*; // Provides InputStream, OutputStream

import se.lth.cs.eda040.fakecamera.*; // Provides AxisM3006V

/**
 * Itsy bitsy teeny weeny web server. Always returns an image, regardless of the
 * requested file name.
 */
public class ServerMonitor {
	// private Socket clientsocket;
	// private String request;
	// private int myPort;

	/**
	 * Writing what I think is necessary here for our version//Maja
	 */
	private Socket clientSocket;
	private InputStream is; // the is from the client, is used as the os from
							// the server
	private OutputStream os; // the os from the client, is used as the is from
								// the server
	private String request;
	private int myPort;
	private boolean movieMode;

	public synchronized void setMovieMode(boolean movieMode) {
		this.movieMode = movieMode;
		notifyAll();
	}

	public synchronized boolean getMovieMode() {
		return movieMode;
	}

	public synchronized void setPort(int myPort) {
		this.myPort = myPort;
		notifyAll();
	}

	public synchronized int getPort() {
		return myPort;
	}

	public synchronized void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
		synchStreamsAndRequest();
		notifyAll();
	}

	private synchronized void synchStreamsAndRequest() {
		try {

			is = clientSocket.getInputStream();
			os = clientSocket.getOutputStream();

			// Read the request
			request = getLine(is);

			// The request is followed by some additional header lines,
			// followed by a blank line. Those header lines are ignored.
			String header;
			boolean cont = true;
			do {
				header = getLine(is);
				cont = !(header.equals(""));
			} while (cont);

			System.out.println("HTTP request '" + request + "' received.");
			notifyAll();
		} catch (IOException e) {
			System.out.println("Caught exception " + e);
		}
	}

	public synchronized Socket getClientSocket() {
		return clientSocket;
	}

	public synchronized InputStream getInputStream() {
		return is;
	}

	public synchronized OutputStream getOutputStream() {
		return os;
	}

	/**
	 * the following methods are created by Amnup and Emnup
	 * 
	 * @return
	 */
	public synchronized String getRequest() {
		// gives the request to the writer
		return request;
	}

	public synchronized void setRequest(String newReq) {
		// sets the request for the writer
		request = newReq;
		notifyAll();
	}


}
