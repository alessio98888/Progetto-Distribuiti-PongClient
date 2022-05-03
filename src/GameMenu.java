
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.*;

public class GameMenu extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GameMenu() {

        setTitle("Pong Multiplayer");
        setSize(400, 400);
        this.addWindowListener(new WindowAdapter() {
        	 
        	@Override
        	public void windowClosing(WindowEvent e) {
        		if(PongGame.matchMaker != null) {
        			try {
						PongGame.matchMaker.deletePlayerId(PongGame.thisPlayer.getPlayerId());
					} catch (RemoteException e1) {
						e1.printStackTrace();
					}
        		}
        	    System.exit(0);
        	 
        	}
        	 
		  });
        // Create JButton and JPanel
        JButton button = new JButton("Play!");
        button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				button.setText("Searching for another player...");
				button.setEnabled(false);
				Runnable myRunnable =
					    new Runnable(){
					        public void run(){
					        	String gameId = null;
								try {
									PongGame.matchMaker = 
											(MatchMakerRemoteInterface)Naming.lookup("rmi://localhost/MatchMaker");

									do {
										Thread.sleep(500);
										gameId = PongGame.matchMaker.getGameId(PongGame.thisPlayer.getPlayerId());
									} while(gameId == null);

								} catch (MalformedURLException | RemoteException | NotBoundException | InterruptedException e2) {
									e2.printStackTrace();
									return;
								}

								PongGame.setCurrentGameId(gameId);
								try {
									Thread.sleep(2000);
								} catch (InterruptedException e1) {
									return;
								}
								// Start game
								setVisible(false);
								SwingUtilities.invokeLater(new Runnable() {

									@Override
									public void run() {
										GameFrame frame = new GameFrame();
										
									}
								});
							}
					        
					    };
				Thread thread = new Thread(myRunnable);
				thread.start();

				
        }});

        JPanel panel = new JPanel();

        // Add button to JPanel
        panel.add(button);
        // And JPanel needs to be added to the JFrame itself!
        this.getContentPane().add(panel);

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

}