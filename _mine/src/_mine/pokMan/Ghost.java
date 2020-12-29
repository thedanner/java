package _mine.pokMan;

public class Ghost {
	private int x;
	private int y;
	private int dirX;
	private int dirY;
	private boolean lastChoiceX;
	private int speed;
	
	public Ghost(int grid[][], int spd) {
		x = 1;
		y = 1;
		dirX = 1;
		dirY = 1;
		lastChoiceX = false;
		speed = spd;
	}
	
	private void generatePath(int myX, int myY, int myX2, int myX3) {
		if(myX > myX2)
			dirX = -1;
		if(myX < myX2)
			dirX = 1;
		if(myY > myX3)
			dirY = -1;
		if(myY < myX3)
			dirY = 1;
		if(dirX != 0 && dirY != 0)
		{
			if(lastChoiceX)
				dirX = 0;
			else
				dirY = 0;
			lastChoiceX = !lastChoiceX;
		}
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void move(int myX, int myY) {
		int k = x / 40;
		int l = y / 40;
		generatePath(k, l, myX, myY);
		x += dirX * speed;
		y += dirY * speed;
	}
	
	public void setXY(int myX, int myY) {
		x = myX * 40;
		y = myY * 40;
		dirX = 1;
		dirY = 1;
		generatePath(myX, myY, myX, myY);
	}
	
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	public int getSpeed() {
		return speed;
	}
}