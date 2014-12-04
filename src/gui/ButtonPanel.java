package gui;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import client.ButtonHandler;
import client.ClientMonitor;

public class ButtonPanel extends JPanel {
	private JButton movie, connect1, connect2, sync;
	private ButtonHandler buttonHandler;
	private InfoPanel infoPanel;
	
	public ButtonPanel(ClientMonitor mon, InfoPanel infoPanel) {
		setLayout(new GridLayout(2, 2));
		buttonHandler = new ButtonHandler(mon,infoPanel);

		
		connect1 = new JButton("OPEN CONNECTION 1");
		connect1.setActionCommand("OPEN CONNECTION 1");
		connect1.addActionListener(buttonHandler);
		
		connect2 = new JButton("OPEN CONNECTION 2");
		connect2.setActionCommand("OPEN CONNECTION 2");
		connect2.addActionListener(buttonHandler);
		
		movie = new JButton("MOVIE");
		movie.setActionCommand("MOVIE");
		movie.addActionListener(buttonHandler);

		sync = new JButton("SYNCHRONIZED");
		sync.setActionCommand("SYNCHRONIZED");
		sync.addActionListener(buttonHandler);
		
		add(connect1);
		add(connect2);
		add(movie);
		add(sync);

	}
	
	public void setButtonText(int buttonIndex, String text) {
		if (buttonIndex < 0 && buttonIndex > 3) {
			System.out.println("The button index is too high or too low.");
		}

		switch (buttonIndex) {
		
		case 1:
			movie.setText(text);
			movie.setActionCommand("IDLE");
			break;
		case 2:
			sync.setText(text);
			break;
		}
	}

}
