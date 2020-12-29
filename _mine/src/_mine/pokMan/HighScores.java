package _mine.pokMan;

import java.util.ArrayList;

/**
 * This is an initial release of a model data structure that
 * keeps track of a list of high scores.  The data structure used
 * to keep track of the <tt>HighScore</tt> objects this class models
 * is dynamic: an <tt>ArrayList</tt>.  The current design allows for
 * lower numbers to have higher scores, starting at position 1.
 * <br>
 * NOTE::</b> at present, this list is initialized to default values suitable
 * for the PokMan game.  No further changes will be made until a later date in
 * the interest of time.
 * <br>
 * @version 1.0 (01 Jun 2004)
 */
public class HighScores {
	private ArrayList<HighScore> scores;
	private static final int DEFAULT_SIZE = 10;
	
	/**
	 * Default constructor: Creates a new object of type <tt>HighScores</tt>
	 * with <tt>DEFAULT_SIZE</tt> (ten) initial slots for <tt>HighScore</tt>
	 * objects.
	 */
	public HighScores()  {
		initialize(DEFAULT_SIZE);
	}
	
	/**
	 * Creates a new object of type <tt>HighScores</tt> with <tt>size</tt> initial
	 * slots for <tt>HighScore</tt> objects.
	 * @param size the initial number of slots for high scores
	 */
	public HighScores(int size) {
		initialize(size);
	}
	
	/**
	 * Gets the high score in rank <tt>pos</tt> and returns it.
	 * @param pos the rank of the score to return
	 * @return the <tt>HighScore</tt> object in position <tt>pos</tt>
	 */
	public HighScore getScore(int pos) {
		return (HighScore)scores.get(pos);
	}
	
	/**
	 * Adds a new <tt>HighScore</tt> object to the list of high scores by calling the
	 * one-parameter <tt>add</tt> method with a new <tt>HighScore</tt> object.
	 * @param name the name of the player to add
	 * @param score the player's score
	 */
	public void add(String name, int score) {
		add(new HighScore(name, score));
	}
	
	/**
	 * Adds an object of type <tt>HighScore</tt> to the score list and maintains the list
	 * in proper (descending) order.  This is done in the following manner:
	 * <ul>
	 * 	<li>Adds the <tt>HighScore</tt> to the scores list, and then</li>
	 * 	<li>sorts the list using insertion sort, whose efficiency is
	 * 	acceptable for this usage (small list).</li>
	 * </ul>
	 * This is a helper method that calls the <tt>sortScores</tt> method, which
	 * executes the bulk of the insertion sort algorithm.
	 * @param hs the <tt>HighScore</tt> to add to the scores list
	 */
	public void add(HighScore hs) {
		// binary search to see where it goes
		int spot = binarySearch(scores, hs);
		scores.add(spot, hs);
	}
	
	/**
	 * Returns the number of <tt>HighScore</tt>s in the list.
	 * @return the number of <tt>HighScore</tt>s.
	 */
	public int scoreCount() {
		return scores.size(); 
	}
	
	/**
	 * Initializes each instance of <tt>HighScores</tt> objects with the specified
	 * number of slots (<tt>size</tt>) for scores.
	 * @param size the number of scores to initialize the score list to
	 * be able to hold
	 */
	private void initialize(int size) {
		scores = new ArrayList<HighScore>(size);
		
		add(new HighScore("This was", 1000));
		add(new HighScore("made by", 900));
		add(new HighScore("John J", 800));
		add(new HighScore("(aka TripleJ)", 700));
		add(new HighScore("and", 600));
		add(new HighScore("Dan M.", 500));
		add(new HighScore("We hope", 400));
		add(new HighScore("you enjoy this", 300));
		add(new HighScore("parody of PacMan,", 200));
		add(new HighScore("PokMan!!", 100));
	}
	
	/**
	 * 
	 * @param scores
	 * @param score
	 * @return
	 */
	private static int binarySearch(ArrayList<HighScore> scores, HighScore score) {
		int low = 0;
		int high = scores.size() - 1;
		
		while (low <= high) {
			int mid = (low + high) >> 1;
		HighScore midVal = scores.get(mid);
		int comp = midVal.compareTo(score);
		
		if (comp < 0)
			low = mid + 1;
		else if (comp > 0)
			high = mid - 1;
		else
			return mid; // key found
		}
		// key not found, add at this index to maintain sorted order
		return low;
	}
}