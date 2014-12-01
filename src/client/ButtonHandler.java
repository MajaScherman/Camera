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
		switch(command){
		case "IDLE": 
			mon.setCommand(ClientMonitor.IDLE);	
			infoPanel.setLabelText(2, "Idle Mode");
			break;
		case "MOVIE": 
			mon.setCommand(ClientMonitor.MOVIE_MODE);	
			infoPanel.setLabelText(2, "Movie Mode");
			break;
		case "CLOSE CONNECTION": 
			mon.setCommand(ClientMonitor.CLOSE_CONNECTION);	
			System.out.println("cc");
			break;
		case "OPEN CONNECTION": 
			mon.setCommand(ClientMonitor.START_CONNECTION);	
			System.out.println("oc");
			break;
		case "SYNCHRONIZED": 
			System.out.println("sync");
			break;
		case "ASYNCHRONIZED": 
			System.out.println("async");
			break;
		default: 
			System.out.println("Something went terribly wrong when you pressed a button, please do not press any buttons you noob");
			break;
		}
	}
}
