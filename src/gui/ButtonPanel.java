package gui;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import client.ButtonHandler;
import client.ClientMonitor;

public class ButtonPanel extends JPanel {
	private JButton idle, movie, connect1, connect2, sync, async;
	private ButtonHandler buttonHandler;
	private InfoPanel infoPanel;
	
	public ButtonPanel(ClientMonitor mon, InfoPanel infoPanel) {
		setLayout(new GridLayout(3, 2));
		buttonHandler = new ButtonHandler(mon,infoPanel);
		idle = new JButton("IDLE");
		idle.setActionCommand("IDLE");
		idle.addActionListener(buttonHandler);
		
		movie = new JButton("MOVIE");
		movie.setActionCommand("MOVIE");
		movie.addActionListener(buttonHandler);
		
		connect1 = new JButton("OPEN CONNECTION 1");
		connect1.setActionCommand("OPEN CONNECTION 1");
		connect1.addActionListener(buttonHandler);
		
		connect2 = new JButton("OPEN CONNECTION 2");
		connect2.setActionCommand("OPEN CONNECTION 2");
		connect2.addActionListener(buttonHandler);
		
		sync = new JButton("SYNCHRONIZED");
		sync.setActionCommand("SYNCHRONIZED");
		sync.addActionListener(buttonHandler);
		
		async = new JButton("ASYNCHRONIZED");
		async.setActionCommand("ASYNCHRONIZED");
		async.addActionListener(buttonHandler);
		
		add(idle);
		add(movie);
		add(connect1);
		add(connect2);
		add(sync);
		add(async);

	}

}
