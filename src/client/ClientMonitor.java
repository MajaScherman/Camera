package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class ClientMonitor {
	private String image;
	/**
	 * Attributes for change of modes
	 */
	private int command;
	private boolean syncMode;
	private boolean movieMode;
	/**
	 * Attributes for updating GUI
	 */
	private boolean updateGUI;
	private boolean newImage; // The updater checks if a new image has arrived
								// in data
	private boolean newMode; // The updater checks if a new mode has arrived in
								// data
	/**
	 * Attributes for connecting
	 */
	private Socket[] socket;
	private SocketAddress[] socketAddress;
	private boolean[] isConnected;
	private InputStream[] inputStream;
	private OutputStream[] outputStream;
	private int nbrOfSockets;
	/**
	 * Header attributes
	 */
	private byte[] byteToInt; // byte array with size 4, with purpose to
								// transform tó int
	private int type;
	private int size;
	private int cameraNumber;
	private byte[] timeStamp;
	/**
	 * Data in packets
	 */
	private byte[] data;

	public ClientMonitor(int nbrOfSockets, SocketAddress[] socketAddr) {
		syncMode = false;
		movieMode = false;
		updateGUI = false;
		socketAddress = socketAddr;
		socket = new Socket[nbrOfSockets];
		isConnected = new boolean[nbrOfSockets];
		inputStream = new InputStream[nbrOfSockets];
		outputStream = new OutputStream[nbrOfSockets];
		byteToInt = new byte[4];
		timeStamp = new byte[8];
		this.nbrOfSockets = nbrOfSockets;
	}

	/**
	 * Sends only an int to the server
	 * 0=close connection 1=movieMode 2=idle 3=connect to server
	 * 
	 * @throws IOException
	 * 
	 */
	public void sendMessageToServer(int command, int serverIndex)
			throws IOException {
		switch (command) {
		case 0:
			outputStream[serverIndex].write(command);

			disconnectToServer(serverIndex);
			break;
		case 1:
			outputStream[serverIndex].write(command);
			movieMode = true;
			break;
		case 2:
			outputStream[serverIndex].write(command);
			movieMode = false;
			break;
		case 3:
			outputStream[serverIndex].write(command);
			connectToServer(serverIndex);
			break;
		}
		notifyAll();
	}

	public synchronized int getCommand(){
		return command;
	}
	public synchronized int getNbrOfSockets(){
		return nbrOfSockets;
	}
	/**
	 * Request to update the GUI, meaning an image has been sent and the update
	 * should be notified.
	 */
	public synchronized void updateRequest() {
		updateGUI = true;
		notifyAll();
	}

	/**
	 * Tells the updater to update the GUI when time is due.
	 */
	public synchronized void checkUpdate() {
		while (updateGUI == false) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (newImage) {
			newImage = false;
		} else if (newMode) {
			newMode = false;
		}
		updateGUI = false;
		notifyAll();
	}
	
	/**
	 * Establishes connection to server
	 * 
	 * @param serverIndex
	 *            the index of the server we want to connect to (start from
	 *            index 0 and goes up to socketArray.length)
	 */
	public synchronized void connectToServer(int serverIndex) {
		if (!(serverIndex > 0 && serverIndex < socket.length)) {
			// TODO Throw exception
			System.out.println("The server index is out of range, "
					+ "please give a value between 0 and " + socket.length);
		} else if (isConnected[serverIndex]) {
			System.out.println("The server is already connected");
		} else {
			// Establish connection
			// Server must be running before trying to connect
			String host = socketAddress[serverIndex].getHost();
			int port = socketAddress[serverIndex].getPortNumber();
			try {
				socket[serverIndex] = new Socket(host, port);
				// Set socket to no send delay
				socket[serverIndex].setTcpNoDelay(true);
				// Get input stream
				inputStream[serverIndex] = socket[serverIndex].getInputStream();
				// Get output stream
				outputStream[serverIndex] = socket[serverIndex]
						.getOutputStream();
				isConnected[serverIndex] = true;
				notifyAll();
				System.out.println("Server connection with server "
						+ serverIndex + " established");
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Disconnects to the server
	 * 
	 * @param serverIndex
	 *            the index of the server one wants to disconnect to
	 */
	public synchronized void disconnectToServer(int serverIndex) {
		if (!(serverIndex > 0 && serverIndex < socket.length)) {
			// TODO Throw exception
			System.out.println("The server index is out of range, "
					+ "please give a value between 0 and " + socket.length);
		} else if (!isConnected[serverIndex]) {
			System.out.println("The server with index " + serverIndex
					+ " is not connected");
		} else {
			// Close the socket, i.e. abort the connection
			try {
				socket[serverIndex].close();
				isConnected[serverIndex] = false;
				notifyAll();
				System.out.println("Disconnected to server" + serverIndex
						+ " successfully");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	//TODO handle button to command stuff so we can get every command not only 2, and send it to writer thingeingingeee

	/**
	 * This method receives messages from the server.
	 * 
	 * @param server
	 *            The server to listen to starting from index 0
	 * @throws Exception
	 */
	public synchronized void listenToServer(int serverIndex) throws Exception {
		// TODO Kolla att serverindex är inom bounds
		try {
			while (!isConnected[serverIndex]) {
				wait();
			}
			// Read header - read is blocking
			type = readHeaderInts(serverIndex);
			System.out.println("Type is " + type);
			size = readHeaderInts(serverIndex);
			System.out.println("Package size " + size);
			cameraNumber = readHeaderInts(serverIndex);
			System.out.println("Camera number is " + cameraNumber);
			inputStream[serverIndex].read(timeStamp);
			System.out.println("Timestamp is " + timeStamp);
			if (type == 0) {
				readPackage(serverIndex);
				newImage = true;
				updateGUI = true;
				command =type;
				notifyAll();
			} else if (type == 1) {
				readPackage(serverIndex);
				newMode = true;
				updateGUI = true;
				command =type;
				notifyAll();
			} else {
				throw new Exception();
			}
			// TODO Skapa paket buffert så att updategui hinner uppdatera innan
			// header attributen ändras.
			// TODO verifiera att paket är korrekt
		} catch (IOException e) {
			// Occurs in read method of
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private synchronized void readPackage(int serverIndex) throws IOException {
		data = new byte[size];

		// Read package
		int read = 0; // Number of read bytes so far
		while (read != size) {
			// Read bytes and put in data array until size bytes are
			// read
			// Read returns number of bytes read <= size-read
			int n;
			try {
				n = inputStream[serverIndex].read(data, read, size - read);
				if (n == -1) {
					throw new IOException("End of stream");
				}
				read += n;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				throw new IOException(e);
			}
		}
	}

	private synchronized int readHeaderInts(int serverIndex) {

		// Hämta fyra bytes
		try {
			inputStream[serverIndex].read(byteToInt);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Konvertera till int
		ByteBuffer bb = ByteBuffer.wrap(byteToInt);
		return bb.getInt();
	}

	/**
	 * This method writes data to the server.
	 */
	public void writeToServer(int serverIndex) {
		// TODO
	}
}
