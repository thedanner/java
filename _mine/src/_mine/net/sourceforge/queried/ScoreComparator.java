package _mine.net.sourceforge.queried;

import java.util.Comparator;

public class ScoreComparator implements Comparator<PlayerInfo> {

    public ScoreComparator() {
    }

    @Override
	public int compare(PlayerInfo playerInfo1, PlayerInfo playerInfo2) {
       if(playerInfo1.getScore() < playerInfo2.getScore()) {
            return 1;
        }

        return playerInfo1.getScore() <= playerInfo2.getScore() ? 0 : -1;
    }
}
