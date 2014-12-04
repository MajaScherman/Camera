package server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import client.ClientMonitor;
import se.lth.cs.eda040.fakecamera.AxisM3006V;

public class ServerWriter extends Thread {
	private AxisM3006V camera;
	private OutputStream os;
	private ServerMonitor mon;
	private int cameraNbr;
	byte[] image;
	byte[] time;

	public ServerWriter(ServerMonitor mon, String hostAddress, int port,
			AxisM3006V camera, int cameraNbr) {
		this.mon = mon;
		this.camera = camera;
		this.cameraNbr = cameraNbr;
		camera.init();
		camera.setProxy(hostAddress, port);
		image = new byte[camera.IMAGE_BUFFER_SIZE];
		time = new byte[camera.TIME_ARRAY_SIZE];
	}

	public void run() {
		while (!isInterrupted()) {

			mon.openConnection();
			os = mon.getOutputStream();
			while (mon.isConnected()) {
				if (camera.motionDetected()) {
					mon.setMovieMode(true);
					ByteBuffer bb = ByteBuffer.allocate(8);
					bb.putInt(ClientMonitor.COMMAND);
					bb.putInt(ClientMonitor.MOVIE_MODE);
					try {
						os.write(bb.array(), 0, 8);
						os.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("failed to write in serverWriter");
					}
				}
				if (mon.isReadyToSendMessage()) {
					int length = camera.getJPEG(image, 0);
					camera.getTime(time, 0);
					if (length > 0) {
						try {
							// Packaging data
							ByteBuffer bb = ByteBuffer.allocate(12
									+ AxisM3006V.TIME_ARRAY_SIZE + length);
							bb.putInt(ClientMonitor.IMAGE);
							bb.putInt(length);
							bb.putInt(cameraNbr);// 4 bytes for every int
							bb.put(time);
							bb.put(image, 0, length);
							byte[] message = bb.array();

							os.write(message, 0, message.length);
							os.flush();
							mon.updateLastTimeSent();

						} catch (SocketException e) {
							mon.openConnection();
							os = mon.getOutputStream();
						} catch (IOException e) {
							System.out.println("Failed to write to output stream in serverwriter");
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
