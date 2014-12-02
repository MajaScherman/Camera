package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import se.lth.cs.eda040.fakecamera.AxisM3006V;

public class ClientMonitor {

	/**
	 * Attributes for change of modes
	 */
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

	public static final int IMAGE = 0;
	public static final int COMMAND = 1;
	

	/**
	 * Data in packets
	 */
	private byte[] data;
	public static final int CLOSE_CONNECTION = 0;
	public static final int OPEN_CONNECTION = 1;
	public static final int MOVIE_MODE = 2;
	public static final int IDLE_MODE = 3;
	public static final int ASYNCHRONIZED = 4;
	public static final int SYNCHRONIZED = 5;

	/**
	 * Attributes for handling images
	 */
	private Image[] imageBuffer; // An image ring buffer containing 125 Images

	private int putAt;
	private int getAt;
	private int nbrOfImgsInBuffer;
	private Image[][] cameraImages; // Contains one image buffer for each
									// camera

	/**
	 * Attributes for handling commands in updater
	 */
	private int[] commandBuffer;
	private int putAtC;
	private int getAtC;
	private int[][] cameraCommands;
	private int nbrOfCommandsInBuffer;

	/**
	 * Attributes for handling commands in writer
	 */
	private int[] writerCommand;
	private boolean[] newCommand;

	public static final int IMAGE_SIZE = 640 * 480 * 3; // REQ 7
	public static final int IMAGE_BUFFER_SIZE = 125; // Supports 125 images,
														// which is 5 seconds of
														// video in 25 fps
	public static final int NUMBER_OF_CAMERAS = 2;

	public ClientMonitor(int nbrOfSockets, SocketAddress[] socketAddr) {
		syncMode = false;
		movieMode = false;
		updateGUI = false;
		socketAddress = socketAddr;
		writerCommand = new int[nbrOfSockets];
		newCommand = new boolean[nbrOfSockets];
		socket = new Socket[nbrOfSockets];
		isConnected = new boolean[nbrOfSockets];
		inputStream = new InputStream[nbrOfSockets];
		outputStream = new OutputStream[nbrOfSockets];
		this.nbrOfSockets = nbrOfSockets;
		imageBuffer = new Image[IMAGE_BUFFER_SIZE];
		getAt = putAt = nbrOfImgsInBuffer = 0;
		getAtC = putAtC = nbrOfCommandsInBuffer = 0;
	}

	public synchronized void putCommandToWriter(int serverIndex, int command) {
		writerCommand[serverIndex] = command;
		newCommand[serverIndex] = true;
		notifyAll();
	}

	/**
	 * Sends only an int to the server 0=close connection 1=movieMode 2=idle
	 * 3=connect to server
	 * 
	 * @throws IOException
	 * 
	 */
	public synchronized void sendMessageToServer() throws IOException {
		while (!newCommand[0] && !newCommand[1]) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		int serverIndex;
		if (newCommand[0]) {
			serverIndex = 0;
		} else {
			serverIndex = 1;
		}
		newCommand[serverIndex] = false;
		notifyAll();
		switch (writerCommand[serverIndex]) {
		case CLOSE_CONNECTION: 
			if (isConnected[serverIndex]) {
				outputStream[serverIndex].write(writerCommand[serverIndex]);
				disconnectToServer(serverIndex);
			}
			break;
		case OPEN_CONNECTION:
			if (!isConnected[serverIndex]) {
				connectToServer(serverIndex);
			}
			break;
		case MOVIE_MODE:
			for (int i = 0; i < nbrOfSockets; i++) {
				if (isConnected[i]) {
					outputStream[i].write(writerCommand[serverIndex]);
				}
			}
			movieMode = true;
			break;

		case IDLE_MODE:
			for (int i = 0; i < nbrOfSockets; i++) {
				outputStream[i].write(writerCommand[serverIndex]);
			}

			movieMode = false;
			break;
		}
		notifyAll();
	}

	public synchronized int getNbrOfSockets() {
		return nbrOfSockets;
	}

	/**
	 * Tells the updater to update the GUI when time is due.
	 */
	public synchronized int checkUpdate() throws Exception {
		while (updateGUI == false) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		updateGUI = false;
		if (newImage) {
			newImage = false;
			notifyAll();
			return IMAGE;
		} else if (newMode) {
			newMode = false;
			notifyAll();
			return COMMAND;
		} else {
			notifyAll();
			throw new Exception("Check update method is wrong");
		}
	}

