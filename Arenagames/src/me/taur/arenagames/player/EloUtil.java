package me.taur.arenagames.player;


public class EloUtil {
	public static int getConstantByElo(int elo) {
		if (elo < 501) {
			return 20;
			
		} else if (501 < elo && elo < 801) {
			return 18;
			
		} else if (801 < elo && elo < 1501) {
			return 16;
			
		} else if (1501 < elo && elo < 2601) {
			return 12;
			
		} else if (2601 < elo && elo < 3301) {
			return 6;
			
		} else {
			return 0;
			
		}
		
	}
	
	public static int addElo(int oldelo, int avg) {
		int constant = getConstantByElo(oldelo);
		double percent = ((double) avg) / ((double) oldelo);
		double modifier = constant * percent;
		
		int newelo = oldelo + ((int) modifier);
		
		if (newelo > 4000) {
			newelo = 4000;
			
		}
		
		return newelo;
		
	}
	
	public static int removeElo(int oldelo, int avg) {
		int constant = getConstantByElo(oldelo);
		double percent = ((double) avg) / ((double) oldelo);
		double modifier = constant * percent;
		
		int newelo = oldelo - ((int) modifier);
		
		if (newelo < 0) {
			newelo = 0;
			
		}
		
		return newelo;
		
	}
	
}
