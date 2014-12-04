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
				System.out.println(e2 + "got an interrupted exception client reader");
			}
			is = mon.getInputStream(serverIndex);

			while (mon.isConnected(serverIndex)) {
				int type = readInt(serverIndex);
				if (type == mon.IMAGE) {
					int size = readInt(serverIndex);
					int cameraNumber = readInt(serverIndex);

					byte[] temp = readByteArray(serverIndex, 8);
					ByteBuffer bb = ByteBuffer.wrap(temp);
					long timeStamp = bb.getLong();
					long delay = System.currentTimeMillis() - timeStamp;
					Image image = new Image(cameraNumber, timeStamp, delay,
							readByteArray(serverIndex, size));

					mon.setNewImage(true);
					mon.setUpdateGUI(true);

					mon.putImageToBuffer(image, serverIndex);
				} else if (type == mon.COMMAND) {
					int commandData = readInt(serverIndex);
					if (commandData != mon.MOVIE_MODE) {
						System.out
								.println("Client have recieved an invalid command");

					}
					mon.setNewCommand(true);
					mon.setUpdateGUI(true);

					mon.putCommandToUpdaterBuffer(commandData);
					mon.putCommandToClientWriter(1 - serverIndex, commandData);
				} else {
					System.out.println("You got a non existing type :D");
				}
			}
		}
	}

	private synchronized byte[] readByteArray(int serverIndex, int size) {
		byte[] message = new byte[size];
		int readBytes = 0;

		try {
			readBytes = is.read(message, 0, size);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (readBytes != size) {
			System.out
					.println("8 byte was not read from inputstream server reader");

		}
		return message;
	}

	private int readInt(int serverIndex) {
		byte[] temp = new byte[4];
		int readBytes = 0;
		try {
			readBytes = is.read(temp, 0, 4);

		} catch (IOException e) {
			System.out.println("Failed to read an int in client Reader");
			e.printStackTrace();
		}
		if (readBytes != 4) {
			System.out.println("Too few bytes were read by client reader");
		}
		// Konvertera till int
		ByteBuffer bb = ByteBuffer.wrap(temp);
		return bb.getInt(0);
	}
}
