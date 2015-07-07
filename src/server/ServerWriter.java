package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

import se.lth.cs.eda040.proxycamera.AxisM3006V;
//import se.lth.cs.eda040.fakecamera.AxisM3006V;


public class ServerWriter extends Thread {
	private AxisM3006V camera;
	private OutputStream os;
	private ServerMonitor mon;
	private byte[] image;
	private byte[] time;
	private Socket clientSocket;
	private ServerSocket serverSocket;
	private InputStream is;
	private int offset;


	@SuppressWarnings("static-access")
	public ServerWriter(ServerMonitor mon, String hostAddress, int camPort,
			AxisM3006V camera,int clientPort, int offset) {
		this.mon = mon;
		this.camera = camera;
		this.offset = offset;
		camera.init();
		camera.setProxy(hostAddress, camPort);
		camera.connect();

		image = new byte[camera.IMAGE_BUFFER_SIZE];
		time = new byte[camera.TIME_ARRAY_SIZE];

		try {
			serverSocket = new ServerSocket(clientPort);
		} catch (IOException e) {
			System.out
					.println("ServerSocket could not be created in ServerMonitor constructor");
			e.printStackTrace();
		}
	}

	public void run() {
		while (!isInterrupted()) {
			try {
				clientSocket = serverSocket.accept();

				is = clientSocket.getInputStream();
				os = clientSocket.getOutputStream();
				mon.setInputStream(is);
				mon.setConnectionOpened();

				while (mon.isConnected()) {
					if (camera.motionDetected()) {
						if(mon.trySetMovieMode(true)){
							ByteBuffer bb = ByteBuffer.allocate(8);
							bb.putInt(0,ServerMonitor.COMMAND);
							bb.putInt(4,ServerMonitor.MOVIE_MODE);
							os.write(bb.array(), 0, 8);
							os.flush();
						}
					}
					if (mon.isReadyToSendImage()) {
						if(mon.checkIfHasChanged()){
							ByteBuffer bb = ByteBuffer.allocate(8);
							bb.putInt(0,ServerMonitor.COMMAND);
							bb.putInt(4,ServerMonitor.MOVIE_MODE);
							os.write(bb.array(), 0, 8);
							os.flush();
						}
						try {
							sleep(offset);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						int length = camera.getJPEG(image, 0);
						camera.getTime(time, 0);
						if (length > 0) {
							try {
								// Packaging data
								byte[] packet = mon.packageImage(length, time,
										image);
								os.write(packet, 0, packet.length);
								os.flush();
								mon.updateLastTimeSent();

							} catch (SocketException e) {

								clientSocket = serverSocket.accept();
								is = clientSocket.getInputStream();
								os = clientSocket.getOutputStream();

								mon.setInputStream(is);
								mon.setConnectionOpened();

							}
						}
					}
				}
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
