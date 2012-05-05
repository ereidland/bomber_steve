package net.evtr.bombersteve;

import java.util.Vector;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BomberPlayer {
	public int range, points, maxBombs;
	public Player player;
	public int gameID;
	
	public static class Bomb {
		public Block block;
		public int timeLeft;
		
		public Bomb(Block block, int time) {
			this.block = block;
			timeLeft = time;
		}
	}
	
	public boolean hasDied;
	
	public Vector<Bomb> bombs;
	
	public boolean ownsBlock(Block b) {
		for ( int i = 0; i < bombs.size(); i++ ) {
			if ( bombs.get(i).block == b ) return true;
		}
		
		return false;
	}
	
	public void disownBlock(Block b) {
		for ( int i = 0; i < bombs.size(); i++ ) {
			if ( bombs.get(i).block == b ) {
				bombs.remove(i);
				break;
			}
		}
		
	}
	
	public BomberPlayer(Player player, int gameID) {
		this.player = player;
		
		this.gameID = gameID;
		range = 4;
		points = 0;
		
		maxBombs = 1;
		bombs = new Vector<Bomb>();
		
		hasDied = false;
	}
}
