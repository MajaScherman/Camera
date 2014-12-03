package gui;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class DelayPanel extends JPanel {
private JLabel label1;
	
	public DelayPanel(){
		
		Border blackline = BorderFactory.createLineBorder(Color.black);
		
		label1 = new JLabel("Delay 1");
		label1.setBorder(blackline);
		label1.setFont(label1.getFont().deriveFont(50f));
		label1.setHorizontalAlignment(SwingConstants.CENTER);
		
		setLayout(new GridLayout(1, 1));
		add(label1);
		this.setSize(500, 100);
	}
}
