package _mine.pokMan;

import java.awt.BufferCapabilities;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

public class Game implements Runnable
{
	// number of buffers used
	public static final int NUM_BUFFERS = 2;
	
	// max width needed for graphics
	public static final int AREA_WIDTH = 650;
	public static final int AREA_HEIGHT = 550;
	// width of the movable area for PokMan
	public static final int WIDTH = 400;
	public static final int HEIGHT = 440;
	
	private Thread th;
	private Frame mainFrame;
	private boolean drawDebug;
	private boolean useFS;
	private boolean run;
	
	// booleans monitoring whether or not the game is over
	private boolean dead;
	private boolean gameOver;
	// the current score
	private int score = 0;
	// number of remaining lives
	private int lives;
	
	// PokMan's x position
	int x = 0;
	// amount x will change
	int dx[] = { 0, 0 };
	// PokMan's y position
	int y = 0;
	// amount y will change
	int dy[] = { 0, 0 };
	
	// the column pokman is in
	private int curCol;
	// the row pokman is in
	private int curRow;
	// the next x position of pokman in pixels = 9 * Pokman.getSize()s;
	private int nextX;
	// the next y position of pokman in pixels = 9 * Pokman.getSize();
	private int nextY;
	private int mouthOpenAngle;
	private int dMouthOpenAngle;
	private int maxMouthOpenAngle;
	private int minMouthOpenAngle;
	
	private int speed;
	
	// direction that pokman is pointing
	private int mouthStartAngle = 180;
	private int[][] theMaze;
	private Ghost ghost;
	private PokGui parent;
	private GraphicsDevice myDevice;
	
	/**
	 * 
	 * @param speed
	 * @param lives
	 */
	public Game(int speed, int lives, PokGui parent)
	{
		this.parent = parent;
		setSpeed(speed);
		setLives(lives);
		
		myDevice = GraphicsCapabilities.getScreenDevice();
		GraphicsConfiguration gc = myDevice.getDefaultConfiguration();
		
		mainFrame = new Frame(gc);
		
	}
	
