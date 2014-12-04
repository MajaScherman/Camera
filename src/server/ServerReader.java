package server;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class ServerReader extends Thread {

	private ServerMonitor mon;
	private InputStream is;

	public ServerReader(ServerMonitor mon) {
		this.mon = mon;

	}

	/**
	 * Reads the message received from the client. The package only contains 1
	 * int to represent commands. Therefore the size of the package is only 4
	 * bytes.
	 * 
	 * @throws Exception
	 */

	public void run() {
		while (!isInterrupted()) {
			// mon.establishConnection();
			try {
				mon.waitForConnection();
				is = mon.getInputStream();
			} catch (InterruptedException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				System.out.println("Interrupted while waiting for connection, in serverReader");
			}
			while (mon.isConnected()) {

				byte[] message = new byte[4];
				int readBytes = 0;

				try {
					readBytes = is.read(message, 0, 4);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				if (readBytes != 4) {
					System.out.println("4 byte was not read from inputstream server reader");
		
				}

				ByteBuffer bb = ByteBuffer.wrap(message);
				try {
					mon.runCommand(bb.getInt(0));
				} catch (SocketException e) {
					e.printStackTrace();
					try {
						mon.waitForConnection();
						is = mon.getInputStream();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						System.out.println("Interrupted while waiting for connection, in serverReader");
					}
				}

			}
		}
	}
}
