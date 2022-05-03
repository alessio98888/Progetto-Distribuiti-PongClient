import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class PongGame {

	public static final PlayerInfo thisPlayer = new PlayerInfo();

	private static String currentGameId = null;

	static MatchMakerRemoteInterface matchMaker = null;
	public static void main(String[] args) {
		GameMenu menuFrame = new GameMenu();
	}

	public static String getCurrentGameId() {
		return currentGameId;
	}

	public static void setCurrentGameId(String currentGameId) {
		PongGame.currentGameId = currentGameId;
	}

}