	private void initMainFrame()
	{
		mainFrame.addKeyListener(new Keyboard(this));
		mainFrame.setResizable(false);
		mainFrame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent evt)
			{
				stopGame();
			}
		});
		
		if (!mainFrame.isDisplayable())
		{
			mainFrame.setUndecorated(useFS);
			mainFrame.setIgnoreRepaint(useFS);
			mainFrame.setUndecorated(useFS);
		}
	}
	
	private void initGame()
	{
		drawDebug = false;
		run = true;
		
		dead = false;
		gameOver = false;
		theMaze = setupMaze();
		ghost = new Ghost(theMaze, speed - 1);
		
		mouthOpenAngle = 20;
		dMouthOpenAngle = 5;
		maxMouthOpenAngle = 100;
		minMouthOpenAngle = 10;
		
		nextX = 5 * PokMan.SIZE;
		nextY = 5 * PokMan.SIZE;
		
		x = nextX;
		y = nextY;
		
		resetMaze();
	}
	
	public boolean collision()
	{
		if ((ghost.getX() <= y + PokMan.SIZE / 2 && ghost.getX() + PokMan.SIZE
				/ 2 >= y)
				&& ((ghost.getY() <= x + PokMan.SIZE / 2 && ghost.getY()
						+ PokMan.SIZE / 2 >= x)))
		{
			return true;
		}
		return false;
	}
	
	private int[][] setupMaze()
	{
		int[][] mazeArray = new int[12][11];
		for (int row = 1; row < 11; row++)
			for (int col = 0; col < 10; col++)
				mazeArray[row][col] = 2;
		
		return mazeArray;
	}
	
	private void movePokman(int nextX, int nextY, int i)
	{
		x = nextX;
		y = nextY;
		
		// Don't let him go off the sides of the screen
		if (x > WIDTH - PokMan.SIZE + 1)
		{
			x = WIDTH - PokMan.SIZE;
			dx[0] = 0;
			dx[1] = 0;
		}
		if (x < 0)
		{
			x = 0;
			dx[0] = 0;
			dx[1] = 0;
		}
		
		// Don't let him go off the top or bottom of the screen
		if (y > HEIGHT - PokMan.SIZE)
		{
			y = HEIGHT - PokMan.SIZE;
			dy[0] = 0;
			dy[1] = 0;
		}
		if (y < PokMan.SIZE)
		{
			y = PokMan.SIZE;
			dy[0] = 0;
			dy[1] = 0;
		}
	}
	
	private void drawMouth()
	{
		if (dx[0] > 0)
		{
			mouthStartAngle = 0;
		}
		else if (dx[0] < 0)
		{
			mouthStartAngle = 180;
		}
		else if (dy[0] > 0)
		{
			mouthStartAngle = 270;
		}
		else if (dy[0] < 0)
		{
			mouthStartAngle = 90;
		}
	}
	
	private int drawMaze(Graphics gBuf)
	{
		int row;
		int col;
		int pellets;
		
		// draw maze
		gBuf.setColor(Color.BLUE);
		pellets = 0; // prep for counting remaining pellets
		
		for (row = 1; row < theMaze.length - 1; row++)
		{
			for (col = 0; col < theMaze[row].length; col++)
			{
				if (theMaze[row][col] == 2)
				{
					pellets++;// if the entry is 2, add to remaining pellets
					gBuf.setColor(Color.WHITE);// the line below draws the
											   // pellet
					gBuf
							.fillArc(col * PokMan.SIZE + PokMan.SIZE / 2 - 5,
									row * PokMan.SIZE + PokMan.SIZE / 2 - 5,
									10, 10, 0, 360);
				}
			}
		}
		return pellets;
	}
	
	private void resetMaze()
	{
		resetLocations();
	}
	
	private void resetLocations()
	{
		nextX = 9 * PokMan.SIZE;
		nextY = 9 * PokMan.SIZE;
		x = nextX;
		y = nextY;
		
		ghost = new Ghost(theMaze, speed - 1);
		
		dx[0] = 0; // amount x position will change
		dy[0] = 0; // amount y will change
		dx[1] = 0; // amount x position will change
		dy[1] = 0; // amount y will change
		
		mouthStartAngle = 180; // direction that Pokman is pointing
	}
	
	public void setSpeed(int speed)
	{
		if (speed < PokGui.SPEED_MIN || speed > PokGui.SPEED_MAX)
			return;
		
		this.speed = speed;
		
		if (ghost != null)
			ghost.setSpeed(speed - 1);
		
		parent.setSpeed(speed);
	}
	
	public int getSpeed()
	{
		return speed;
	}
	
	public void setLives(int newLives)
	{
		if (newLives < 0)
			throw new IllegalArgumentException("newSpeed (" + newLives
					+ ") must be >= 0");
		this.lives = newLives;
	}
	
	public int getLives()
	{
		return lives;
	}
	
	public void toggleDebug()
	{
		drawDebug = !drawDebug;
	}
	
	public void start(boolean fullScreen)
	{
		if (fullScreen)
			parent.setVisible(false);
		
		parent.setStartButtonsEnabled(false);
		useFS = fullScreen;
		
		initMainFrame();
		mainFrame.setVisible(true);
		
		th = new Thread(this);
		th.start();
	}
	
	public void stop()
	{
		stopGame();
		
		parent.setVisible(true);
		
		parent.setStartButtonsEnabled(true);
		mainFrame.setVisible(false);
		th = null;
	}
	
	public void stopGame()
	{
		run = false;
	}
	
	@Override
	public void run()
	{
		initGame();
		
		// sets up threading
		Thread thisThread = Thread.currentThread();
		thisThread.setPriority(Thread.MIN_PRIORITY);
		
		DisplayMode oldMode = myDevice.getDisplayMode();
		
		try
		{
			run = true;
			
			if (useFS)
			{
				setResolution();
				myDevice.setFullScreenWindow(mainFrame);
			}
			else
			{
				mainFrame.setBounds(50, 50, AREA_WIDTH, AREA_HEIGHT);
				mainFrame.requestFocusInWindow();
			}
			
			mainFrame.createBufferStrategy(NUM_BUFFERS);
			BufferStrategy myStrategy = mainFrame.getBufferStrategy();
			
			// main work loop
			while (thisThread == th && run)
			{
				Graphics g = myStrategy.getDrawGraphics();
				
				if (!myStrategy.contentsLost())
				{
					run = render(g, myStrategy.getCapabilities()); // render
																   // loop
					myStrategy.show();
					g.dispose();
				}
				
				try
				{
					Thread.sleep(20);
				}
				catch (InterruptedException e)
				{
				}
			}
		}
		finally
		{
			if (useFS)
			{
				if (GraphicsCapabilities.isDisplayChangeSupported())
					myDevice.setDisplayMode(oldMode);
				myDevice.setFullScreenWindow(null);
			}
			stop();
		}
	}
	
	private void setResolution()
	{
		if (GraphicsCapabilities.isDisplayChangeSupported())
		{
			try
			{
				myDevice.setDisplayMode(new DisplayMode(1024, 768, 16, 60));
			}
			catch (RuntimeException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private boolean render(Graphics g, BufferCapabilities buf)
	{
		clearScreen(g);
		mainFrame.setBackground(Color.BLACK);
		
		// draw walls
		g.setColor(Color.RED);
		g.drawLine(WIDTH, 0, WIDTH, HEIGHT);
		g.drawLine(0, HEIGHT, WIDTH, HEIGHT);
		
		// draw quit message
		g.setColor(Color.BLUE);
		g.setFont(new Font("Arial", Font.ITALIC + Font.BOLD, 32));
		g.drawString("Press \"Esc\" to quit.", 20, HEIGHT + 2 * PokMan.SIZE);
		
		// draw score, lives, and speed
		g.setColor(Color.GREEN);
		g.drawString("Score: " + score, WIDTH + PokMan.SIZE, 50);
		g.drawString("Lives: " + lives, WIDTH + PokMan.SIZE, 100);
		g.drawString("Speed: " + speed, WIDTH + PokMan.SIZE, 150);
		
		// draw debug info
		if (drawDebug)
		{
			int x = 10;
			int y = HEIGHT + 2 * PokMan.SIZE + 40;
			int h = 12;
			
			boolean fsr = buf.isFullScreenRequired();
			boolean pf = buf.isPageFlipping();
			boolean mba = buf.isMultiBufferAvailable();
			boolean fa = buf.getFrontBufferCapabilities().isAccelerated();
			boolean ftv = buf.getFrontBufferCapabilities().isTrueVolatile();
			boolean ba = buf.getBackBufferCapabilities().isAccelerated();
			boolean btv = buf.getBackBufferCapabilities().isTrueVolatile();
			
			g.setFont(new Font("Arial", Font.ITALIC + Font.BOLD, h));
			g.setColor(Color.GRAY);
			g.drawString("Full screen required: " + fsr, x, y += h);
			g.drawString("Page flipping: " + pf, x, y += h);
			g.drawString("Multi buffer available: " + mba, x, y += h);
			g.drawString("Front buffer - accelerated: " + fa, x, y += h);
			g.drawString("Front buffer - true volatile: " + ftv, x, y += h);
			g.drawString("Back buffer - accelerated: " + ba, x, y += h);
			g.drawString("Back buffer - true volatile: " + btv, x, y += h);
			// g.drawString(" " + pf, x, y += h);
		}
		
		// algorithm for finding coordinates
		curCol = (x + PokMan.SIZE / 2) / PokMan.SIZE;
		curRow = (y + PokMan.SIZE / 2) / PokMan.SIZE;
		
		// pellet eaten, score is increased by 1
		if (theMaze[curRow][curCol] == 2)
		{
			theMaze[curRow][curCol] = 0;
			score++;
		}
		
		// do ghost stuff
		int i = 0;
		int pellets = 0;
		for (i = 1; i >= 0; i--)
		{
			nextX = x + dx[i];
			nextY = y + dy[i];
			
			if (i == 1)
			{
				dx[0] = dx[1];
				dy[0] = dy[1];
			}
		}
		
		// sets the death boolean to true, indicating collision w/ghost
		if (!gameOver && collision() == true)
			dead = true;
		
		drawMouth();
		// Move Pokman
		if (!gameOver)
			movePokman(nextX, nextY, i);
		
		// Make the mouth chomp
		mouthOpenAngle = mouthOpenAngle + dMouthOpenAngle;
		
		if (mouthOpenAngle > maxMouthOpenAngle)
		{
			mouthOpenAngle = maxMouthOpenAngle;
			dMouthOpenAngle = -10;
		}
		if (mouthOpenAngle < minMouthOpenAngle)
		{
			mouthOpenAngle = minMouthOpenAngle;
			dMouthOpenAngle = 5;
		}
		pellets = drawMaze(g);
		
		g.setColor(Color.YELLOW);
		
		// draw PokMan
		g.fillArc(x, y, PokMan.SIZE, PokMan.SIZE, mouthStartAngle
				+ mouthOpenAngle / 2, 360 - mouthOpenAngle);
		
		// draw ghost, very, very messy to look at, but the most efficient way
		if (!gameOver)
		{
			ghost.move(curRow, curCol);
			
			g.setColor(Color.GRAY);
			g.fillOval(ghost.getY(), ghost.getX(), PokMan.SIZE, PokMan.SIZE);
			g.setColor(Color.RED);
			g.fillOval(ghost.getY() + PokMan.SIZE / 5, ghost.getX()
					+ PokMan.SIZE / 5, PokMan.SIZE / 5, PokMan.SIZE / 5);
			g.fillOval(ghost.getY() + PokMan.SIZE - 15, ghost.getX()
					+ PokMan.SIZE / 5, PokMan.SIZE / 5, PokMan.SIZE / 5);
			g.setColor(Color.BLUE);
			g.fillOval(ghost.getY() + 10, ghost.getX() + PokMan.SIZE / 2 + 5,
					PokMan.SIZE / 2, PokMan.SIZE / 5);
		}
		else
		{
			g.setFont(new Font("Arial", Font.ITALIC + Font.BOLD, 32));
			g.setColor(Color.GREEN);
			g.drawString("Final Score: ", WIDTH + PokMan.SIZE, 300);
			g.drawString(score + "", WIDTH + PokMan.SIZE, 400);
		}
		
		if (dead == true)
		{
			lives--;
			dead = false;
			if (lives <= 0)
			{
				gameOver = true;
			}
			else
			{
				resetMaze();
			}
		}
		
		if (pellets < 1)
		{
			gameOver = true;
		}
		
		return run;
	}
	
	private void clearScreen(Graphics gBuf)
	{
		if (gBuf != null)
			gBuf.clearRect(0, 0, mainFrame.getWidth(), mainFrame.getHeight());
	}
}
