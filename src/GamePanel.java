import java.awt.*;
import java.awt.event.*;
import java.rmi.Naming;
import java.util.*;
import javax.swing.*;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
public class GamePanel extends JPanel implements Runnable{

	boolean gameHasEnded = false;
	static final int GAME_WIDTH = 600;
	static final int GAME_HEIGHT = (int)(GAME_WIDTH * (0.5555));
	static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH,GAME_HEIGHT);
	static final int BALL_DIAMETER = 20;
	static final int PADDLE_WIDTH = 25;
	static final int PADDLE_HEIGHT = 100;
	static final int scoreToWin = 11;
	Thread gameThread;
	Image image;
	Graphics graphics;
	Random random;
	Paddle paddle1;
	Paddle paddle2;
	Ball ball;
	Score score;
	Coordinate2DRemoteInterface thisRemotePlayer = null;
	Coordinate2DRemoteInterface otherRemotePlayer = null;
	Coordinate2DRemoteInterface remoteBall = null;
	PlayerScoresRemoteInterface playerScores = null;
	String thisPlayerId = null;
	String otherPlayerId = null;
	String ballManagerId = null;
	boolean iAmTheBallManager = false;
	GamePanel thisPanel;
	GameFrame topFrame;
	GamePanel(GameFrame topFrame){
		this.topFrame = topFrame;
		thisPanel = this;
		thisPlayerId = PongGame.thisPlayer.getPlayerId();
		otherPlayerId = null;
		String[] playerIds = null;
		try {
			thisRemotePlayer = 
					(Coordinate2DRemoteInterface)Naming.lookup("rmi://localhost/"+PongGame.getCurrentGameId()+
							"/"+thisPlayerId);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			System.out.println("ERROR RETRIEVING REMOTE THIS PLAYER. Game ID: " + PongGame.getCurrentGameId()+
					", Player ID: " + thisPlayerId);
			e.printStackTrace();
		}
		try {
			playerIds = Naming.list("rmi://localhost/"+PongGame.getCurrentGameId());
		} catch (MalformedURLException | RemoteException e) {
			e.printStackTrace();
			System.out.println("ERROR NAMING LIST.");
		}
		boolean managerFound = false;
		boolean otherPlayerFound = false;
		for(String playerUrl: playerIds) {
			System.out.println("LIST ELEMENT: " + playerUrl);
			String[] splitted;
			splitted = playerUrl.split("/");
			String playerId = splitted[splitted.length-1];
			if(!managerFound && playerId.equals("ball") && splitted[splitted.length-3].equals(PongGame.getCurrentGameId())) {
					ballManagerId = splitted[splitted.length-2];
					managerFound = true;
			}	
			if(!otherPlayerFound && splitted[splitted.length-2].equals(PongGame.getCurrentGameId()))
			{
				if(!playerId.equals("ball") &&!playerId.equals("playerScores") &&!thisPlayerId.equals(playerId)) {
					otherPlayerId = playerId;
					otherPlayerFound = true;
				}
			}
		}
		try {
			playerScores = (PlayerScoresRemoteInterface)Naming.lookup("rmi://localhost/"+PongGame.getCurrentGameId()+
					"/playerScores");
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			System.out.println("ERROR RETRIEVING REMOTE PLAYER SCORES");
			e.printStackTrace();
		}
		try {
			remoteBall = (Coordinate2DRemoteInterface)Naming.lookup("rmi://localhost/"+PongGame.getCurrentGameId()+
					"/"+ballManagerId+"/ball");
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			System.out.println("ERROR RETRIEVING REMOTE BALL");
			e.printStackTrace();
		}
		if(ballManagerId.equals(thisPlayerId)) {
			iAmTheBallManager = true;

		}
		System.out.println("INFO: Game ID: "+ PongGame.getCurrentGameId()+
				", Other Player ID: "+ otherPlayerId + ", This Player ID: " + thisPlayerId +
				", Ball Manager ID: " + ballManagerId);
		try {
			otherRemotePlayer = 
					(Coordinate2DRemoteInterface)Naming.lookup("rmi://localhost/"+PongGame.getCurrentGameId()+
							"/"+otherPlayerId);

		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			System.out.println("ERROR RETRIEVING REMOTE OTHER PLAYER. Game ID: "+ PongGame.getCurrentGameId()+
					", Other Player ID: "+ otherPlayerId + ", This Player ID: " + thisPlayerId);
			e.printStackTrace();
		}
		newPaddles();
		if(!iAmTheBallManager) {
			int ballX = 0, ballY = 0;
			try {
				ballX = remoteBall.getX();
				while(ballX == 0) {
					ballX = remoteBall.getX();
				}
				ballY = remoteBall.getY();

			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ball = new Ball(ballX,ballY,BALL_DIAMETER,BALL_DIAMETER, remoteBall, iAmTheBallManager);
		} else {
			newBall();
		}
		score = new Score(GAME_WIDTH,GAME_HEIGHT);
		this.setFocusable(true);
		this.addKeyListener(new AL());
		this.setPreferredSize(SCREEN_SIZE);
		
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	public void newBall() {
		int ballX;
		int ballY;
		try {
			if(iAmTheBallManager) {
				random = new Random();
				ballX = (GAME_WIDTH/2)-(BALL_DIAMETER/2);
				ballY = random.nextInt(GAME_HEIGHT-BALL_DIAMETER);
				remoteBall.setX(ballX);
				remoteBall.setY(ballY);
				//Thread.sleep(100);
				ball = new Ball(ballX,ballY,BALL_DIAMETER,BALL_DIAMETER, remoteBall, iAmTheBallManager);
			} /*else {
				ballX = remoteBall.getX();
				ballY = remoteBall.getY();
				while(ballX==0 || ballY==0 || ballX != (GAME_WIDTH/2)-(BALL_DIAMETER/2)) {
					ballX = remoteBall.getX();
					ballY = remoteBall.getY();
				}
			}*/
		} catch(RemoteException e) {
			System.out.println("ERROR. REMOTE BALL");
			e.printStackTrace();
			return;
		}
	}
	public void newPaddles() {
		
		int xPlayer1 = 0; 
		int yPlayer1 = (GAME_HEIGHT/2)-(PADDLE_HEIGHT/2);
		int xPlayer2 = GAME_WIDTH-PADDLE_WIDTH;
		int yPlayer2 = (GAME_HEIGHT/2)-(PADDLE_HEIGHT/2);

		try {
			thisRemotePlayer.setX(xPlayer1);
			thisRemotePlayer.setY(yPlayer1);

			//otherRemotePlayer.setX(xPlayer2);
			//otherRemotePlayer.setY(yPlayer2);

			paddle1 = new Paddle(xPlayer1,yPlayer1,PADDLE_WIDTH,PADDLE_HEIGHT,1, thisRemotePlayer);
			paddle2 = new Paddle(xPlayer2,yPlayer2,PADDLE_WIDTH,PADDLE_HEIGHT,2, otherRemotePlayer);

		} catch (RemoteException | NullPointerException e) {
			System.out.println("ERROR SETTING REMOTE POSITIONS");
			e.printStackTrace();
		}
	}
	public void paint(Graphics g) {
		image = createImage(getWidth(),getHeight());
		graphics = image.getGraphics();
		draw(graphics);
		g.drawImage(image,0,0,this);
	}
	public void draw(Graphics g) {
		paddle1.draw(g);
		paddle2.draw(g);
		ball.draw(g);
		score.draw(g);
		Toolkit.getDefaultToolkit().sync(); 

	}
	boolean firstMove = true;
	int precBallX, precBallY, currentBallX, currentBallY;
	public void move() {
		paddle1.move();
		paddle2.move(); 
		ball.move();
		
	}
	public void checkCollision() {
		
		if(iAmTheBallManager) {
			//bounce ball off top & bottom window edges
			if(ball.y <=0) {
				ball.setYDirection(-ball.yVelocity);
			}
			if(ball.y >= GAME_HEIGHT-BALL_DIAMETER) {
				ball.setYDirection(-ball.yVelocity);
			}
			//bounce ball off paddles
			if(ball.intersects(paddle1)) {
				ball.xVelocity = Math.abs(ball.xVelocity);
				ball.xVelocity++; //optional for more difficulty
				if(ball.yVelocity>0)
					ball.yVelocity++; //optional for more difficulty
				else
					ball.yVelocity--;
				ball.setXDirection(ball.xVelocity);
				ball.setYDirection(ball.yVelocity);
			}
			if(ball.intersects(paddle2)) {
				ball.xVelocity = Math.abs(ball.xVelocity);
				ball.xVelocity++; //optional for more difficulty
				if(ball.yVelocity>0)
					ball.yVelocity++; //optional for more difficulty
				else
					ball.yVelocity--;
				ball.setXDirection(-ball.xVelocity);
				ball.setYDirection(ball.yVelocity);
			}
		}
		//stops paddles at window edges
		if(paddle1.y<=0)
			paddle1.y=0;
		if(paddle1.y >= (GAME_HEIGHT-PADDLE_HEIGHT))
			paddle1.y = GAME_HEIGHT-PADDLE_HEIGHT;
		if(paddle2.y<=0)
			paddle2.y=0;
		if(paddle2.y >= (GAME_HEIGHT-PADDLE_HEIGHT))
			paddle2.y = GAME_HEIGHT-PADDLE_HEIGHT;
		//give a player 1 point and creates new paddles & ball
		int leftLimit;
		int rightLimit;
		leftLimit = 0;
		rightLimit = GAME_WIDTH-BALL_DIAMETER;
		if(iAmTheBallManager) {
			if(ball.x <= leftLimit) {
				newPaddles();
				score.player2++;
				try {
					playerScores.setPlayer2Score(score.player2);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				newBall();
				System.out.println("Player 2: "+score.player2);
			}
			if(ball.x >= rightLimit) {
				newPaddles();
				score.player1++;
				try {
					playerScores.setPlayer1Score(score.player1);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				newBall();
				System.out.println("Player 1: "+score.player1);
			}
		} else {
			try {
				int newPlayer1Score = playerScores.getPlayer2Score();
				int newPlayer2Score = playerScores.getPlayer1Score();
				if(score.player1 != newPlayer1Score || score.player2 != newPlayer2Score) {
					newPaddles();
				}
				score.player1 = newPlayer1Score; 
				score.player2 = newPlayer2Score; 
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		boolean haveIWon = false;
		if(score.player1 == GamePanel.scoreToWin && !gameHasEnded) {
			// I WON
			gameHasEnded = true;
			haveIWon = true;
		} 
		else if(score.player2 == GamePanel.scoreToWin && !gameHasEnded) {
			gameHasEnded = true;
			haveIWon = false;
		}
		if(gameHasEnded) {
			try {
				PongGame.matchMaker.deletePlayerId(PongGame.thisPlayer.getPlayerId());
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
			boolean haveIWonThread = haveIWon;
			SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							JButton button = new JButton("Go to the Main Menu");
							button.addActionListener(new ActionListener() {
								
								@Override
								public void actionPerformed(ActionEvent e) {
									GameMenu menu = new GameMenu();
									topFrame.dispose();
									
								}
							});

							String text;
							if(haveIWonThread) {
								text = "WINNER";
							} else {
								text = "DEFEATED";
							}

							JLabel winLooseText = new JLabel(text, SwingConstants.CENTER);
							winLooseText.setFont(new Font("Serif", Font.PLAIN, 50));
							JPanel panel = new JPanel();

							panel.add(winLooseText);
							panel.add(button);
							panel.setFocusable(true);
							panel.setPreferredSize(SCREEN_SIZE);
							topFrame.getContentPane().add(panel);
							topFrame.getContentPane().remove(thisPanel);
							topFrame.pack();
							
						}
					});

			//this.gameThread.interrupt();
		}
	}
	public void run() {

		//game loop
		long lastTime = System.nanoTime();
		double amountOfTicks =60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		while(true) {
			long now = System.nanoTime();
			delta += (now -lastTime)/ns;
			lastTime = now;
			if(delta >=1) {
				move();
				checkCollision();

				if(gameHasEnded) {
					return;
				}
				repaint();
				delta--;
			}
		}
		
	}
	public class AL extends KeyAdapter{
		public void keyPressed(KeyEvent e) {
			paddle1.keyPressed(e);
			paddle2.keyPressed(e);
		}
		public void keyReleased(KeyEvent e) {
			paddle1.keyReleased(e);
			paddle2.keyReleased(e);
		}
	}
}