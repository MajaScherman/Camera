package server;

import java.io.IOException;

import se.lth.cs.eda040.fakecamera.AxisM3006V;

public class Server{
	
	public static void main(String[] args) {
		AxisM3006V camera = new AxisM3006V();
		ServerMonitor servMon = new ServerMonitor(7897,"argus-1.student.lth.se", 0,camera);
		ServerReader servReader = new ServerReader(servMon);
		ServerWriter servWriter = new ServerWriter(servMon.getServerSocket(),servMon, camera);
	}

}
