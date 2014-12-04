package server;

public class MotionDetector extends Thread {
	private ServerMonitor mon;

	public MotionDetector(ServerMonitor monitor) {
		this.mon = monitor;
	}

	public void run() {
		while (!isInterrupted()) {
			mon.motionDetection();
		}
	}
}
