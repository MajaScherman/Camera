package client;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class ClientReader extends Thread {
	private ClientMonitor mon;
	private int serverIndex;
	private InputStream is;

	/**
	 * The reader reads incoming data on the client side.
	 * 
	 * @param m
	 *            The monitor for the client
	 * @param server
	 *            the number of the server that the reader handles
	 */
	public ClientReader(ClientMonitor m, int serverNbr) {
		mon = m;
		serverIndex = serverNbr;
	}

	public void run() {
		while (!isInterrupted()) {
			try {
				mon.waitForConnection(serverIndex);
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}
			is = mon.getInputStream(serverIndex);
			while (mon.isConnected(serverIndex)) {
				// Read header - read is blocking
				int type = readInt(serverIndex);
				System.out.println("Type is " + type);
				if (type == mon.IMAGE) {
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

					mon.setNewImage(true);
					mon.setUpdateGUI(true);

					mon.putImageToBuffer(image, serverIndex);// data
																// contains
																// the image
				} else if (type == mon.COMMAND) {
					int commandData = readInt(serverIndex);
					System.out.println("command data client side is"
							+ commandData);
					if (commandData != mon.MOVIE_MODE) {
						System.out.println("Client have recieved an invalid command");

					}
					mon.setNewImage(true);
					mon.setUpdateGUI(true);

					mon.putCommandToUpdaterBuffer(commandData);
					mon.putCommandToClientWriter(1 - serverIndex,
							commandData); // Send
					// movie
					// mode
					// to
					// the
					// other
					// server
				} else {
					System.out.println("You got a non existing type :D");
				}
			}
		}
	}

	private synchronized byte[] readByteArray(int serverIndex, int size) {
		byte[] temp = new byte[size];
		int bytesLeft = size;
		int tempIndex = 0;
		while (bytesLeft > 0) {
			// läs en byte
			int read;
			try {
				read = is.read();
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

	private int readInt(int serverIndex) {
		byte[] temp = new byte[4];

		// läs en byte
		int k = 0;
		try {
			k = is.read(temp, 0, 4);

		} catch (IOException e) {
			System.out.println("Failed to read an int in client Reader");
			e.printStackTrace();
		}
		if (k != 4) {
			System.out.println("Too few bytes were read by client reader");
		}
		// Konvertera till int
		ByteBuffer bb = ByteBuffer.wrap(temp);
		return bb.getInt(0);
	}
}
