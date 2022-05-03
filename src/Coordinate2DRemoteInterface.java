import java.rmi.*;
public interface Coordinate2DRemoteInterface extends Remote{
	public int getX() throws RemoteException;
	
	public void setX(int x) throws RemoteException;
	
	public int getY() throws RemoteException;
	
	public void setY(int y) throws RemoteException;
	
}
