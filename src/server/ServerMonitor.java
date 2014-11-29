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
	 * Attributes for connection
	 */
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private InputStream is; // the is from the client, is used as the os from
							// the server
	private OutputStream os; // the os from the client, is used as the is from
								// the server
	private boolean isConnected;

	/**
	 * Attributes for camera
	 */
	private AxisM3006V camera;
	private int cameraNbr;

	/**
	 * Attributes for commands
	 */
	private boolean movieMode;
	private int command;
	private long lastTimeSentImg;

	// Hopefully its ok to add 3*4 for our spaceship
	public static int BUFFER_LENGTH = AxisM3006V.IMAGE_BUFFER_SIZE
			+ AxisM3006V.TIME_ARRAY_SIZE + 4 * 3;
	public static int MESSAGE_SIZE = 4;

	// add statics for commands

	public ServerMonitor(int port, int cameraNbr) {
		this.cameraNbr = cameraNbr;
		camera = new AxisM3006V();
		camera.init();
		camera.setProxy("argus-1.student.lth.se", port);

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void establishConnection() {
		if (isConnected) {
			System.out.println("Server connection is already established");
		} else {
			try {
				clientSocket = serverSocket.accept();
				is = clientSocket.getInputStream();
				os = clientSocket.getOutputStream();
				isConnected = true;
				notifyAll();
			} catch (Exception e) {
				System.out.println("Could not establish connection");
			}
		}
	}

	public synchronized void closeConnection() {
		if (!isConnected) {
			System.out.println("Connection is already closed");
		} else {
			try {
				clientSocket.close();
				serverSocket.close();
			} catch (IOException e) {
				System.out.println("Could not close connection");
				e.printStackTrace();
			}
		}
	}

	// public synchronized ServerSocket getServerSocket() {
	// return serverSocket;
	// }

	/**
	 * Reads the message received from the client. The package only contains 1
	 * int to represent commands. Therefor the size of the package is only 4
	 * bytes.
	 */

	public synchronized void readAndUnpackCommand() {
		byte[] message = new byte[MESSAGE_SIZE];
		try {
			is.read(message);
		} catch (IOException e) {
			e.printStackTrace();

		}
		ByteBuffer bb = ByteBuffer.wrap(message);
		command = bb.getInt();

		notifyAll();
	}

	/**
	 * Interprets the command and performs the correct actions.
	 */
	public synchronized void runCommand() {
		switch (command) {
		case 0:
			isConnected = false;
			break;
		case 1:
			movieMode = true;
			break;
		case 2:
			movieMode = false;
			break;
		}
		notifyAll();
	}

	/**
	 * Read a line from InputStream 's', terminated by CRLF. The CRLF is not
	 * included in the returned string.
	 */
	// private synchronized String getLine(InputStream s) throws IOException {
	// boolean done = false;
	// String result = "";
	//
	// while (!done) {
	// int ch = s.read(); // Read *** blocks until data is available YAAAAY
	// if (ch <= 0 || ch == 10) {
	// // Something < 0 means end of data (closed socket)
	// // ASCII 10 (line feed) means end of line
	// done = true;
	// } else if (ch >= ' ') {
	// result += (char) ch;
	// }
	// }
	//
	// return result;
	// }
	public void write() {
		while (!isConnected) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		byte[] message = getImage();
		if (movieMode) {

			if(message != null){
				try {
					os.write(message);
					lastTimeSentImg = System.currentTimeMillis();
				} catch (IOException e) {
					System.out.println("Message could not be sent in movie mode");
					e.printStackTrace();
				}
				
			}
		} else {
			if(System.currentTimeMillis() - lastTimeSentImg>=5000){
				try {
					os.write(message);
				} catch (IOException e) {
					System.out.println("Message could not be sent in idle mode");
					e.printStackTrace();
				}
				lastTimeSentImg = System.currentTimeMillis();
			}else{
				 long t = lastTimeSentImg + 5000;
				 long diff = t - System. currentTimeMillis ();
				 if (diff > 0) {
					 Thread.sleep(diff);
						try {
							os.write(message);
						} catch (IOException e) {
							System.out.println("Message could not be sent in idle mode");
							e.printStackTrace();
						}
				 }
				}
			}
			

		}
		// metakod
		// kolla om vi fått bild
		// ev.uppdater mon
		// skicka bild till client
		// om upptäcka movement
		// så uppdatera mon till moviemode
		// sen meddela klienten att vi har fått movie mode

		
		 if (camera.motionDetected()) {
		 }
		
		

	}

	private synchronized byte[] getImage() {
		byte[] image = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
		byte[] imageTime = new byte[AxisM3006V.TIME_ARRAY_SIZE];
		int length = camera.getJPEG(image, 0);// 0 is our offset
		if (length != 0) {
			// ev.uppdater mon
			// skicka bild till client
			camera.getTime(imageTime, 0);
			byte[] message = packageImage(0, length, cameraNbr, imageTime,
					image);
			return message;
		}
		return null;
	}

	/**
	 * Collects the necessary information about the image and puts it into a
	 * byte array.
	 * 
	 * @param type
	 *            , Tells the client what the package contains, in this case an
	 *            image.
	 * @param size
	 *            , size of image.
	 * @param cameraNbr
	 *            , Tells from which camera the package is sent.
	 * @param time
	 *            , At which time the image was taken.
	 * @param image
	 *            , The image itself.
	 * @return
	 */
	private synchronized byte[] packageImage(int type, int size, int cameraNbr,
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
}
