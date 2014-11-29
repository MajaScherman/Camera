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
	private boolean terminated; // temp variable to avoid red repo

	public ServerWriter(ServerSocket serverSocket, ServerMonitor mon,
			AxisM3006V camera) {
		this.serverSocket = serverSocket;
		this.mon = mon;
		this.camera = camera;
		terminated = false; // temp variable to avoid red repo
	}

	public void run() {
		while (!isInterrupted()) { //
			mon.write();
		}
	}
}
