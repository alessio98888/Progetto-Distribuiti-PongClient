import java.awt.*;
import java.awt.event.*;

import java.rmi.RemoteException;

public class Paddle extends Rectangle{

	int id;
	int yVelocity;
	int speed = 10;
	
	Coordinate2DRemoteInterface remotePlayer;

	Paddle(int x, int y, int PADDLE_WIDTH, int PADDLE_HEIGHT, int id, Coordinate2DRemoteInterface remotePlayer){
		super(x,y,PADDLE_WIDTH,PADDLE_HEIGHT);
		this.remotePlayer = remotePlayer;
		this.id=id;
	}
	Paddle(int x, int y, int PADDLE_WIDTH, int PADDLE_HEIGHT, int id){
		super(x,y,PADDLE_WIDTH,PADDLE_HEIGHT);
			
		this.id=id;
	}
	
	public void keyPressed(KeyEvent e) {
		switch(id) {
		case -1:
			if(e.getKeyCode()==KeyEvent.VK_W) {
				setYDirection(-speed);
			}
			if(e.getKeyCode()==KeyEvent.VK_S) {
				setYDirection(speed);
			}
			break;
		case 1:
			if(e.getKeyCode()==KeyEvent.VK_UP) {
				setYDirection(-speed);
			}
			if(e.getKeyCode()==KeyEvent.VK_DOWN) {
				setYDirection(speed);
			}
			break;
		}
	}
	public void keyReleased(KeyEvent e) {
		switch(id) {
		case -1:
			if(e.getKeyCode()==KeyEvent.VK_W) {
				setYDirection(0);
			}
			if(e.getKeyCode()==KeyEvent.VK_S) {
				setYDirection(0);
			}
			break;
		case 1:
			if(e.getKeyCode()==KeyEvent.VK_UP) {
				setYDirection(0);
			}
			if(e.getKeyCode()==KeyEvent.VK_DOWN) {
				setYDirection(0);
			}
			break;
		}
	}
	public void setYDirection(int yDirection) {
		yVelocity = yDirection;
	}
	public void move() {
		if(id==1) {
			y= y + yVelocity;
			try {
				remotePlayer.setY(y);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else {
			try {
				y = remotePlayer.getY();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	public void draw(Graphics g) {
		if(id==1) {
			g.setColor(Color.blue);
			g.fillRect(x, y, width, height);
		}
		else {
			g.setColor(Color.red);
			try {
				g.fillRect(x, remotePlayer.getY(), width, height);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
}