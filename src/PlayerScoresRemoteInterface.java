
import java.rmi.*;
public interface PlayerScoresRemoteInterface extends Remote{
	int getPlayer1Score() throws RemoteException;
	int getPlayer2Score() throws RemoteException;
	void setPlayer1Score(int player1Score) throws RemoteException;
	void setPlayer2Score(int player2Score) throws RemoteException;
}
