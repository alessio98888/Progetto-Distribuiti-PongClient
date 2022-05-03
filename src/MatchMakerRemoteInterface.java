
import java.rmi.*;
public interface MatchMakerRemoteInterface extends Remote {

	public String getGameId(String requestingPlayerId) throws RemoteException;
	public boolean deletePlayerId(String playerId) throws RemoteException;
}
