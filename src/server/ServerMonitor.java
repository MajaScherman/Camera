package server;

/*
 * Real-time and concurrent programming
 *
 * Minimalistic HTTP server solution.
 *
 * Package created by Patrik Persson, maintained by klas@cs.lth.se
 * Adapted for Axis cameras by Roger Henriksson 
 */

import java.io.IOException;
import java.io.InputStream; // Provides InputStream, OutputStream
import java.io.OutputStream;
import java.net.Socket; // Provides ServerSocket, Socket
import java.nio.ByteBuffer;

import se.lth.cs.eda040.fakecamera.AxisM3006V; // Provides AxisM3006V

public class ServerMonitor {
	/**
	 * Writing what I think is necessary here for our version//Maja
	 */
	private Socket clientSocket;
	private InputStream is; // the is from the client, is used as the os from
							// the server
	private OutputStream os; // the os from the client, is used as the is from
								// the server
	private String request;
	private int port;
	private boolean movieMode;
	private AxisM3006V camera;
	private String header;
	private int cameraNbr;

	public ServerMonitor(int port, int cameraNbr) {
		this.cameraNbr = cameraNbr;
		this.port = port;
		camera = new AxisM3006V();
		camera.init();
		camera.setProxy("argus-1.student.lth.se", port);

	}

	public synchronized void setMovieMode(boolean movieMode) {
		this.movieMode = movieMode;
		notifyAll();
	}

	public synchronized boolean getMovieMode() {
		return movieMode;
	}

	public synchronized void setPort(int port) {
		this.port = port;
		notifyAll();
	}

	public synchronized int getPort() {
		return port;
	}

	public synchronized void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
		synchStreams();
		readRequest();
		readHeader();

		notifyAll();
	}

	private synchronized void synchStreams() {
		try {

			is = clientSocket.getInputStream();
			os = clientSocket.getOutputStream();
		} catch (IOException e) {
			System.out.println("Caught exception " + e);
		}
	}

	private synchronized String readHeader() {
		try {
			// The request is followed by some additional header lines,
			// followed by a blank line. Those header lines are ignored.

			boolean cont = true;
			do {
				header = getLine(is);
				cont = !(header.equals(""));
			} while (cont);

			System.out.println("HTTP request '" + request + "' received.");
			notifyAll();
			return header;

		} catch (IOException e) {
			System.out.println("Caught exception " + e);
			notifyAll();
			return null; // om det blir fel ev kommer hit om det blir tomt,
							// finns inte någon header
		}
	}

	private synchronized String readRequest() {
		// gives the request to the writer
		// Read the request
		try {
			request = getLine(is);
		} catch (IOException e) {
			System.out.println("Caught exception " + e);
			e.printStackTrace();
		}
		return request;

	}

	public synchronized String getRequest() {
		return request;
	}

	public synchronized String getHeader() {
		return header;
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

	public synchronized int getCameraNbr() {
		return cameraNbr;
	}
//Hopefully its ok to add 1 to have spaceship for cameraNbr
	public static int BUFFER_LENGTH = AxisM3006V.IMAGE_BUFFER_SIZE
			+ AxisM3006V.TIME_ARRAY_SIZE + 1;

	public synchronized byte[] packageMessage(int cameraNbr, byte[] time,
			byte[] image) {
		ByteBuffer bb = ByteBuffer.allocate(BUFFER_LENGTH);
		bb.put(time);
		bb.putInt(cameraNbr);
		bb.put(image);
		byte[] message = new byte[bb.capacity()];
		bb.get(message, 0, message.length);
		return message;

	}

	/**
	 * Read a line from InputStream 's', terminated by CRLF. The CRLF is not
	 * included in the returned string.
	 */
	private synchronized String getLine(InputStream s) throws IOException {
		boolean done = false;
		String result = "";

		while (!done) {
			int ch = s.read(); // Read
			if (ch <= 0 || ch == 10) {
				// Something < 0 means end of data (closed socket)
				// ASCII 10 (line feed) means end of line
				done = true;
			} else if (ch >= ' ') {
				result += (char) ch;
			}
		}

		return result;
	}

}