	/**
	 * Establishes connection to server
	 * 
	 * @param serverIndex
	 *            the index of the server we want to connect to (start from
	 *            index 0 and goes up to socketArray.length)
	 */
	public synchronized void connectToServer(int serverIndex) {
		if (!(serverIndex >= 0 && serverIndex < socket.length)) {
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

	/**
	 * This method receives messages from the server.
	 * 
	 * @param server
	 *            The server to listen to starting from index 0
	 * @throws Exception
	 */
	public synchronized void listenToServer(int serverIndex) throws Exception {
		if (serverIndex >= 0 && serverIndex < nbrOfSockets) {
			try {
				while (!isConnected[serverIndex]) {
					wait();
				}
				System.out.println("read header client side");
				// Read header - read is blocking
				int type = readInt(serverIndex);
				System.out.println("Type is " + type);
				if (type == IMAGE) {
					int size = readInt(serverIndex);
					System.out.println("Package size JPEG" + size);
					int cameraNumber = readInt(serverIndex);
					System.out.println("Camera number is " + cameraNumber);
					byte[] timeStamp = new byte[8];
					int bytesRead  = 0;
					int bytesLeft  = timeStamp.length;
					int status;

					// We have to keep reading until -1 (meaning "end of file") is
					// returned. The socket (which the stream is connected to)
					// does not wait until all data is available; instead it
					// returns if nothing arrived for some (short) time.
					do {
						status = inputStream[serverIndex].read(timeStamp, bytesRead, bytesLeft);
						// The 'status' variable now holds the no. of bytes read,
						// or -1 if no more data is available
						if (status > 0) {
							bytesRead += status;
							bytesLeft -= status;
						}
					} while (bytesRead >= 0);
					System.out.println("Timestamp is " + timeStamp);

					Image image = new Image(cameraNumber, timeStamp,
							readPackage(serverIndex, size));
					newImage = true;
					updateGUI = true;
					putImageToBuffer(image);// data contains the image
					notifyAll();
				} else if (type == COMMAND) {
					int commandData = readInt(serverIndex);
					if (commandData != MOVIE_MODE) {
						throw new Exception(
								"Client have recieved an invalid command");

					}
					newMode = true;
					updateGUI = true;
					putCommandToBuffer(commandData);
					// om det finns en begäran på ett movie mode i paketet måste
					// command sätts till MOVIEMODE
					notifyAll();
				} else {
					throw new Exception("You got a non existing type :D");
				}
				// TODO verifiera att paket är korrekt
			} catch (IOException e) {
				// Occurs in read method of
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Check your server indices");
		}
	}

	private synchronized byte[] readPackage(int serverIndex, int size) throws IOException {
		// Now load the JPEG image.
		data = new byte[size];
		int bytesRead = 0;
		int bytesLeft = size;
		int status;

		// We have to keep reading until -1 (meaning "end of file") is
		// returned. The socket (which the stream is connected to)
		// does not wait until all data is available; instead it
		// returns if nothing arrived for some (short) time.
		do {
			status = inputStream[serverIndex].read(data, bytesRead, bytesLeft);
			// The 'status' variable now holds the no. of bytes read,
			// or -1 if no more data is available
			if (status > 0) {
				bytesRead += status;
				bytesLeft -= status;
			}
		} while (bytesRead >= 0);
		return data;
	}

	private synchronized void putCommandToBuffer(int com) {
		commandBuffer[putAtC] = com;
		putAtC++;
		nbrOfCommandsInBuffer++;
		if (putAtC >= 125) {
			putAtC = 0;
		}
		notifyAll();
	}

	public synchronized int getCommandFromBuffer() {
		while (nbrOfCommandsInBuffer < 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int com = commandBuffer[getAtC];
		getAtC++;
		nbrOfCommandsInBuffer--;
		if (getAtC >= 125) {
			getAtC = 0;
		}
		notifyAll();
		return com;
	}

	private synchronized void putImageToBuffer(Image image) {
		imageBuffer[putAt] = image;
		putAt++;
		nbrOfImgsInBuffer++;
		if (putAt >= 125) {
			putAt = 0;
		}
		notifyAll();
	}

	public synchronized Image getImageFromBuffer() {
		while (nbrOfImgsInBuffer < 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Image image = imageBuffer[getAt];
		getAt++;
		nbrOfImgsInBuffer--;
		if (getAt >= 125) {
			getAt = 0;
		}
		notifyAll();
		return image;
	}
	
	private synchronized int readInt(int serverIndex) {
		byte[] byteToInt = new byte[4];
		int bytesRead = 0;
		int bytesLeft = 4;
		int status;
		// We have to keep reading until -1 (meaning "end of file") is
		// returned. The socket (which the stream is connected to)
		// does not wait until all data is available; instead it
		// returns if nothing arrived for some (short) time.
		try {
			do {

				status = inputStream[serverIndex].read(byteToInt, bytesRead,
						bytesLeft);

				// The 'status' variable now holds the no. of bytes read,
				// or -1 if no more data is available
				if (status > 0) {
					bytesRead += status;
					bytesLeft -= status;
				}
			} while (bytesRead >= 0);
		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		// Konvertera till int
		ByteBuffer bb = ByteBuffer.wrap(byteToInt);
		return bb.getInt();
	}

}
