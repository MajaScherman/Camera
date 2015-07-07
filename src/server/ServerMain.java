package server;

import se.lth.cs.eda040.proxycamera.AxisM3006V;

public class ServerMain {

	public static void main(String[] args) {
		if (args.length != 3) {
			System.out
					.println("The arguments to start the server are invalid. Please type in arg1:hostaddress to camera, arg2:port number to camera, arg3:port number to connect to the client. Also make sure the camera proxy is started");

		} else {
			// TODO flytta till default package for korskompileringen
			AxisM3006V camera = new AxisM3006V();
			ServerMonitor serverMon = new ServerMonitor(0);
			ServerReader serverReader = new ServerReader(serverMon);
			ServerWriter serverWriter = new ServerWriter(serverMon, args[0],
					Integer.parseInt(args[1]), camera, Integer.parseInt(args[2]), 0);// last para is
			// offset
			serverWriter.start();
			serverReader.start();
		}
	}
}
