package server;
import java.io.IOException;
import java.io.InputStream; 
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket; 
import java.nio.ByteBuffer;

import se.lth.cs.eda040.fakecamera.AxisM3006V; // Provides AxisM3006V

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
	
	public static final int CLOSE_CONNECTION = 0;
	public static final int MOVIE_MODE = 1;
	public static final int IDLE = 2;
	public static final int START_CONNECTION = 3;
	
	public static final int IMAGE = 0;
	public static final int COMMAND = 1;

	// Hopefully its ok to add 3*4 for the type, cameraNbr, and size.
	public static int BUFFER_LENGTH = AxisM3006V.IMAGE_BUFFER_SIZE
			+ AxisM3006V.TIME_ARRAY_SIZE + 4 * 3;
	public static int MESSAGE_SIZE = 4;


	public ServerMonitor(int port, int cameraNbr,AxisM3006V camera ) {
		this.cameraNbr = cameraNbr;
		this.camera = camera;
		camera.init();
		camera.setProxy("argus-1.student.lth.se", port);

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("ServerSocket could not be created in ServerMonitor constructor");
			e.printStackTrace();
		}
	}
	
	public ServerSocket getServerSocket(){
		return serverSocket;
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
				isConnected = false;
			} catch (IOException e) {
				System.out.println("Could not close connection");
				e.printStackTrace();
			}
		}
		notifyAll();
	}


	/**
	 * Reads the message received from the client. The package only contains 1
	 * int to represent commands. Therefore the size of the package is only 4
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
		case CLOSE_CONNECTION:
			isConnected = false;
			break;
		case MOVIE_MODE:
			movieMode = true;
			break;
		case IDLE:
			movieMode = false;
			break;
		}
		notifyAll();
	}
	/**
	 * While connected images are fetched and sent to the client at the given speed depending on if the MovieMode is active or not.
	 * Checks if a motion was detected in the latest image, if so then MovieMode is activated and the client is informed.
	 */
	public void write() {
		try {
			while (!isConnected) {
				wait();
			}
			byte[] message = getImage();
			if (movieMode) {
				if (message != null) {
					os.write(message);
					lastTimeSentImg = System.currentTimeMillis();
				}
			} else {
				if (System.currentTimeMillis() - lastTimeSentImg >= 5000) {
					os.write(message);
					lastTimeSentImg = System.currentTimeMillis();
				} else {
					long t = lastTimeSentImg + 5000;
					long diff = t - System.currentTimeMillis();
					if (diff > 0) {
						Thread.sleep(diff);
						os.write(message);
					}
				}
				motionDetection();
				notifyAll();
			}
		} catch (Exception e) {
			System.out.println("Message could not be sent");
			e.printStackTrace();
		}
	}
/**
 * Fetches the image from the camera, creates a package with a header and the image.
 * @return message, the complete message with header and image. If no image was available then null is returned.
 */
	private synchronized byte[] getImage() {
		byte[] image = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
		byte[] imageTime = new byte[AxisM3006V.TIME_ARRAY_SIZE];
		int length = camera.getJPEG(image, 0);
		if (length != 0) {
			camera.getTime(imageTime, 0);
			byte[] message = packageData(IMAGE, length, cameraNbr, imageTime,
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
	 *            , Tells the client what the package contains, 0 = image 1=
	 *            command
	 * @param size
	 *            , size of image.
	 * @param cameraNbr
	 *            , Tells from which camera the package is sent.
	 * @param time
	 *            , At which time the image was taken.
	 * @param data
	 * 
	 * @return
	 */
	private synchronized byte[] packageData(int type, int size, int cameraNbr,
			byte[] time, byte[] data) {

		ByteBuffer bb = ByteBuffer.allocate(BUFFER_LENGTH);
		bb.putInt(type);
		bb.putInt(size);
		bb.putInt(cameraNbr);// 4 bytes for every int
		bb.put(time);
		bb.put(data);// image or command
		byte[] message = new byte[bb.capacity()];
		bb.get(message, 0, message.length);
		return message;

	}
/**
 * Checks if there was any motion detected and if so, then informs the client.
 * @throws IOException, if the command informing the client of the detected motion was not sent.
 */
	private synchronized void motionDetection() throws IOException {
		if (camera.motionDetected()) {
			movieMode = true;
			byte[] latestImgTime = new byte[AxisM3006V.TIME_ARRAY_SIZE];
			ByteBuffer bb = ByteBuffer.allocate(4);

			camera.getTime(latestImgTime, 0);

			os.write(packageData(COMMAND, 4, cameraNbr, latestImgTime, bb.putInt(1)
					.array()));
		} 

	}

}
