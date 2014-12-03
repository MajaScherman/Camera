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
				JButton temp1 = (JButton) evt.getSource();
				temp1.setText("MOVIE MODE");
				temp1.setActionCommand("MOVIE");										
				infoPanel.setLabelText(2, "Idle Mode");
				break;
			case "MOVIE":
				mon.putCommandToWriter(0, ClientMonitor.MOVIE_MODE);
				mon.putCommandToWriter(1, ClientMonitor.MOVIE_MODE);
				JButton temp2 = (JButton) evt.getSource();
				temp2.setText("IDLE MODE");
				temp2.setActionCommand("IDLE");		
				infoPanel.setLabelText(2, "Movie Mode");
				break;
			case "CLOSE CONNECTION 1":
				JButton temp3 = (JButton) evt.getSource();
				temp3.setText("OPEN CONNECTION 1");
				temp3.setActionCommand("OPEN CONNECTION 1");
				mon.putCommandToWriter(0, ClientMonitor.CLOSE_CONNECTION);
				break;
			case "CLOSE CONNECTION 2":
				JButton temp4 = (JButton) evt.getSource();
				temp4.setText("OPEN CONNECTION 2");
				temp4.setActionCommand("OPEN CONNECTION 2");
				mon.putCommandToWriter(1, ClientMonitor.CLOSE_CONNECTION);
				break;
			case "OPEN CONNECTION 1":
				JButton temp5 = (JButton) evt.getSource();
				temp5.setText("CLOSE CONNECTION 1");
				temp5.setActionCommand("CLOSE CONNECTION 1");
				mon.putCommandToWriter(0, ClientMonitor.OPEN_CONNECTION);
				break;
			case "OPEN CONNECTION 2":
				JButton temp6 = (JButton) evt.getSource();
				temp6.setText("CLOSE CONNECTION 2");
				temp6.setActionCommand("CLOSE CONNECTION 2");
				mon.putCommandToWriter(1, ClientMonitor.OPEN_CONNECTION);
				break;
			case "SYNCHRONIZED":
				//TODO FIX Sync modes
				JButton temp7 = (JButton) evt.getSource();
				temp7.setText("ASYNCHRONIZED MODE");
				temp7.setActionCommand("ASYNCHRONIZED");
				infoPanel.setLabelText(1, "Synchronized");
				break;
			case "ASYNCHRONIZED":
				JButton temp8 = (JButton) evt.getSource();
				temp8.setText("SYNCHRONIZED MODE");
				temp8.setActionCommand("SYNCHRONIZED");	
				infoPanel.setLabelText(1, "Asynchronized");
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
