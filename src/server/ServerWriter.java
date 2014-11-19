package server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import se.lth.cs.eda040.fakecamera.AxisM3006V;

public class ServerWriter extends Thread {
	private ServerSocket serverSocket;
	private ServerMonitor mon;
	private Socket clientSocket;
	private volatile InputStream is;
	private volatile OutputStream os;
	private AxisM3006V camera;
	private int port;
	private byte[] image;
	private byte[] imageTime;
	private int length;

	public ServerWriter(ServerSocket serverSocket, ServerMonitor mon,
			AxisM3006V camera) {
		this.serverSocket = serverSocket;
		this.mon = mon;
		this.camera = camera;
	}

	public void run() {
		while (true) {
			// metakod
			// kolla om vi fått bild
			// ev.uppdater mon
			// skicka bild till client
			// om upptäcka movement
			// så uppdatera mon till moviemode
			// sen meddela klienten att vi har fått movie mode
			length = camera.getJPEG(image, 0);// 0 is our offset
			if (length != 0) {
				// ev.uppdater mon
				// skicka bild till client
				camera.getTime(imageTime, 0);
				byte[] message = mon.packageImage(0, length,
						mon.getCameraNbr(), imageTime, image);
				mon.sendPackage(message);
				
			}
			if (camera.motionDetected()) {
				mon.setMovieMode(true);
			}
		}

	}

}
