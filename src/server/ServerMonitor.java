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
import java.net.ServerSocket;
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
	//private String header;
	private int cameraNbr;
	private ServerSocket serverSocket;
	private boolean closed;
	private byte[] header;
	private int command;
	

	// Hopefully its ok to add 3*4 for our spaceship
	public static int BUFFER_LENGTH = AxisM3006V.IMAGE_BUFFER_SIZE
			+ AxisM3006V.TIME_ARRAY_SIZE + 4 * 3;
	public static int MESSAGE_SIZE = 4; 

	public ServerMonitor(int port, int cameraNbr) {
		this.cameraNbr = cameraNbr;
		this.port = port;
		camera = new AxisM3006V();
		camera.init();
		camera.setProxy("argus-1.student.lth.se", port);
		header = new byte[MESSAGE_SIZE];

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void setCloseConnection(boolean closed) {
		this.closed = closed;
	}

	public synchronized boolean shouldCloseConnection() {
		return closed;
	}

	public synchronized void closeConnection() {
		try {
			clientSocket.close();
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void sendPackage(byte[] packet) {
		try {
			os.write(packet);
			os.flush();
		} catch (IOException e) {
			// TODO What happens here when the connection is closed? will it
			// block the sending of images or do we need to handle this?
			e.printStackTrace();
		}

	}

	public synchronized byte[] packageImage(int type, int size, int cameraNbr,
			byte[] time, byte[] image) {
		ByteBuffer bb = ByteBuffer.allocate(BUFFER_LENGTH);
		bb.putInt(type);
		bb.putInt(size);
		bb.putInt(cameraNbr);// 4 bytes for every int
		bb.put(time);
		bb.put(image);
		byte[] message = new byte[bb.capacity()];
		bb.get(message, 0, message.length);
		return message;

	}

	public synchronized ServerSocket getServerSocket() {
		return serverSocket;
	}

	public synchronized void acceptClient() {
		try {
			clientSocket = serverSocket.accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		notifyAll();
	}

	/**
	 * public synchronized void setClientSocket(Socket clientSocket) {
	 * this.clientSocket = clientSocket; synchStreams(); readRequest();
	 * readHeader();
	 * 
	 * notifyAll(); }
	 */
	public synchronized void synchStreams() {
		try {

			is = clientSocket.getInputStream();
			os = clientSocket.getOutputStream();
		} catch (IOException e) {
			System.out.println("Caught exception " + e);
		}
	}

	/**
	 * I believe this method is wrongly implemented with our new header, maybe
	 * WIIIIIIIIIIIHOOOOOOOOOOOOOO XDXDXD hälsar emnup
	 * 
	 * @return
	 */

	/*
	 * We say that the package only have 1 int information, thus 4 byte size to represent the different commands
	 */

	public synchronized void readMessage() {
		
		try {
			is.read(header);
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}

	}
	public synchronized void updateValuesFromMessage(){
		updateCommand();
	}
	private synchronized void updateCommand(){
		ByteBuffer bb= ByteBuffer.wrap(header);
		command = bb.getInt();
	}
	

	public synchronized String readRequest() {
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
/*
	public synchronized String getHeader() {
		return header;
	}
*/
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

	/**
	 * Read a line from InputStream 's', terminated by CRLF. The CRLF is not
	 * included in the returned string.
	 */
	private synchronized String getLine(InputStream s) throws IOException {
		boolean done = false;
		String result = "";

		while (!done) {
			int ch = s.read(); // Read *** blocks until data is available YAAAAY
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
