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
				mon.putCommandToClientWriter(0, ClientMonitor.IDLE_MODE); 
				mon.putCommandToClientWriter(1, ClientMonitor.IDLE_MODE);	
				JButton temp1 = (JButton) evt.getSource();
				temp1.setText("MOVIE MODE");
				temp1.setActionCommand("MOVIE");
				mon.setMovieMode(false);
				infoPanel.setLabelText(2, "Idle Mode");
				break;
			case "MOVIE":
				mon.putCommandToClientWriter(0, ClientMonitor.MOVIE_MODE);
				mon.putCommandToClientWriter(1, ClientMonitor.MOVIE_MODE);
				JButton temp2 = (JButton) evt.getSource();
				mon.setMovieMode(true);
				temp2.setText("IDLE MODE");
				temp2.setActionCommand("IDLE");		
				infoPanel.setLabelText(2, "Movie Mode");
				break;
			case "CLOSE CONNECTION 1":
				JButton temp3 = (JButton) evt.getSource();
				temp3.setText("OPEN CONNECTION 1");
				temp3.setActionCommand("OPEN CONNECTION 1");
				mon.putCommandToClientWriter(0, ClientMonitor.CLOSE_CONNECTION);
				break;
			case "CLOSE CONNECTION 2":
				JButton temp4 = (JButton) evt.getSource();
				temp4.setText("OPEN CONNECTION 2");
				temp4.setActionCommand("OPEN CONNECTION 2");
				mon.putCommandToClientWriter(1, ClientMonitor.CLOSE_CONNECTION);
				break;
			case "OPEN CONNECTION 1":
				JButton temp5 = (JButton) evt.getSource();
				temp5.setText("CLOSE CONNECTION 1");
				temp5.setActionCommand("CLOSE CONNECTION 1");
				mon.putCommandToClientWriter(0, ClientMonitor.OPEN_CONNECTION);
				break;
			case "OPEN CONNECTION 2":
				JButton temp6 = (JButton) evt.getSource();
				temp6.setText("CLOSE CONNECTION 2");
				mon.setSyncMode(0);
				temp6.setActionCommand("CLOSE CONNECTION 2");
				mon.putCommandToClientWriter(1, ClientMonitor.OPEN_CONNECTION);
				break;
			case "SYNCHRONIZED":
				//TODO FIX Sync modes
				JButton temp7 = (JButton) evt.getSource();
				temp7.setText("ASYNCHRONIZED MODE");
				temp7.setActionCommand("ASYNCHRONIZED");
				infoPanel.setLabelText(3, "Synchronized");
				mon.setSyncMode(1);
				mon.putCommandToUpdaterBuffer(ClientMonitor.SYNCHRONIZED);
				break;
			case "ASYNCHRONIZED":
				JButton temp8 = (JButton) evt.getSource();
				temp8.setText("SYNCHRONIZED MODE");
				temp8.setActionCommand("SYNCHRONIZED");	
				infoPanel.setLabelText(3, "Asynchronized");
				mon.setSyncMode(2);
				mon.putCommandToUpdaterBuffer(ClientMonitor.ASYNCHRONIZED);
				break;
			case "AUTO":
				JButton temp9 = (JButton) evt.getSource();
				temp9.setText("FORCE MODE");
				temp9.setActionCommand("FORCE");	
				infoPanel.setLabelText(4, "Auto mode");
				mon.setForceMode(false);
				mon.putCommandToUpdaterBuffer(ClientMonitor.AUTO);
				
				mon.putCommandToClientWriter(0, ClientMonitor.AUTO);
				mon.putCommandToClientWriter(1, ClientMonitor.AUTO);
				break;
				
			case "FORCE":
				JButton temp10 = (JButton) evt.getSource();
				temp10.setText("AUTO MODE");
				temp10.setActionCommand("AUTO");	
				infoPanel.setLabelText(4, "Force mode");
				mon.setForceMode(true);
				mon.putCommandToUpdaterBuffer(ClientMonitor.FORCED);

				mon.putCommandToClientWriter(0, ClientMonitor.FORCED);
				mon.putCommandToClientWriter(1, ClientMonitor.FORCED);
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
