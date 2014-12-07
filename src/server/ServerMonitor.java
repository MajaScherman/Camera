package server;

import java.io.InputStream;
import java.net.SocketException;
import java.nio.ByteBuffer;

import se.lth.cs.eda040.fakecamera.AxisM3006V; //For constants

public class ServerMonitor {
	/**
	 * Attributes for connection
	 */
	private InputStream is;

	private boolean isConnected;
	private int serverNbr;
	private boolean inputStreamIsSet;
	private boolean forcedMode;

	public static final int CLOSE_CONNECTION = 0;
	public static final int MOVIE_MODE = 2;
	public static final int IDLE_MODE = 3;
	public static final int IMAGE = 0;
	public static final int COMMAND = 1;
	public static final int AUTO = 6;
	public static final int FORCED = 7;

	/**
	 * Attributes for commands
	 */
	private boolean movieMode, realMovieMode; // realMovieMode notices changes
	private boolean hasChanged;											// in forcemode but does not
												// update until set to auto
	private long lastTimeSentImg;

	// Hopefully its ok to add 3*4 for the type, cameraNbr, and size.
	public static int BUFFER_LENGTH = AxisM3006V.IMAGE_BUFFER_SIZE
			+ AxisM3006V.TIME_ARRAY_SIZE + 4 * 3;
	public static int MESSAGE_SIZE = 4;

	public ServerMonitor(int serverNbr) {
		this.serverNbr = serverNbr;
		movieMode = isConnected = inputStreamIsSet = forcedMode = false;
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

	public synchronized InputStream getInputStream()
			throws InterruptedException {
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
		case CLOSE_CONNECTION:
			setConnectionClosed();
			break;
		case MOVIE_MODE:
			movieMode = true;
			break;
		case IDLE_MODE:
			movieMode = false;
			break;
		case FORCED:
			forcedMode = true;
			break;
		case AUTO:
			hasChanged = movieMode == realMovieMode;
			movieMode = realMovieMode;
			forcedMode = false;
			break;
		default:
			System.out.println("Invalid command sent to server from client");
			break;
		}
		notifyAll();
	}

	public synchronized boolean isReadyToSendImage() {
		if (forcedMode) {
			if (movieMode) {
				return true;
			} else {
				long diff = System.currentTimeMillis() - lastTimeSentImg;
				return (diff >= 5000);
			}
		} else {
			long diff = System.currentTimeMillis() - lastTimeSentImg;
			return (((diff >= 5000) || movieMode)); // && isConnected
		}
	}
	
	public synchronized boolean checkIfHasChanged(){
		return hasChanged;
	}

	public synchronized void waitForConnection() throws InterruptedException {
		while (!isConnected) {
			wait();
		}
	}

	public synchronized boolean trySetMovieMode(boolean b) {
		if (!forcedMode) {
			movieMode = b;
			notifyAll();
			return true;
		}
		realMovieMode = b;
		notifyAll();
		return false;
	}

	public synchronized void updateLastTimeSent() {
		lastTimeSentImg = System.currentTimeMillis();
		notifyAll();
	}

	public synchronized byte[] packageImage(int length, byte[] time,
			byte[] image) {
		ByteBuffer bb = ByteBuffer.allocate(12 + AxisM3006V.TIME_ARRAY_SIZE
				+ length);
		bb.putInt(IMAGE);
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
