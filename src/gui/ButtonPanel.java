package gui;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import client.ButtonHandler;

public class ButtonPanel extends JPanel {
	private JButton idle, movie, close, connect, sync, async;
	private ButtonHandler buttonHandler;
	public ButtonPanel() {
		setLayout(new GridLayout(3, 2));
		buttonHandler = new ButtonHandler();
		idle = new JButton("IDLE");
		idle.setActionCommand("IDLE");
		idle.addActionListener(buttonHandler);
		
		movie = new JButton("MOVIE");
		movie.setActionCommand("MOVIE");
		movie.addActionListener(buttonHandler);
		
		close = new JButton("CLOSE CONNECTION");
		close.setActionCommand("CLOSE CONNECTION");
		close.addActionListener(buttonHandler);
		
		connect = new JButton("OPEN CONNECTION");
		connect.setActionCommand("OPEN CONNECTION");
		connect.addActionListener(buttonHandler);
		
		sync = new JButton("SYNCHRONIZED");
		sync.setActionCommand("SYNCHRONIZED");
		sync.addActionListener(buttonHandler);
		
		async = new JButton("ASYNCHRONIZED");
		async.setActionCommand("ASYNCHRONIZED");
		async.addActionListener(buttonHandler);
		
		add(idle);
		add(movie);
		add(close);
		add(connect);
		add(sync);
		add(async);

	}

}
