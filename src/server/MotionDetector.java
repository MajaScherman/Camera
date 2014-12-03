package server;

public class MotionDetector extends Thread {
	private ServerMonitor mon;

	public MotionDetector(ServerMonitor monitor) {
		this.mon = monitor;
	}

	public void run() {
		while (!isInterrupted()) {
			try {
				sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mon.motionDetection();
		}
	}
}
