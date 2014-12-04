package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

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
	private ImageBuffer imageBufferServer1, imageBufferServer2; // An image ring buffer
																// containing
																// 125 Images

	private boolean imageS1LastTime;
	/**
	 * Attributes for handling commands in updater
	 */
	private CommandBuffer updaterBuffer;
	private int[][] cameraCommands;

	/**
	 * Attributes for handling commands in writer
	 */
	private CommandBuffer writerBufferServer1, writerBufferServer2;
	private boolean server1LastTime; // Is used for fair checking of what server
										// to write to.

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
		socketAddress = socketAddr;
		socket = new Socket[nbrOfSockets];
		isConnected = new boolean[nbrOfSockets];
		inputStream = new InputStream[nbrOfSockets];
		outputStream = new OutputStream[nbrOfSockets];
		somethingOnStream = new boolean[nbrOfSockets];
		this.nbrOfSockets = nbrOfSockets;
		writerBufferServer1 = new CommandBuffer(COMMAND_BUFFER_SIZE);
		writerBufferServer2 = new CommandBuffer(COMMAND_BUFFER_SIZE);
		updaterBuffer = new CommandBuffer(COMMAND_BUFFER_SIZE);
		imageBufferServer1 = new ImageBuffer(IMAGE_BUFFER_SIZE);
		imageBufferServer2 = new ImageBuffer(IMAGE_BUFFER_SIZE);
		updateGUI = false;
		finishedUpdating = true;
		syncMode = false;
		movieMode = false;
		imageS1LastTime = false;
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
					+ "please give a value between 0 and " + socket.length
					+ (-1));
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

				if (serverIndex == 0) {
					writerBufferServer1 = new CommandBuffer(COMMAND_BUFFER_SIZE);
				} else {
					writerBufferServer2 = new CommandBuffer(COMMAND_BUFFER_SIZE);

				}
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
		if (!(serverIndex >= 0 && serverIndex < socket.length)) {
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
				while (!isConnected[serverIndex]) {// || !finishedUpdating
					wait();
				}
				// System.out.println("read header client side");
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

						byte[] temp = readByteArray(serverIndex, 8);
						ByteBuffer bb = ByteBuffer.wrap(temp);
						long timeStamp = bb.getLong();

						System.out.println("Timestamp client side is "
								+ timeStamp);

						long delay = System.currentTimeMillis() - timeStamp;

						System.out.println("Delay client side is " + delay);

						Image image = new Image(cameraNumber, timeStamp, delay,
								readByteArray(serverIndex, size));

						newImage = true;
						updateGUI = true;
//						finishedUpdating = false;
						System.out.println("Not finished updating GUI");
						putImageToBuffer(image, serverIndex);// data contains
																// the image
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
//						finishedUpdating = false;
						putCommandToUpdaterBuffer(commandData);
						putCommandToClientWriter(1 - serverIndex, commandData); // Send
																		// movie
																		// mode
																		// to
																		// the
																		// other
																		// server
						notifyAll();
					} else {
						throw new Exception("You got a non existing type :D");
					}
				} else {

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
	 * Sends only an int to the server 0=close connection 1=movieMode 2=idle
	 * 3=connect to server
	 * 
	 * @throws IOException
	 * 
	 */
	public synchronized void sendMessageToServer() throws IOException {
		while (writerBufferServer1.getNbrOfCommandsInBuffer() <= 0
				&& writerBufferServer2.getNbrOfCommandsInBuffer() <= 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		int serverIndex;
		int command;
		if (server1LastTime) {// If server 1 was checked first last time
			server1LastTime = !server1LastTime;
			if (writerBufferServer2.getNbrOfCommandsInBuffer() > 0) {
				serverIndex = 1;
				command = writerBufferServer2.getCommandFromBuffer();
			} else {
				serverIndex = 0;
				command = writerBufferServer1.getCommandFromBuffer();
			}
		} else {// If server 2 was checked first last time
	
			if (writerBufferServer1.getNbrOfCommandsInBuffer() > 0) {
				serverIndex = 0;
				command = writerBufferServer1.getCommandFromBuffer();
			} else {
				serverIndex = 1;
				command = writerBufferServer2.getCommandFromBuffer();
			}
		}
		switch (command) {
		case CLOSE_CONNECTION:
			if (isConnected[serverIndex]) {
				byte[] bytes = ByteBuffer.allocate(4).putInt(command, 0)
						.array();
				// TODO controllera att byte arrays används rätt överallt
				outputStream[serverIndex].write(bytes, 0, 4);
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
			byte[] bytes1 = ByteBuffer.allocate(4).putInt(command, 0).array();
			for (int i = 0; i < nbrOfSockets; i++) {
				if (isConnected[i]) {
					outputStream[i].write(bytes1, 0, 4);
					outputStream[i].flush();
				}
			}
			movieMode = true;
			break;
	
		case IDLE_MODE:
			byte[] bytes2 = ByteBuffer.allocate(4).putInt(command, 0).array();
			for (int i = 0; i < nbrOfSockets; i++) {
				if (isConnected[i]) {
					outputStream[i].write(bytes2, 0, 4);
					outputStream[i].flush();
				}
			}
			movieMode = false;
			break;
		}
		notifyAll();
	}

	public synchronized void putCommandToClientWriter(int serverIndex, int command) {
		if (serverIndex == 0) {
			writerBufferServer1.putCommandToBuffer(command);
		} else {
			writerBufferServer2.putCommandToBuffer(command);
		}
		notifyAll();
	}

	private synchronized void putCommandToUpdaterBuffer(int com) {
		updaterBuffer.putCommandToBuffer(com);
		notifyAll();
	}

	public synchronized int getCommandFromUpdaterBuffer() {
		while (updaterBuffer.getNbrOfCommandsInBuffer() <= 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		int com = updaterBuffer.getCommandFromBuffer();
//		finishedUpdating = true;
		System.out.println("finished updating GUI");
		notifyAll();
		return com;
	}

	private synchronized void putImageToBuffer(Image image, int serverIndex) {
		if (serverIndex == 0) {
			imageBufferServer1.putImageToBuffer(image);
		} else if (serverIndex == 1) {
			imageBufferServer2.putImageToBuffer(image);
		}
		notifyAll();
	}

	public synchronized Image getImageFromBuffer() {
		while (imageBufferServer1.getNbrOfImagesInBuffer() <= 0
				&& imageBufferServer2.getNbrOfImagesInBuffer() <= 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Image image;
		if (imageS1LastTime) {// If server 1 was checked first last time
			imageS1LastTime = !imageS1LastTime;
			if (imageBufferServer2.getNbrOfImagesInBuffer() > 0) {
				image = imageBufferServer2.getImageFromBuffer();
			} else {
				image = imageBufferServer1.getImageFromBuffer();
			}
		} else {// If server 2 was checked first last time

			if (imageBufferServer1.getNbrOfImagesInBuffer()> 0) {
				image = imageBufferServer1.getImageFromBuffer();
			} else {
				image = imageBufferServer2.getImageFromBuffer();
			}
		}
//		finishedUpdating = true;
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

	public synchronized void somethingOnStream(int serverIndex)
			throws IOException, InterruptedException {
		// TODO controll that this is corectly implemented
		while (!isConnected[serverIndex]) {
			wait();
		}
		if (inputStream[serverIndex].available() > 0) {
			System.out
					.println("trueie****************************************************************");
			somethingOnStream[serverIndex] = true;
			// notifyAll();
		} else {
			// System.out.println("falsie");
			somethingOnStream[serverIndex] = false;
			// notifyAll();
		}
	}

	public synchronized long SyncMode(Image imageC1, Image imageC2) {

		return 0;
	}

}
