package server;

import java.io.InputStream;
import java.net.SocketException;
import java.nio.ByteBuffer;

import se.lth.cs.eda040.fakecamera.AxisM3006V; //For constants
import client.ClientMonitor;

public class ServerMonitor {
	/**
	 * Attributes for connection
	 */
	private InputStream is;

	private boolean isConnected;
	private int serverNbr;
	private boolean inputStreamIsSet;

	/**
	 * Attributes for commands
	 */
	private boolean movieMode;
	private long lastTimeSentImg;

	// Hopefully its ok to add 3*4 for the type, cameraNbr, and size.
	public static int BUFFER_LENGTH = AxisM3006V.IMAGE_BUFFER_SIZE
			+ AxisM3006V.TIME_ARRAY_SIZE + 4 * 3;
	public static int MESSAGE_SIZE = 4;

	public ServerMonitor(int serverNbr) {
		this.serverNbr = serverNbr;
		movieMode = isConnected = inputStreamIsSet = false;
		lastTimeSentImg = System.currentTimeMillis();

	}

	public synchronized boolean isConnected() {
		return isConnected;
	}

	public synchronized void setConnectionOpened() {
		if (isConnected) {
			System.out.println("Server connection is already established");
		} else {
			isConnected = true;
			lastTimeSentImg = System.currentTimeMillis();
			notifyAll();

		}
	}

	public synchronized InputStream getInputStream() throws InterruptedException {
		while (!inputStreamIsSet) {
			wait();
		}
		return is;
	}

	public synchronized void setConnectionClosed() throws SocketException {
		if (!isConnected) {
			System.out.println("Connection is already closed");
		} else {
			movieMode = false;
			isConnected = false;
			inputStreamIsSet = false;
			notifyAll();
			throw new SocketException("The connection is closed");
		}
	}

	/**
	 * Interprets the command and performs the correct actions.
	 */
	public synchronized void runCommand(int command) throws SocketException {
		switch (command) {
		case ClientMonitor.CLOSE_CONNECTION:
			setConnectionClosed();
			break;
		case ClientMonitor.MOVIE_MODE:
			movieMode = true;
			break;
		case ClientMonitor.IDLE_MODE:
			movieMode = false;
			break;
		default:
			System.out.println("Invalid command sent to server from client");
			break;
		}
		notifyAll();
	}

	public synchronized boolean isReadyToSendImage() {
		long diff = System.currentTimeMillis() - lastTimeSentImg;
		return (((diff >= 5000) || movieMode)); //  && isConnected
	}

	public synchronized void waitForConnection() throws InterruptedException {
		while (!isConnected) {
			wait();
		}
	}

	public synchronized void setMovieMode(boolean b) {
		movieMode = b;
		notifyAll();
	}

	public synchronized void updateLastTimeSent() {
		lastTimeSentImg = System.currentTimeMillis();
		notifyAll();
	}

	public synchronized byte[] packageImage(int length, byte[] time,
			byte[] image) {
		ByteBuffer bb = ByteBuffer.allocate(12 + AxisM3006V.TIME_ARRAY_SIZE
				+ length);
		bb.putInt(ClientMonitor.IMAGE);
		bb.putInt(length);
		bb.putInt(serverNbr);// 4 bytes for every int
		bb.put(time);
		bb.put(image, 0, length);
		return bb.array();

	}

	public synchronized void setInputStream(InputStream is2) {
		this.is = is2;
		inputStreamIsSet = true;
		notifyAll();
	}

}
