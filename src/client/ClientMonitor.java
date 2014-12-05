package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientMonitor {

	/**
	 * Attributes for updating GUI
	 */
	private boolean updateGUI;
	private boolean newImage; // The updater checks if a new image has arrived
								// in data
	private boolean newMode; // The updater checks if a new mode has arrived in
	private boolean forceMode;							// data
	private int syncMode;
	private boolean movieMode;
	/**
	 * Attributes for connecting
	 */
	private boolean[] isConnected;

	private InputStream[] inputStream;
	/**
	 * Header attributes
	 */

	public static final int IMAGE = 0;
	public static final int COMMAND = 1;

	/**
	 * Data in packets
	 */
	public static final int CLOSE_CONNECTION = 0;
	public static final int OPEN_CONNECTION = 1;
	public static final int MOVIE_MODE = 2;
	public static final int IDLE_MODE = 3;
	public static final int ASYNCHRONIZED = 4;
	public static final int SYNCHRONIZED = 5;
	public static final int AUTO = 6;
	public static final int FORCED = 7;

	/**
	 * Attributes for handling images
	 */
	private ImageBuffer imageBufferServer1, imageBufferServer2; // An image ring
																// buffer
																// containing
																// 125 Images

	private boolean imageS1LastTime;
	/**
	 * Attributes for handling commands in updater
	 */
	private CommandBuffer updaterBuffer;

	/**
	 * Attributes for handling commands in writer
	 */
	private CommandBuffer writerBufferServer1, writerBufferServer2;
	private boolean server1LastTime; // Is used for fair checking of what server
										// to write to.

	public static final int IMAGE_SIZE = 640 * 480 * 3; // REQ 7
	public static final int IMAGE_BUFFER_SIZE = 125; // Supports 125 images,
														// which is 5 seconds of
														// video in 25 fps
	public static final int COMMAND_BUFFER_SIZE = 125; // Supports 125 images,
	// which is 5 seconds of
	// video in 25 fps
	public static final int NUMBER_OF_CAMERAS = 2;


	public ClientMonitor(int nbrOfSockets) {
		isConnected = new boolean[nbrOfSockets];

		inputStream = new InputStream[nbrOfSockets];

		writerBufferServer1 = new CommandBuffer(COMMAND_BUFFER_SIZE);
		writerBufferServer2 = new CommandBuffer(COMMAND_BUFFER_SIZE);
		updaterBuffer = new CommandBuffer(COMMAND_BUFFER_SIZE);
		imageBufferServer1 = new ImageBuffer(IMAGE_BUFFER_SIZE);
		imageBufferServer2 = new ImageBuffer(IMAGE_BUFFER_SIZE);
		updateGUI = false;
		imageS1LastTime = false;
		syncMode = 0;
	}


	public synchronized void waitForConnection(int serverIndex)
			throws InterruptedException {
		while (!isConnected[serverIndex]) {
			wait();
		}

	}

	public synchronized void putCommandToClientWriter(int serverIndex,
			int command) {
		if (serverIndex == 0) {
			writerBufferServer1.putCommandToBuffer(command);
		} else {
			writerBufferServer2.putCommandToBuffer(command);
		}
		notifyAll();
	}

	public synchronized void putCommandToUpdaterBuffer(int com) {
		updaterBuffer.putCommandToBuffer(com);
	}

	public synchronized int getCommandFromUpdaterBuffer() {
		int com = updaterBuffer.getCommandFromBuffer();
		return com;
	}

	public synchronized void putImageToBuffer(Image image, int serverIndex) {
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

			if (imageBufferServer1.getNbrOfImagesInBuffer() > 0) {
				image = imageBufferServer1.getImageFromBuffer();
			} else {
				image = imageBufferServer2.getImageFromBuffer();
			}
		}
		notifyAll();
		return image;
	}

	public synchronized long SyncMode(Image imageC1, Image imageC2) {

		return 0;
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
		if (newMode) {
			newMode = false;
			notifyAll();
			return COMMAND;
		}else if (newImage) {
			newImage = false;
			notifyAll();
			return IMAGE;
		}else {
			notifyAll();
			throw new Exception("Check update method is wrong");
		}
	}

	public synchronized InputStream getInputStream(int serverIndex) {
		return inputStream[serverIndex];
	}

	public synchronized boolean isConnected(int serverIndex) {
		return isConnected[serverIndex];
	}

	public synchronized int[] waitForWriterInput() throws InterruptedException {
		while (writerBufferServer1.getNbrOfCommandsInBuffer() <= 0
				&& writerBufferServer2.getNbrOfCommandsInBuffer() <= 0) {
			wait();
		}
		int command;
		int serverIndex;
		if (server1LastTime) {// If server 1 was checked first last time
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
		server1LastTime = !server1LastTime;
		int[] temp = { command, serverIndex };
		return temp;
	}

	

	public synchronized void setNewImage(boolean b) {
		newImage = b;
		notifyAll();
	}

	public synchronized void setUpdateGUI(boolean b) {
		updateGUI = b;
		notifyAll();
	}

	public synchronized void setNewCommand(boolean b) {
		newMode = b;
		notifyAll();
	}

	public synchronized boolean isOnlyOneImage() {

		return !(imageBufferServer1.getNbrOfImagesInBuffer() > 0 && imageBufferServer2
				.getNbrOfImagesInBuffer() > 0);
	}

	public synchronized Image[] getImagesFromBuffers() {
		Image[] images = new Image[2];
		images[0] = imageBufferServer1.getImageFromBuffer();
		images[1] = imageBufferServer2.getImageFromBuffer();
		return images;
	}

	public synchronized void setIsConnected(int serverIndex, boolean status) {
		isConnected[serverIndex] = status;
		notifyAll();
	}

	public synchronized void createServerCommandBuffer(int serverIndex) {
		if (serverIndex == 0) {
			writerBufferServer1 = new CommandBuffer(COMMAND_BUFFER_SIZE);
		} else {
			writerBufferServer2 = new CommandBuffer(COMMAND_BUFFER_SIZE);

		}
		notifyAll();
	}


	public synchronized void setInputStream(InputStream is, int serverIndex) {
		inputStream[serverIndex] = is;
		notifyAll();
		
	}


	public synchronized int getSyncMode() {
		
		return syncMode;
	}


	public synchronized void setSyncMode(int mode) {
		syncMode = mode;
		notifyAll();
		
	}


	public synchronized boolean getForceMode() {
		return forceMode;
	}


	public synchronized void setForceMode(boolean b) {
		forceMode = b;
		notifyAll();
	}


	public synchronized boolean getMovieMode() {
		return movieMode;
	}
	
	public synchronized void setMovieMode(boolean mode){
		movieMode = mode;
		notifyAll();
	}

}
