package client;

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
				mon.putCommandToAllServers(ClientMonitor.IDLE_MODE); 
				JButton temp1 = (JButton) evt.getSource();
				temp1.setText("MOVIE MODE");
				temp1.setActionCommand("MOVIE");
				infoPanel.setLabelText(2, "Idle Mode");
				break;
			case "MOVIE":
				mon.putCommandToAllServers(ClientMonitor.MOVIE_MODE);
				JButton temp2 = (JButton) evt.getSource();
				temp2.setText("IDLE MODE");
				temp2.setActionCommand("IDLE");		
				infoPanel.setLabelText(2, "Movie Mode");
				break;
			case "CLOSE CONNECTION 1":
				JButton temp3 = (JButton) evt.getSource();
				temp3.setText("OPEN CONNECTION 1");
				temp3.setActionCommand("OPEN CONNECTION 1");
				mon.putCommand(0, ClientMonitor.CLOSE_CONNECTION);
				break;
			case "CLOSE CONNECTION 2":
				JButton temp4 = (JButton) evt.getSource();
				temp4.setText("OPEN CONNECTION 2");
				temp4.setActionCommand("OPEN CONNECTION 2");
				mon.putCommand(1, ClientMonitor.CLOSE_CONNECTION);
				break;
			case "OPEN CONNECTION 1":
				JButton temp5 = (JButton) evt.getSource();
				temp5.setText("CLOSE CONNECTION 1");
				temp5.setActionCommand("CLOSE CONNECTION 1");
				mon.putCommand(0, ClientMonitor.OPEN_CONNECTION);
				break;
			case "OPEN CONNECTION 2":
				JButton temp6 = (JButton) evt.getSource();
				temp6.setText("CLOSE CONNECTION 2");
				temp6.setActionCommand("CLOSE CONNECTION 2");
				mon.putCommand(1, ClientMonitor.OPEN_CONNECTION);
				break;
			case "SYNCHRONIZED":
				JButton temp7 = (JButton) evt.getSource();
				temp7.setText("ASYNCHRONIZED MODE");
				temp7.setActionCommand("ASYNCHRONIZED");
				infoPanel.setLabelText(3, "Synchronized");
				mon.putCommandToUpdaterBuffer(ClientMonitor.SYNCHRONIZED);
				break;
			case "ASYNCHRONIZED":
				JButton temp8 = (JButton) evt.getSource();
				temp8.setText("SYNCHRONIZED MODE");
				temp8.setActionCommand("SYNCHRONIZED");	
				infoPanel.setLabelText(3, "Asynchronized");
				mon.putCommandToUpdaterBuffer(ClientMonitor.ASYNCHRONIZED);
				break;
			case "AUTO":
				JButton temp9 = (JButton) evt.getSource();
				temp9.setText("FORCE MODE");
				temp9.setActionCommand("FORCE");	
				infoPanel.setLabelText(4, "Auto mode");
				mon.putCommandToAllServers(ClientMonitor.AUTO);
				break;
				
			case "FORCE":
				JButton temp10 = (JButton) evt.getSource();
				temp10.setText("AUTO MODE");
				temp10.setActionCommand("AUTO");	
				infoPanel.setLabelText(4, "Force mode");
				mon.putCommandToAllServers(ClientMonitor.FORCED);
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
