package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class ClientWriter extends Thread {
	private ClientMonitor mon;
	private OutputStream[] os;

	private Socket[] socket;
	private SocketAddress[] socketAddress;

	private InputStream[] inputStream;

	public ClientWriter(ClientMonitor mon, SocketAddress[] socketAddr,
			int nbrOfSockets) {
		this.mon = mon;

		socket = new Socket[nbrOfSockets];
		socketAddress = socketAddr;
		inputStream = new InputStream[nbrOfSockets];
		os = new OutputStream[nbrOfSockets];
	}

	public void run() {
		while (!isInterrupted()) {
			try {
				int[] commandAndServerIndex = mon.waitForWriterInput();
				int command = commandAndServerIndex[0];
				int serverIndex = commandAndServerIndex[1];
				switch (command) {
				case ClientMonitor.CLOSE_CONNECTION:
					if (mon.isConnected(commandAndServerIndex[1])) {
						byte[] bytes = ByteBuffer.allocate(4)
								.putInt(command, 0).array();
						os[serverIndex].write(bytes, 0, 4);
						os[serverIndex].flush();
						disconnectToServer(serverIndex);
					}
					break;
				case ClientMonitor.OPEN_CONNECTION:
					if (!mon.isConnected(serverIndex)) {
						connectToServer(serverIndex);
					}
					break;
				case ClientMonitor.MOVIE_MODE:
					byte[] bytes1 = ByteBuffer.allocate(4).putInt(0, command)
							.array();
					for (int i = 0; i < 2; i++) {
						if (mon.isConnected(i)) {
							os[i].write(bytes1, 0, 4);
							os[i].flush();
						}
					}
					break;

				case ClientMonitor.IDLE_MODE:
					byte[] bytes2 = ByteBuffer.allocate(4).putInt(0, command)
							.array();
					for (int i = 0; i < 2; i++) {
						if (mon.isConnected(i)) {

							os[i].write(bytes2, 0, 4);
							os[i].flush();
						}
					}
					break;
				case ClientMonitor.AUTO:
					byte[] bytes3 = ByteBuffer.allocate(4).putInt(0, command)
							.array();
					for (int i = 0; i < 2; i++) {
						if (mon.isConnected(i)) {

							os[i].write(bytes3, 0, 4);
							os[i].flush();
						}
					}
					break;
				case ClientMonitor.FORCED:
					byte[] bytes4 = ByteBuffer.allocate(4).putInt(0, command)
							.array();
					for (int i = 0; i < 2; i++) {
						if (mon.isConnected(i)) {

							os[i].write(bytes4, 0, 4);
							os[i].flush();
						}
					}
					break;
				default:
					System.out
							.println("Got an unexpected command in client writer");
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e1) {
				System.out
						.println("Got interrupted while waiting for writer input");
				e1.printStackTrace();
			}catch(Exception e2){
				e2.printStackTrace();
			}

		}
	}

	private void connectToServer(int serverIndex) throws Exception {
		if (!(serverIndex >= 0 && serverIndex < socket.length)) {
			
			throw new Exception("The server index is out of range, "
					+ "please give a value between 0 and " + socket.length
					+ (-1));
		} else if (mon.isConnected(serverIndex)) {
			System.out.println("The server is already connected");
		} else {
			// Establish connection
			// Server must be running before trying to connect
			String host = socketAddress[serverIndex].getHost();
			int port = socketAddress[serverIndex].getPortNumber();
			try {
				socket[serverIndex] = new Socket(host, port);
				// Set socket to no send delay
				socket[serverIndex].setTcpNoDelay(true);
				// Get input stream
				inputStream[serverIndex] = socket[serverIndex].getInputStream();
				// Get output stream
				mon.setInputStream(inputStream[serverIndex], serverIndex);
				os[serverIndex] = socket[serverIndex].getOutputStream();
				mon.setIsConnected(serverIndex, true);
				mon.createServerCommandBuffer(serverIndex);

				System.out.println("Server connection with server "
						+ serverIndex + " established");
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Disconnects to the server
	 * 
	 * @param serverIndex
	 *            the index of the server one wants to disconnect to
	 * @throws Exception 
	 */

	private synchronized void disconnectToServer(int serverIndex) throws Exception {
		if (!(serverIndex >= 0 && serverIndex < socket.length)) {
		
			throw new Exception("The server index is out of range, "
					+ "please give a value between 0 and " + socket.length);
		} else if (!mon.isConnected(serverIndex)) {
			System.out.println("The server with index " + serverIndex
					+ " is not connected");
		} else {
			// Close the socket, i.e. abort the connection
			try {
				socket[serverIndex].close();
				mon.setIsConnected(serverIndex, false);

				notifyAll();
				System.out.println("Disconnected to server" + serverIndex
						+ " successfully");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
