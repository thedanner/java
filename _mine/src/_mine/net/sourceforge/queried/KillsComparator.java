package _mine.net.sourceforge.queried;

import java.util.Comparator;

public class KillsComparator implements Comparator<PlayerInfo> {

    public KillsComparator() {
    }

    @Override
	public int compare(PlayerInfo playerInfo1, PlayerInfo playerInfo2) {
        if(playerInfo1.getKills() < playerInfo2.getKills()) {
            return 1;
        }

        return playerInfo1.getKills() <= playerInfo2.getKills() ? 0 : -1;
    }
}
