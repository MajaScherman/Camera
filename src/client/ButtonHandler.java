package client;

import gui.GUI;
import gui.InfoPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class ButtonHandler implements ActionListener {
	private ClientMonitor mon;
	private InfoPanel infoPanel;

	public ButtonHandler(ClientMonitor mon, InfoPanel infoPanel) {
		this.mon = mon;
		this.infoPanel = infoPanel;
	}

	public void actionPerformed(ActionEvent evt) {
		String command = ((JButton) evt.getSource()).getActionCommand();
		try {
			switch (command) {
			case "IDLE":
				mon.sendMessageToServer(0, ClientMonitor.IDLE); // Notice that
																// we send in a
																// serverIndex 0,
																// but this is
																// however
																// irrelevant
																// since we send
																// the command
																// to all
																// servers
				infoPanel.setLabelText(2, "Idle Mode");
				break;
			case "MOVIE":
				mon.sendMessageToServer(0, ClientMonitor.MOVIE_MODE);
				infoPanel.setLabelText(2, "Movie Mode");
				break;
			case "CLOSE CONNECTION 1":
				JButton temp1 = (JButton) evt.getSource();
				temp1.setText("OPEN CONNECTION 1");
				mon.sendMessageToServer(0, ClientMonitor.CLOSE_CONNECTION);
				break;
			case "CLOSE CONNECTION 2":
				JButton temp2 = (JButton) evt.getSource();
				temp2.setText("OPEN CONNECTION 2");
				mon.sendMessageToServer(1, ClientMonitor.CLOSE_CONNECTION);
				break;
			case "OPEN CONNECTION 1":
				JButton temp3 = (JButton) evt.getSource();
				temp3.setText("CLOSE CONNECTION 1");
				mon.sendMessageToServer(0, ClientMonitor.OPEN_CONNECTION);
				break;
			case "OPEN CONNECTION 2":
				JButton temp4 = (JButton) evt.getSource();
				temp4.setText("CLOSE CONNECTION 2");
				mon.sendMessageToServer(1, ClientMonitor.OPEN_CONNECTION);
				break;
			case "SYNCHRONIZED":
				System.out.println("sync");
				break;
			case "ASYNCHRONIZED":
				System.out.println("async");
				break;
			default:
				System.out
						.println("Something went terribly wrong when you pressed a button, please do not press any buttons you noob");
				break;
			}
		} catch (Exception e) {
			System.out
					.println("Something went wrong with buttonhandler sending commands.");
			e.printStackTrace();
		}
	}
}
