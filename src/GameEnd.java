
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.*;

public class GameEnd extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean haveIWon;

	public GameEnd(boolean haveIWon) {

		this.haveIWon = haveIWon;
        setTitle("Pong Multiplayer: Results");
        setSize(400, 400);
        this.addWindowListener(new WindowAdapter() {
        	 
        	@Override
        	public void windowClosing(WindowEvent e) {
        	 
        	}
        	 
		  });
        // Create JButton and JPanel
        JButton button = new JButton("Go to the Main Menu");
        button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});

        String text;
        if(this.haveIWon) {
        	text = "WINNER";
        } else {
        	text = "DEFEATED";
        }

        JLabel winLooseText = new JLabel(text, SwingConstants.CENTER);
        winLooseText.setFont(new Font("Serif", Font.PLAIN, 50));
        JPanel panel = new JPanel();

        panel.add(winLooseText);
        panel.add(button);
        // And JPanel needs to be added to the JFrame itself!
        this.getContentPane().add(panel);

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

}
