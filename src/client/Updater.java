package client;

public class Updater extends Thread {
	private ClientMonitor monitor;
	
	// check if there's a new image
	public Updater(ClientMonitor mon) {
		monitor = mon;
	}

	public void run() {
		while (!isInterrupted()) {
			monitor.checkUpdate(); //Check if new image has arrived
			//Here we know that its time to update the GUI with new image. So we should just refresh image in GUI. 
		}
	}
}
