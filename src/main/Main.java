package main;

import gui.GUI;
import se.lth.cs.eda040.fakecamera.AxisM3006V;
import server.Server;
import server.ServerMonitor;
import server.ServerReader;
import server.ServerWriter;
import client.Client;
import client.ClientMonitor;
import client.ClientReader;
import client.ClientWriter;
import client.SocketAddress;

public class Main {
	public static void main(String[] args) {

		/**
		 * Start up the client
		 */
		SocketAddress adr1 = new SocketAddress("argus-1.student.lth.se", 7897);
		SocketAddress adr2 = new SocketAddress("argus-2.student.lth.se", 7898);
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
		 * Start up one server
		 */

		AxisM3006V camera1 = new AxisM3006V();
		ServerMonitor serverMon1 = new ServerMonitor(7897,
				"argus-1.student.lth.se", 0, camera1);
		ServerReader serverReader1 = new ServerReader(serverMon1);
		ServerWriter serverWriter1 = new ServerWriter(serverMon1);
		serverReader1.start();
		serverWriter1.start();

		AxisM3006V camera2 = new AxisM3006V();
		ServerMonitor serverMon2 = new ServerMonitor(7898,
				"argus-2.student.lth.se", 1, camera2);
		ServerReader serverReader2 = new ServerReader(serverMon2);
		ServerWriter serverWriter2 = new ServerWriter(serverMon2);
		serverReader2.start();
		serverWriter2.start();

	}

}
