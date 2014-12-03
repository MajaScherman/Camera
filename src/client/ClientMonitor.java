package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

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
	private boolean finishedUpdating;
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

	/**
	 * attributes for reader, connection //TODO
	 */
	private boolean[] somethingOnStream;

	public static final int IMAGE_SIZE = 640 * 480 * 3; // REQ 7
	public static final int IMAGE_BUFFER_SIZE = 125; // Supports 125 images,
														// which is 5 seconds of
														// video in 25 fps
	public static final int COMMAND_BUFFER_SIZE = 125; // Supports 125 images,
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
		somethingOnStream = new boolean[nbrOfSockets];
		this.nbrOfSockets = nbrOfSockets;
		imageBuffer = new Image[IMAGE_BUFFER_SIZE];
		commandBuffer = new int[COMMAND_BUFFER_SIZE];
		getAt = putAt = nbrOfImgsInBuffer = 0;
		getAtC = putAtC = nbrOfCommandsInBuffer = 0;
		finishedUpdating = true;
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
				outputStream[serverIndex].flush();
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
					outputStream[serverIndex].flush();
				}
			}
			movieMode = true;
			break;

		case IDLE_MODE:
			for (int i = 0; i < nbrOfSockets; i++) {
				if (isConnected[i]) {
					outputStream[i].write(writerCommand[serverIndex]);
					outputStream[serverIndex].flush();
				}
			}
			movieMode = false;
			break;
		}
		notifyAll();
	}

	public synchronized void somethingOnStream(int serverIndex)
			throws IOException, InterruptedException {
		// TODO controll that this is corectly implemented
		while (!isConnected[serverIndex]) {
			wait();
		}
		if (inputStream[serverIndex].available() > 0) {
			System.out.println("trueie****************************************************************");
			somethingOnStream[serverIndex] = true;
			//notifyAll();
		} else {
			//System.out.println("falsie");
			somethingOnStream[serverIndex] = false;
			//notifyAll();
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
				while (!isConnected[serverIndex]) {// || !finishedUpdating
					wait();
				}
				//System.out.println("read header client side");
				// Read header - read is blocking
				if (somethingOnStream[serverIndex]) {
					System.out.println("frogie");
					int type = readInt(serverIndex);
					System.out.println("Type is " + type);
					if (type == IMAGE) {
						int size = readInt(serverIndex);
						System.out.println("Package size client side JPEG"
								+ size);
						int cameraNumber = readInt(serverIndex);
						System.out.println("Camera number client side is "
								+ cameraNumber);
						byte[] timeStamp = readByteArray(serverIndex, 8);

						System.out.println("Timestamp client side is "
								+ Arrays.toString(timeStamp));

						Image image = new Image(cameraNumber, timeStamp,
								readByteArray(serverIndex, size));
						newImage = true;
						updateGUI = true;
						finishedUpdating = false;
						putImageToBuffer(image);// data contains the image
						notifyAll();
					} else if (type == COMMAND) {
						int commandData = readInt(serverIndex);
						System.out.println("command data client side is"
								+ commandData);
						if (commandData != MOVIE_MODE) {
							throw new Exception(
									"Client have recieved an invalid command");

						}
						newMode = true;
						updateGUI = true;
						putCommandToBuffer(commandData);
						notifyAll();
					} else {
						throw new Exception("You got a non existing type :D");
					}
				}else{
					
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

	private synchronized void putCommandToBuffer(int com) {
		commandBuffer[putAtC] = com;
		putAtC++;
		nbrOfCommandsInBuffer++;
		if (putAtC >= COMMAND_BUFFER_SIZE) {
			putAtC = 0;
		}
		notifyAll();
	}

	public synchronized int getCommandFromBuffer() {
		while (nbrOfCommandsInBuffer <= 0) {
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
		if (getAtC >= COMMAND_BUFFER_SIZE) {
			getAtC = 0;
		}
		finishedUpdating = true;
		notifyAll();
		return com;
	}

	private synchronized void putImageToBuffer(Image image) {
		System.out.println("putting image in buffer");
		imageBuffer[putAt] = image;
		putAt++;
		nbrOfImgsInBuffer++;
		if (putAt >= 125) {
			putAt = 0;
		}
		notifyAll();
	}

	public synchronized Image getImageFromBuffer() {
		while (nbrOfImgsInBuffer <= 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("collecting an image");
		Image image = imageBuffer[getAt];
		getAt++;
		nbrOfImgsInBuffer--;
		if (getAt >= 125) {
			getAt = 0;
		}
		finishedUpdating = true;
		notifyAll();
		return image;
	}

	private synchronized byte[] readByteArray(int serverIndex, int size) {
		byte[] temp = new byte[size];
		int bytesLeft = size;
		int tempIndex = 0;
		while (bytesLeft > 0) {
			// läs en byte
			int read;
			try {
				read = inputStream[serverIndex].read();
				// lägg i byteToInt
				// TODO Kolla read fallet -1
				temp[tempIndex] = (byte) read;
				tempIndex++;
				bytesLeft--;
			} catch (IOException e) {
				System.out.println("error in readint method client side" + e);
				e.printStackTrace();
			}
		}
		return temp;
	}

	private synchronized int readInt(int serverIndex) {
		System.out.println("Reading ints");
		byte[] temp = new byte[4];

		// läs en byte
		int k = 0;
		try {
			System.out.println("groda");
			k = inputStream[serverIndex].read(temp, 0, 4);

		} catch (IOException e) {
			System.out.println("error in readint method client side" + e);
			e.printStackTrace();
		}
		System.out.println("hej");
		if (k == 4) {
			System.out.println("4 byte lästes in till temp");
		}
		// Konvertera till int
		ByteBuffer bb = ByteBuffer.wrap(temp);
		return bb.getInt(0);
	}

}
