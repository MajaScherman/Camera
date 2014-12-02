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
		String actionCommand = ((JButton) evt.getSource()).getActionCommand();
		try {
			switch (actionCommand) {
			case "IDLE":
				mon.putCommandToWriter(0, ClientMonitor.IDLE_MODE); // Notice that
				mon.putCommandToWriter(1, ClientMonitor.IDLE_MODE);												// we send in a
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
				mon.putCommandToWriter(0, ClientMonitor.MOVIE_MODE);
				mon.putCommandToWriter(1, ClientMonitor.MOVIE_MODE);
				infoPanel.setLabelText(2, "Movie Mode");
				break;
			case "CLOSE CONNECTION 1":
				JButton temp1 = (JButton) evt.getSource();
				temp1.setText("OPEN CONNECTION 1");
				temp1.setActionCommand("OPEN CONNECTION 1");
				mon.putCommandToWriter(0, ClientMonitor.CLOSE_CONNECTION);
				break;
			case "CLOSE CONNECTION 2":
				JButton temp2 = (JButton) evt.getSource();
				temp2.setText("OPEN CONNECTION 2");
				temp2.setActionCommand("OPEN CONNECTION 2");
				mon.putCommandToWriter(1, ClientMonitor.CLOSE_CONNECTION);
				break;
			case "OPEN CONNECTION 1":
				JButton temp3 = (JButton) evt.getSource();
				temp3.setText("CLOSE CONNECTION 1");
				temp3.setActionCommand("CLOSE CONNECTION 1");
				mon.putCommandToWriter(0, ClientMonitor.OPEN_CONNECTION);
				break;
			case "OPEN CONNECTION 2":
				JButton temp4 = (JButton) evt.getSource();
				temp4.setText("CLOSE CONNECTION 2");
				temp4.setActionCommand("CLOSE CONNECTION 2");
				mon.putCommandToWriter(1, ClientMonitor.OPEN_CONNECTION);
				break;
			case "SYNCHRONIZED":
				//TODO FIX Sync modes
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
