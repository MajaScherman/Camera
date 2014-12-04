package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import main.Main;

public class ClientWriter extends Thread {
	private ClientMonitor mon;
	private OutputStream[] os;

	public ClientWriter(ClientMonitor mon) {
		this.mon = mon;
	}

	public void run() {
		while (!isInterrupted()) {
			// try {
			// mon.waitForConnection(0);
			// mon.waitForConnection(1);
			// } catch (InterruptedException e2) {
			// // TODO Auto-generated catch block
			// e2.printStackTrace();
			// }
			while (mon.isConnected(0) || mon.isConnected(1)) {
				os = mon.getOutPutStreams();
				try {
					int[] commandAndServerIndex = mon.waitForWriterInput(); // Command
																			// index
					// 0,
					// serverIndex
					// is
					// at
					// index
					// 1
					int command = commandAndServerIndex[0];
					int serverIndex = commandAndServerIndex[1];
					switch (command) {
					case ClientMonitor.CLOSE_CONNECTION:
						if (mon.isConnected(commandAndServerIndex[1])) {
							byte[] bytes = ByteBuffer.allocate(4)
									.putInt(command, 0).array();
							os[serverIndex].write(bytes, 0, 4);
							os[serverIndex].flush();
							mon.disconnectToServer(serverIndex);
						}
						break;
					case ClientMonitor.OPEN_CONNECTION:
						if (!mon.isConnected(serverIndex)) {
							mon.connectToServer(serverIndex);
						}
						break;
					case ClientMonitor.MOVIE_MODE:
						byte[] bytes1 = ByteBuffer.allocate(4)
								.putInt(command, 0).array();
						for (int i = 0; i < 2; i++) {
							if (mon.isConnected(i)) {
								os[i].write(bytes1, 0, 4);
								os[i].flush();
							}
						}
						mon.setMovieMode(true);
						break;

					case ClientMonitor.IDLE_MODE:
						byte[] bytes2 = ByteBuffer.allocate(4)
								.putInt(command, 0).array();
						for (int i = 0; i < 2; i++) {
							if (mon.isConnected(i)) {
								os[i].write(bytes2, 0, 4);

								os[i].flush();

							}
						}
						mon.setMovieMode(false);
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e1) {
					System.out
							.println("Got interrupted while waiting for writer input");
					e1.printStackTrace();
				}
			}
		}
	}
}
