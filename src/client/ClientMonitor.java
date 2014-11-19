package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientMonitor {
	private String command;
	private String image;
	private boolean syncMode;
	private boolean movieMode;
	private boolean updateGUI;
	private Socket[] socketArray;
	private SocketAddress[] socketAddresses;

	public ClientMonitor(int nbrOfSockets, SocketAddress[] socketAddr) {
		syncMode = false;
		movieMode = false;
		updateGUI = false;
		socketAddresses = socketAddr;
		socketArray = new Socket[nbrOfSockets];
	}

	/**
	 * Request to update the GUI, meaning an image has been sent and the update
	 * should be notified.
	 */
	public synchronized void updateRequest() {
		updateGUI = true;
		notifyAll();
	}

	/**
	 * Tells the updater to update the GUI when time is due.
	 */
	public synchronized void checkUpdate() {
		while (updateGUI == false) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// TODO do something
		updateGUI = false;
	}

	public void setResult() {
		// The reader sets the command, if one has been received, according to
		// the info received from the inputstream
		// from the cilent socket. String getLine(InputStream s)?
		// If image then the reader does not set the command. but sets the image
	}

	public String getToServerCommand() {
		// The writer gets the command set by the reader.
		// Combine with putLine(OutputStream s, String str)?
		return command;

	}

	public String getFromServerCommand() {
		// The writer gets the command set by the reader.
		// Combine with putLine(OutputStream s, String str)?
		return command;

	}

	public String getImage() {
		// image is set by the reader if an image is received. This method is
		// used by the updater which updates the GUI.
		// The update continously calls getImage() to check if there's a new
		// image.
		// How to distinguish from which camera the image comes from?
		return image;
	}

	public boolean checkSyncMode() {
		// Checks if the cameras are in sync
		// Updater uses this to check if they are synchronous and use this
		// knowledge when displaying the images
		return syncMode;

	}

	public synchronized boolean changeModeFlag() {
		// Should be called by the reader when one camera has changed to movie
		// mode.
		// This should trigger the writer to tell the other cameras to change to
		// movie mode as well.<z<
		movieMode = true;
		notifyAll();
		return movieMode;

	}

	/**
	 * This method recieves messages from the server.
	 * 
	 * @param server
	 *            The server to listen to starting from index 0
	 */
	public void listenToServer(int server) {
		try {

			// Establish connection
			// Server must be running before trying to connect
			String host = socketAddresses[server].getAddress();
			int port = socketAddresses[server].getPortNumber();
			socketArray[server] = new Socket(host, port);

			// Get input stream
			InputStream is = socketArray[server].getInputStream();

			byte[] data = new byte[8192];

			// Read header - read is blocking
			byte hi = (byte) is.read();
			// If read returns -1 then end-of-stream has been reached
			if (hi == -1)
				throw new IOException("End of stream");
			byte lo = (byte) is.read();
			if (lo == -1)
				throw new IOException("End of stream");

			// Calculate size of package
			// Byte is signed. & 0xFF creates an int from the byte bit pattern,
			// allowing
			// for interpretation of byte values in the range 0-254. Leave 255
			// as it is
			// used to indicate end-of-stream in read().
			int size = (hi & 0xFF) * 255 + (lo & 0xFF);

			// Read package
			int read = 0; // Number of read bytes so far
			while (read != size) {
				// Read bytes and put in data array until size bytes are read
				// Read returns number of bytes read <= size-read
				int n = is.read(data, read, size - read);
				if (n == -1)
					throw new IOException("End of stream");
				read += n;
			}

			// Verify that the package was received correctly
			// by looking at the last 100 bytes
			boolean ok = true;
			for (int j = 0; j < 100; j++) {
				if (data[size - 1 - j] != 100 - j)
					ok = false;
			}
			if (ok)
				System.out.printf("Client: Received package of size %d ok\n",
						size);
			else
				System.out.printf("Client: Did not receive package properly\n");

			// Close socket
			socketArray[server].close();

		} catch (IOException e) {
			// Occurs in read method of
			e.printStackTrace();
		}

	}

	/**
	 * This method writes data to the server.
	 */
	public void writeToServer() {
		try {
			while (movieMode == false) {

			}
			for (int server = 0; server < socketArray.length; server++) {
				// Increase the probability that the server is accepting
				// connections,
				// it is *still* a race condition though
				Thread.sleep(2000);

				// Establish connection
				// Server must be running before trying to connect
				String host = socketAddresses[server].getAddress();
				int port = socketAddresses[server].getPortNumber();
				socketArray[server] = new Socket(host, port);

				// Set socket to no send delay
				socketArray[server].setTcpNoDelay(true);

				// Get input stream
				InputStream is = socketArray[server].getInputStream();

				// Get output stream
				OutputStream os = socketArray[server].getOutputStream();

				// Send a random number of data packages of different sizes
				// Assume a header of two bytes for encoding the package size
				byte[] data = new byte[8192];
				int pkg = (int) (Math.random() * 90 + 10);
				for (int i = 0; i < pkg; i++) {

					// Randomize size of package
					int size = (int) (Math.random() * 8000 + 192);

					// Fill package with zeros, except the last 100 bytes
					byte count = 100;
					for (int j = size - 1; j >= 0; j--) {
						data[j] = count > 0 ? count-- : 0;
					}

					// Create header
					byte hi = (byte) (size / 255);
					byte lo = (byte) (size % 255);

					// Send header
					os.write(hi);
					os.write(lo);

					// Send package
					os.write(data, 0, size);

					// Flush data
					os.flush();

					// Wait for acknowledgment - read is blocking
					int ack = is.read();
					// If read returns -1 then end-of-stream has been reached
					if (ack == -1)
						throw new IOException("End of stream");
					System.out
							.printf("Client: Received ack from connection %d for package %d\n",
									server, i);
				}

				// Close the socket, i.e. abort the connection
				socketArray[server].close();
			}

		} catch (UnknownHostException e) {
			// Occurs if the socket cannot find the host
			e.printStackTrace();
		} catch (IOException e) {
			// Occurs if there is an error trying to connect to the host,
			// or there is an error during the call to the write method.
			//
			// Example: the connection is closed on the server side, but
			// the client is still trying to write data.
			e.printStackTrace();
		} catch (InterruptedException e) {
			// Occurs if the sleep method is interrupted
			e.printStackTrace();
		}
	}
}
