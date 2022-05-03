
import java.awt.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;

public class Ball extends Rectangle{

	Random random;
	int xVelocity;
	int yVelocity;
	int xVelocityRemote;
	int yVelocityRemote;
	int initialSpeed = 2;
	int xRemote;
	int yRemote;
	Coordinate2DRemoteInterface remoteBall;
	boolean iAmTheBallManager;
	Ball(int x, int y, int width, int height, Coordinate2DRemoteInterface remoteBall, boolean iAmTheBallManager){
		super(x,y,width,height);
		xRemote = x;
		yRemote = y;
		this.remoteBall = remoteBall;
		this.iAmTheBallManager = iAmTheBallManager;

		if(iAmTheBallManager) {
			random = new Random();
			int randomXDirection = random.nextInt(2);
			if(randomXDirection == 0)
				randomXDirection--;
			setXDirection(randomXDirection*initialSpeed);
			
			int randomYDirection = random.nextInt(2);
			if(randomYDirection == 0)
				randomYDirection--;
			setYDirection(randomYDirection*initialSpeed);
		}
		
	}
	
	public void setXDirection(int randomXDirection) {
		xVelocity = randomXDirection;
		xVelocityRemote = randomXDirection*(-1);
	}
	public void setXVelocity(int xVelocity) {
		this.xVelocity = xVelocity;
		this.xVelocityRemote = -xVelocity;
	}
	public void setYDirection(int randomYDirection) {
		yVelocity = randomYDirection;
		//yVelocityRemote = randomYDirection*(-1);
		yVelocityRemote = randomYDirection;
	}
	public void move() {
		try {
			if(iAmTheBallManager) {
				xRemote += xVelocityRemote;
				yRemote += yVelocityRemote;
				x += xVelocity;
				y += yVelocity;
				remoteBall.setX(xRemote);
				remoteBall.setY(yRemote);
			} else {
				x = remoteBall.getX();
				y = remoteBall.getY();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	public void draw(Graphics g) {
		g.setColor(Color.white);
		g.fillOval(x, y, height, width);
	}
}