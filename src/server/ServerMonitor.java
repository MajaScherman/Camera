package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import se.lth.cs.eda040.fakecamera.AxisM3006V; //For constants
import client.ClientMonitor;

public class ServerMonitor {
	/**
	 * Attributes for connection
	 */
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private InputStream is;
	private OutputStream os;
	private boolean isConnected;


	/**
	 * Attributes for commands
	 */
	private boolean movieMode;
	private long lastTimeSentImg;

	// Hopefully its ok to add 3*4 for the type, cameraNbr, and size.
	public static int BUFFER_LENGTH = AxisM3006V.IMAGE_BUFFER_SIZE
			+ AxisM3006V.TIME_ARRAY_SIZE + 4 * 3;
	public static int MESSAGE_SIZE = 4;

	public ServerMonitor(int port, int cameraNbr) {

		movieMode = isConnected = false;
		lastTimeSentImg = System.currentTimeMillis();
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out
					.println("ServerSocket could not be created in ServerMonitor constructor");
			e.printStackTrace();
		}
	}

	public synchronized boolean isConnected() {
		return isConnected;
	}

	public synchronized void openConnection() {
		if (isConnected) {
			System.out.println("Server connection is already established");
		} else {
			try {
				clientSocket = serverSocket.accept();// blocking until connection available
				is = clientSocket.getInputStream();
				os = clientSocket.getOutputStream();
				isConnected = true;
				lastTimeSentImg = System.currentTimeMillis();
				notifyAll();
			} catch (IOException e) {
				System.out.println("Could not establish connection" + e);
			}
		}
	}

	public synchronized InputStream getInputStream() {
		return is;
	}

	public synchronized OutputStream getOutputStream() {
		return os;
	}

	public synchronized void closeConnection() throws SocketException {
		if (!isConnected) {
			System.out.println("Connection is already closed");
		} else {
			try {
				movieMode = false;
				isConnected = false;
				notifyAll();
				clientSocket.close();
				System.out.println("CLOSED CONNECTION!");
				// serverSocket.close();
			} catch (IOException e) {
				System.out.println("SPACESHIP IS CLOSED");
				throw new SocketException(
						"the connection is closed and IOException: " + e);
			}
			throw new SocketException("The connection is closed");
		}
	}



	/**
	 * Interprets the command and performs the correct actions.
	 */
	public synchronized void runCommand(int command) throws SocketException {
		switch (command) {
		case ClientMonitor.CLOSE_CONNECTION:
			closeConnection();
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

	public synchronized boolean isReadyToSendMessage() {
		long diff = System.currentTimeMillis() - lastTimeSentImg;
		return (((diff >= 0) || movieMode) && isConnected);
	}

	public synchronized void waitForConnection() throws InterruptedException {
		while(!isConnected){
			wait();
		}
	}

	public void setMovieMode(boolean b) {
		movieMode = b;
	}

	public void updateLastTimeSent() {
		lastTimeSentImg = System.currentTimeMillis();
	}

}
