package net.evtr.bombersteve;

import java.util.Vector;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BomberPlayer {
	public int range, points, maxBombs, wins, gameID;
	public Player player;
	
	public static class Bomb {
		public Block block;
		public int timeLeft;
		
		public Bomb(Block block, int time) {
			this.block = block;
			timeLeft = time;
		}
	}
	
	public boolean hasDied, isReady;
	
	public Vector<Bomb> bombs;
	
	public boolean ownsBomb(Block b) {
		for ( int i = 0; i < bombs.size(); i++ ) {
			Block bomb = bombs.get(i).block;
			if ( bomb.getX() == b.getX() && bomb.getY() == b.getY() && bomb.getZ() == b.getZ() ) return true;
		}
		
		return false;
	}
	
	public void disownBomb(Block b) {
		for ( int i = 0; i < bombs.size(); i++ ) {
			Block bomb = bombs.get(i).block;
			if ( bomb.getX() == b.getX() && bomb.getY() == b.getY() && bomb.getZ() == b.getZ() ) {
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
		wins = 0;
		
		maxBombs = 1;
		bombs = new Vector<Bomb>();
		
		hasDied = false;
		isReady = false;
	}
}
