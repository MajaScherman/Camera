package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

import client.ClientMonitor;
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
	private long lastTimeSentImg;

	// Hopefully its ok to add 3*4 for the type, cameraNbr, and size.
	public static int BUFFER_LENGTH = AxisM3006V.IMAGE_BUFFER_SIZE
			+ AxisM3006V.TIME_ARRAY_SIZE + 4 * 3;
	public static int MESSAGE_SIZE = 4;

	public ServerMonitor(int port, String hostAddress, int cameraNbr,
			AxisM3006V camera) {
		this.cameraNbr = cameraNbr;
		this.camera = camera;
		camera.init();
		camera.setProxy(hostAddress, port);

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

	public synchronized ServerSocket getServerSocket() {
		return serverSocket;
	}

	public synchronized void establishConnection() {
		if (isConnected) {
			System.out.println("Server connection is already established");
		} else {
			try {
				clientSocket = serverSocket.accept();// blocking until
														// connection available
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
				notifyAll();
			} catch (IOException e) {
				System.out.println("Could not close connection");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Reads the message received from the client. The package only contains 1
	 * int to represent commands. Therefore the size of the package is only 4
	 * bytes.
	 */

	public synchronized void readAndRunCommand() {
		byte[] message = new byte[MESSAGE_SIZE];
		try {
			is.read(message);
		} catch (IOException e) {
			e.printStackTrace();

		}
		ByteBuffer bb = ByteBuffer.wrap(message);
		int command = bb.getInt();
		runCommand(command);
		notifyAll();
	}

	/**
	 * Interprets the command and performs the correct actions.
	 */
	private synchronized void runCommand(int command) {
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

	/**
	 * While connected images are fetched and sent to the client at the given
	 * speed depending on if the MovieMode is active or not. Checks if a motion
	 * was detected in the latest image, if so then MovieMode is activated and
	 * the client is informed.
	 */
	public void write() {
		if (!isConnected) {
			establishConnection();
		}

		byte[] message;
		if (movieMode) {
			message = getImage();
			if (message != null) {
				System.out.println("message is an image and not null");
				try {
					os.write(message);
				} catch (IOException e) {
					System.out.println("Message could not be sent, check servermonitor.write()");
					e.printStackTrace();
				}
				lastTimeSentImg = System.currentTimeMillis();
			} else {
				System.out.println("could not fetch an image");
			}
		} else {

			if (System.currentTimeMillis() - lastTimeSentImg >= 5000) {

				message = getImage();
				if (message != null) {
					try {
						os.write(message);
					} catch (IOException e) {
						System.out.println("Message could not be sent2");

						e.printStackTrace();
					}
					lastTimeSentImg = System.currentTimeMillis();
				}
			} else {
				long t = lastTimeSentImg + 5000;
				long diff = t - System.currentTimeMillis();
				if (diff > 0) {
					try {
						Thread.sleep(diff);
					} catch (InterruptedException e) {
						System.out.println("sleep failed");
						e.printStackTrace();
					}
					message = getImage();
					if (message != null) {
						try {
							os.write(message);
						} catch (IOException e) {
							System.out.println("Message could not be sent3");
							e.printStackTrace();
						}
						lastTimeSentImg = System.currentTimeMillis();
					}
				}
			}
			try {
				motionDetection();
			} catch (IOException e) {
				System.out.println("Motiondetection failed");
				e.printStackTrace();
			}
			notifyAll();
		}
	}

	/**
	 * Fetches the image from the camera, creates a package with a header and
	 * the image.
	 * 
	 * @return message, the complete message with header and image. If no image
	 *         was available then null is returned.
	 */
	private synchronized byte[] getImage() {
		byte[] image = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
		byte[] imageTime = new byte[AxisM3006V.TIME_ARRAY_SIZE];
		int length = camera.getJPEG(image, 0);
		if (length != 0) {
			camera.getTime(imageTime, 0);
			byte[] message = packageData(ClientMonitor.IMAGE, length,
					cameraNbr, imageTime, image);
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
		byte[] message = bb.array();
		return message;

	}

	/**
	 * Checks if there was any motion detected and if so, then informs the
	 * client.
	 * 
	 * @throws IOException
	 *             , if the command informing the client of the detected motion
	 *             was not sent.
	 */
	private synchronized void motionDetection() throws IOException {
		if (camera.motionDetected()) {
			movieMode = true;
			ByteBuffer bb = ByteBuffer.allocate(8);
			bb.putInt(ClientMonitor.COMMAND);
			bb.putInt(ClientMonitor.MOVIE_MODE);
			os.write(bb.array());
			notifyAll();
		}

	}

}
