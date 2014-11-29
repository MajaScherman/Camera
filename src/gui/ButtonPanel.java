package gui;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ButtonPanel extends JPanel {
	private JButton idle, movie, close, connect, sync, assync;

	public ButtonPanel() {
		setLayout(new GridLayout(3, 2));
		idle = new JButton("IDLE");
		movie = new JButton("MOVIE");
		close = new JButton("CLOSE CONNECTION");
		connect = new JButton("OPEN CONNECTION");
		sync = new JButton("SYNCHRONIZED");
		assync = new JButton("ASSYNCHRONIZED");
		add(idle);
		add(movie);
		add(close);
		add(connect);
		add(sync);
		add(assync);

	}

}
