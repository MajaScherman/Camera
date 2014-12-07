package client;

import gui.GUI;

public class ClientMain {

	public static void main(String[] args) {
		if (args.length != 4) {
			System.out.println("The arguments to start the client are invalid. Please make sure to type arg1: hostaddress 1, arg2: port number to first host, arg3: hostaddess 2, arg4: port number to second host");
		} else {
			/**
			 * Start up the client
			 */
			SocketAddress adr1 = new SocketAddress(args[0], Integer.parseInt(args[1]));
			SocketAddress adr2 = new SocketAddress(args[2], Integer.parseInt(args[3]));
			SocketAddress[] addresses = new SocketAddress[2];
			addresses[0] = adr1;
			addresses[1] = adr2;
			ClientMonitor clientMon = new ClientMonitor(2);
			ClientReader cReader0 = new ClientReader(clientMon, 0);
			ClientReader cReader1 = new ClientReader(clientMon, 1);
			ClientWriter cWriter = new ClientWriter(clientMon, addresses, 2);

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
}
