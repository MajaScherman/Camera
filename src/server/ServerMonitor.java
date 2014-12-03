package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import se.lth.cs.eda040.fakecamera.AxisM3006V; // Provides AxisM3006V
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
	 * Attributes for camera
	 */
	private AxisM3006V camera;
	private int cameraNbr;

	/**
	 * Attributes for commands
	 */
	private boolean movieMode;
	private boolean motionDetected;
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
		movieMode = isConnected = motionDetected = false;
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
				byte[] message = getImage();
				if (message != null) {
					os.write(message, 0, message.length);
					os.flush();
					lastTimeSentImg = System.currentTimeMillis();
					System.out.println("hej whatever");
				} 
				System.out.println("The server connected to the client socket");
				notifyAll();
			} catch (IOException e) {
				System.out.println("Could not establish connection" + e);
			}
		}
	}

	public synchronized void closeConnection() throws SocketException {
		if (!isConnected) {
			System.out.println("Connection is already closed");
		} else {
			try {
				isConnected = false;
				notifyAll();
				clientSocket.close();
				//serverSocket.close();
				System.out.println("soon to send our SPACESHIP");
			throw new SocketException("The connection is closed");
			} catch (IOException e) {
				System.out.println("SPACESHIP");
				throw new SocketException("the connection is closed and IOException: " + e);
			}
		}
	}

	/**
	 * Reads the message received from the client. The package only contains 1
	 * int to represent commands. Therefore the size of the package is only 4
	 * bytes.
	 */

	public synchronized void readAndRunCommand() throws SocketException{
		byte[] message = new byte[MESSAGE_SIZE];
		int bytesLeft = MESSAGE_SIZE;
		int tempIndex = 0;
		while (bytesLeft > 0) {
			// läs en byte
			int read;
			try {
				read = is.read();
				// lägg i message
				message[tempIndex] = (byte) read;
				tempIndex++;
				bytesLeft--;
			} catch (IOException e) {
				System.out
						.println("error in readandruncommand method server side"
								+ e);
				e.printStackTrace();
			}
		}

		// Konvertera till int
		ByteBuffer bb = ByteBuffer.wrap(message);
		System.out.println("Last header serverside int was: " + bb.getInt(0));
		int command = bb.getInt(0);
		System.out.println("Command server side was: " + command);
		runCommand(command);
		notifyAll();
	}

	/**
	 * Interprets the command and performs the correct actions.
	 */
	private synchronized void runCommand(int command) throws SocketException {
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
	public synchronized void write() {
		try {
			while (!isConnected) {
				wait();
			}
			if (motionDetected) {
				motionIsDetected();
			}
			byte[] message;
			
			if (movieMode) {
				message = getImage();
				System.out.println("got an image");
				if (message != null) {
					System.out
							.println("message is an image and not null in movie mode");
					os.write(message, 0, message.length);//TODO gives an error if disconnected with wrong timing socket write error
					os.flush();
					System.out
							.println("printed image to output stream in movie mode");
					lastTimeSentImg = System.currentTimeMillis();

					System.out.println("time for sending image:"
							+ lastTimeSentImg);
				} else {
					System.out.println("could not fetch an image");
				}
			} else {
				long timeUntilAllowed = lastTimeSentImg + 5000;
				long diff = timeUntilAllowed - System.currentTimeMillis();
				while (!motionDetected && diff >= 0) {
					System.out.println("waiting diff sek" + diff);
					wait(diff);
					diff = timeUntilAllowed - System.currentTimeMillis();

				}
				message = getImage();
				if (message != null) {
					os.write(message, 0, message.length);
					os.flush();
					lastTimeSentImg = System.currentTimeMillis();

				} else {
					System.out
							.println("write in server failed, message was null in idle mode");
				}

			}
		} catch (SocketException e) {
			System.out.println(e + " socket exception");
			establishConnection();
		}catch(Exception e){
			System.out.println("Write failed");
			e.printStackTrace();
			
		}
		//notifyAll();
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
		System.out.println("Längden på JPEG serversida: " + length);
		if (length != 0) {
			camera.getTime(imageTime, 0); // second param is offset
			System.out.println("Cameratid serversida: "
					+ Arrays.toString(imageTime));
			byte[] message = packageData(ClientMonitor.IMAGE, length,
					cameraNbr, imageTime, image);
			System.out.println("packagedata färdig serversida: ");
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
	 * @param length
	 *            , size of image.
	 * @param cameraNbr
	 *            , Tells from which camera the package is sent.
	 * @param time
	 *            , At which time the image was taken.
	 * @param image
	 * 
	 * @return
	 */
	private synchronized byte[] packageData(int type, int length,
			int cameraNbr, byte[] time, byte[] image) {
		System.out.println("vi packeterar datan här serversida");
		ByteBuffer bb = ByteBuffer.allocate(12 + AxisM3006V.TIME_ARRAY_SIZE
				+ length);
		bb.putInt(type);
		bb.putInt(length);
		bb.putInt(cameraNbr);// 4 bytes for every int
		bb.put(time);
		bb.put(image, 0, length);
		byte[] message = null;
		if (bb.hasArray()) {
			message = bb.array();
		} else {
			System.out
					.println("byte buffer does not have an array in package data server side ja");
		}
		System.out.println("slutet av packagedata serversida: ");
		return message;

	}

	public synchronized void motionDetection() {
		System.out.println("do we detect a motion???");
		while (!isConnected) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		if (camera.motionDetected()) {
			System.out.println("detected motion");
			motionDetected = true;
			notifyAll();
		}
	}

	/**
	 * Checks if there was any motion detected and if so, then informs the
	 * client.
	 * 
	 * @throws IOException
	 */
	private synchronized void motionIsDetected() throws IOException {
		System.out.println("motion is detected");
		if (!movieMode) {
			movieMode = true;
			ByteBuffer bb = ByteBuffer.allocate(8);
			bb.putInt(ClientMonitor.COMMAND);
			bb.putInt(ClientMonitor.MOVIE_MODE);
			os.write(bb.array(), 0, 8);
			os.flush();
		}
		motionDetected = false;
		notifyAll();
	}

	
}
