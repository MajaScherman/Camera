package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class ButtonHandler implements ActionListener {

	public ButtonHandler() {
		
	}

	public void actionPerformed(ActionEvent evt) {
		String command = ((JButton) evt.getSource()).getActionCommand();
		switch(command){
		case "IDLE": 
				System.out.println("idel osv");
			break;
		case "MOVIE": 
			System.out.println("måvi");
			break;
		case "CLOSE CONNECTION": 
			System.out.println("cc");
			break;
		case "OPEN CONNECTION": 
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
