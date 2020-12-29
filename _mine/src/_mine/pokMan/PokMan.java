package _mine.pokMan;

public class PokMan {
	public static final int SIZE = 40;
	static int row;
	static int col;
	int numr;
	int numc;
	
	
	public PokMan(int x,int y) {
		row = y / 2;
		col = x / 2;
		numr = y;
		numc = x;		
	}
	
	public void changeLoc(int r,int c) {
		row = r;
		col = c;
	}
	
	public boolean clear(int dir) {
		if(dir == 1 && row == 0)
			return false;
		if(dir == 2 && col == numc)
			return false;
		if(dir == 3 && row == numr)
			return false;
		if(dir == 4 && col == 0)
			return false;
		return true;
	}
	
	public void move(int dir) {
		if(dir == 1)
			if(clear(dir))
				row -= 1;
		
		if(dir == 2)
			if(clear(dir))
				col +=1;
		
		if(dir == 3)
			if(clear(dir))
				row += 1;
		
		if(dir == 4)
			if(clear(dir))
				col -= 1;
				
		changeLoc(row,col);
	}
}