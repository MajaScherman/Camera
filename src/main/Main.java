package main;

import gui.GUI;
import se.lth.cs.eda040.fakecamera.AxisM3006V;
import server.ServerMonitor;
import server.ServerReader;
import server.ServerWriter;
import client.ClientMonitor;
import client.ClientReader;
import client.ClientWriter;
import client.SocketAddress;
import client.Updater;

public class Main {
	public static void main(String[] args) {
		/**
		 * Start up two servers
		 */

		AxisM3006V camera1 = new AxisM3006V();
		ServerMonitor serverMon1 = new ServerMonitor(7897, 0);
		ServerReader serverReader1 = new ServerReader(serverMon1);
		ServerWriter serverWriter1 = new ServerWriter(serverMon1, "localhost",
				7897, camera1,0);
		serverWriter1.start();
		serverReader1.start();

		AxisM3006V camera2 = new AxisM3006V();
		ServerMonitor serverMon2 = new ServerMonitor(7898, 1);
		ServerReader serverReader2 = new ServerReader(serverMon2);
		ServerWriter serverWriter2 = new ServerWriter(serverMon2, "localhost",
				7898, camera2,1);
		serverWriter2.start();
		serverReader2.start();

		/**
		 * Start up the client
		 */
		SocketAddress adr1 = new SocketAddress("localhost", 7897);
		SocketAddress adr2 = new SocketAddress("localhost", 7898);
		SocketAddress[] addresses = new SocketAddress[2];
		addresses[0] = adr1;
		addresses[1] = adr2;
		ClientMonitor clientMon = new ClientMonitor(2, addresses);
		ClientReader cReader0 = new ClientReader(clientMon, 0);
		ClientReader cReader1 = new ClientReader(clientMon, 1);
		ClientWriter cWriter = new ClientWriter(clientMon);

		cReader0.start();
		cReader1.start();
		cWriter.start();

		/**
		 * Start up the GUI
		 */

		GUI gui = new GUI(clientMon);

		/**
		 * Start up the updater
		 */
		Updater updater = new Updater(clientMon, gui);
		updater.start();

	}

}
