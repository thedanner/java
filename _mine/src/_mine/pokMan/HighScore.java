package _mine.pokMan;

/**
 * This class represents an individual <tt>HighScore</tt> object with a name
 * and a score.
 */
public class HighScore implements Comparable<HighScore> {
	private String name;
	private int score;
	
	/**
	 * Creates a new instance of a <tt>HighScore</tt> object.
	 * @param name the name of the player to add
	 * @param s the player's score
	 */
	public HighScore(String name, int s) {
		this.name = name;
		score = s;
	}
	
	/**
	 * Gets the name of the player who is represented by this
	 * <tt>HighScore</tt> object.
	 * @return the player's name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the score of the player who is represented by this
	 * <tt>HighScore</tt> object.
	 * @return the player's score
	 */
	public int getScore() {
		return score;
	}
	
	/**
	 * Compares this <tt>HighScore</tt> object to another based solely on
	 * score.  The returned value is the parameter's score minus this object's
	 * score.
	 * @param o the high score to compare to.
	 * @return the parameter's score minus this object's score.
	 */
	@Override
	public int compareTo(HighScore o) {
		int s = o.getScore();
		return s - score;
	}
}