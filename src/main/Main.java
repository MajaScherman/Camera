package main;

import server.ServerMain;
import client.ClientMain;
//import se.lth.cs.eda040.fakecamera.AxisM3006V;

public class Main {
	public static void main(String[] args) {
		//Start up server 1
		String[] argumentsToS1 = new String[] {"argus-6.student.lth.se","8888","5555"};
		ServerMain.main(argumentsToS1);
		//Start up server 2
		String[] argumentsToS2 = new String[] {"argus-6.student.lth.se","8888","6666"};
		ServerMain.main(argumentsToS2);
		
		//Start up Client
		String[] argumentsToC = new String[] {"localhost","5555","localhost","6666"};
		ClientMain.main(argumentsToC);
	}

}
