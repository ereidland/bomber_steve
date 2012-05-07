package net.evtr.bombersteve;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class BomberGame {
	public java.util.Vector<BomberPlayer> players;
	private int id;
	public boolean bStarted;
	
	private Vector bottomLeft, size;
	private BomberPlayer[][] damageOwner;
	
	public BomberSteve plugin;
	public World world;
	
	public int hardDensity, softDensity, hardSpacing;
	
	public Random random;
	
	public int getID() {
		return id;
	}
	
	public void removePlayer(BomberPlayer player) {
		players.remove(player);
	}
	public void addPlayer(BomberPlayer player) {
		if ( !players.contains(player) ) {
			players.add(player);
		}
		player.gameID = id;
	}
	
	public BomberPlayer getBombOwner(Block b) {
		BomberPlayer owner = null;
		for ( int i = 0; i < players.size(); i++ ) {
			if ( players.get(i).ownsBomb(b) ) {
				owner = players.get(i);
			}
		}
		return owner;
	}
	
	public boolean hasPlayer(BomberPlayer player) {
		return players.contains(player);
	}
	
	public int getBombY() {
		return bottomLeft.getBlockY() + 1;
	}
	
	public boolean containsBlock(int x, int y, int z) {
		return x >= bottomLeft.getBlockX() && x <= bottomLeft.getBlockX() + size.getBlockX()
			&& y >= bottomLeft.getBlockY() && y <= bottomLeft.getBlockY() + size.getBlockY()
			&& z >= bottomLeft.getBlockZ() && z <= bottomLeft.getBlockZ() + size.getBlockZ();
	}
	
	public boolean containsBlock(Block b) {
		return containsBlock(b.getX(), b.getY(), b.getZ());
	}
	
	public void stopGame() {
		if ( bStarted ) {
			bStarted = false;
			removeBombs();
			clearRegion();
			resetDeaths();
		}
	}
	
	public void clearColumn(int x, int z) {
		for ( int y = getBombY(); y < bottomLeft.getBlockY() + size.getBlockY() - 1; y++ ) {
			world.getBlockAt(x, y, z).setType(Material.AIR);
		}
	}
	
	public boolean checkForEnd() {
		if ( !bStarted ) return false;
		int numLiving = 0;
		BomberPlayer possibleWinner = null;
		for ( int i = 0; i < players.size(); i++ ) {
			if ( players.get(i).hasDied == false ) {
				numLiving++;
				possibleWinner = players.get(i);
			}
		}
		
		boolean gameEnded = numLiving <= 1;
		if ( gameEnded ) {
			if ( possibleWinner != null ) {
				possibleWinner.wins++;
				plugin.getServer().broadcastMessage(ChatColor.GREEN + possibleWinner.player.getDisplayName() + ChatColor.GOLD + " won game " + ChatColor.GREEN + id + ChatColor.GOLD + "!" + ChatColor.GREEN + " (" + possibleWinner.wins + (possibleWinner.wins == 0 || possibleWinner.wins != 1 ? " wins" : " win" ) + " total).");
			} else {
				plugin.getServer().broadcastMessage(ChatColor.GOLD + "Game " + ChatColor.GREEN + id + ChatColor.GOLD + " ended in a draw!");
			}
			
			bStarted = false;
			removeBombs();
			clearRegion();
			resetDeaths();
		}
		
		return gameEnded;
	}
	
	public void resetDeaths() {
		for ( int i = 0; i < players.size(); i++ ) {
			players.get(i).hasDied = false;
		}
	}
	
	public void removeBombs() {
		for ( int i = 0; i < players.size(); i++ ) {
			players.get(i).bombs.clear();
		}
	}
	
	public boolean bombBlock(Block b, BomberPlayer player) {
		boolean continueBombing = false, setDamageOwner = true;
		switch (b.getType() ) {
			case FIRE:
				continueBombing = true;
				break;
			case COBBLESTONE:
				clearColumn(b.getX(), b.getZ());
				b.setType(Material.FIRE);
				break;
			case AIR:
				b.setType(Material.FIRE);
				continueBombing = true;
				break;
			case TNT:
				BomberPlayer owner = getBombOwner(b);
				if ( owner != null ) {
					owner.disownBomb(b);
					continueBombing = false;
					setDamageOwner = false;
					detonateBomb(owner, b.getX(), b.getZ(), owner.range);
				} else {
					b.setType(Material.FIRE);
					setDamageOwner = true;
					continueBombing = true;
				}
				break;
			default:
				setDamageOwner = false;
		}
		if ( setDamageOwner ) {
			setDamageOwner(b.getX(), b.getZ(), player);
		}
		return continueBombing;
	}
	
	public BomberPlayer getDamageOwner(int x, int z) {
		x -= bottomLeft.getBlockX();
		z -= bottomLeft.getBlockZ();
		if ( x >= 0 && x < size.getBlockX() && z >= 0 && z < size.getBlockZ() ) {
			return damageOwner[x][z];
		}
		return null;
	}
	public void setDamageOwner(int x, int z, BomberPlayer player) {
		x -= bottomLeft.getBlockX();
		z -= bottomLeft.getBlockZ();
		if ( x >= 0 && x < size.getBlockX() && z >= 0 && z < size.getBlockZ() ) {
			damageOwner[x][z] = player;
		}
	}
	
	public void detonateBomb(BomberPlayer player, int x, int z, int range) {
		world.getBlockAt(x, getBombY(), z).setType(Material.AIR);
		world.createExplosion(x, getBombY(), z, 0);
		for ( int fx = x; fx <= x + range; fx++ ) {
			if ( !bombBlock(world.getBlockAt(fx, getBombY(), z), player) )
				break;
		}
		for ( int fx = x - 1; fx >= x - range; fx-- ) {
			if ( !bombBlock(world.getBlockAt(fx, getBombY(), z), player) )
				break;
		}
		
		for ( int fz = z + 1; fz <= z + range; fz++ ) {
			if ( !bombBlock(world.getBlockAt(x, getBombY(), fz), player) )
				break;
		}
		for ( int fz = z - 1; fz >= z - range; fz-- ) {
			if ( !bombBlock(world.getBlockAt(x, getBombY(), fz), player) )
				break;
		}
	}
	
	public void startGame(){
		if ( !bStarted && players.size() > 0 ) {
			addComplexity();
			bStarted = true;
			resetDeaths();
			
			for ( int i = 0; i < players.size(); i++ ) {
				bringPlayer(players.get(i));
			}
		}
	}
	
	public Vector getBottomLeft() {
		return bottomLeft;
	}
	public Vector getSize() {
		return size;
	}
	
	public void deleteEverything() {
		bStarted = false;
		removeBombs();
		resetDeaths();
		for ( int x = bottomLeft.getBlockX(); x <= bottomLeft.getBlockX() + size.getBlockX(); x++ ) {
			for ( int z = bottomLeft.getBlockZ(); z <= bottomLeft.getBlockZ() + size.getBlockZ(); z++ ) {
				for ( int y = bottomLeft.getBlockY(); y <= bottomLeft.getBlockY() + size.getBlockY(); y++ ) {
					world.getBlockAt(x, y, z).setType(Material.AIR);
				}
			}
		}
		players.clear();
	}
	
	public void onTimer() {
		if ( !bStarted || checkForEnd() ) return;
		for ( int x = bottomLeft.getBlockX(); x < bottomLeft.getBlockX() + size.getBlockX(); x++ ) {
			for ( int z = bottomLeft.getBlockZ(); z < bottomLeft.getBlockZ() + size.getBlockZ(); z++ ) {
				damageOwner[x - bottomLeft.getBlockX()][z - bottomLeft.getBlockZ()] = null;
				Block b = world.getBlockAt(x, getBombY(), z);
				if ( b.getType() == Material.FIRE ) {
					b.setType(Material.AIR);
				}
			}
		}
		for ( int i = 0; i < players.size(); i++ ) {
			BomberPlayer player = players.get(i);
			java.util.Vector<BomberPlayer.Bomb> toRemove = new java.util.Vector<BomberPlayer.Bomb>();
			for ( int j = 0; j < player.bombs.size(); j++ ) {
				BomberPlayer.Bomb bomb = player.bombs.get(j);
				bomb.timeLeft--;
				
				if ( bomb.timeLeft <= 0 ) {
					detonateBomb(player, bomb.block.getX(), bomb.block.getZ(), player.range);
					toRemove.add(bomb);
				}
			}
			
			for ( int j = 0; j < toRemove.size(); j++ ) {
				player.bombs.remove(toRemove.get(j));
			}
		}
	}
	
	public boolean placeBomb(BomberPlayer player, int x, int y, int z) {
		if ( bStarted && hasPlayer(player) && x > bottomLeft.getBlockX() && x < bottomLeft.getBlockX() + size.getBlockX() - 1 && z > bottomLeft.getBlockZ() && z < bottomLeft.getBlockZ() + size.getBlockZ() - 1 ) {
			if ( player.bombs.size() < player.maxBombs ) {
				Block b = world.getBlockAt(x, y, z);
				b.setType(Material.TNT);
				player.bombs.add(new BomberPlayer.Bomb(b, 3));
				return true;
			}
		}
		player.player.sendMessage(ChatColor.RED + "Can't place bomb!");
		return false;
	}
	
	public void addColumns(int spacing) {
		for ( int x = bottomLeft.getBlockX() + spacing; x < bottomLeft.getBlockX() + size.getBlockX(); x += spacing ) {
			for ( int z = bottomLeft.getBlockZ() + spacing; z < bottomLeft.getBlockZ() + size.getBlockZ(); z += spacing ) {
				for ( int y = bottomLeft.getBlockY() + 1; y < bottomLeft.getBlockY() + size.getBlockY() - 1; y++ ) {
					world.getBlockAt(x, y, z).setType(Material.BRICK);
				}
			}
		}
	}
	
	
	//Note: density is from 0 to 100
	public void addRandomBlocks(int density, boolean soft) {
		Random r = new Random();
		r.setSeed(System.currentTimeMillis());
		for ( int x = bottomLeft.getBlockX() + 1; x < bottomLeft.getBlockX() + size.getBlockX() - 1; x++ ) {
			for ( int z = bottomLeft.getBlockZ() + 1; z < bottomLeft.getBlockZ() + size.getBlockZ() - 1; z++ ) {
				if ( r.nextInt(99) < density ) {
					for ( int y = bottomLeft.getBlockY() + 1; y < bottomLeft.getBlockY() + size.getBlockY() - 1; y++ ) {
						Block b = world.getBlockAt(x, y, z);
						if ( b.getType() != Material.BRICK ) {
							b.setType(soft ? Material.COBBLESTONE : Material.BRICK);
						}
					}
				}
			}
		}
	}
	
	public void clearRegion() { 
		for ( int x = bottomLeft.getBlockX() + 1; x < bottomLeft.getBlockX() + size.getBlockX() - 1; x++ ) {
			for ( int z = bottomLeft.getBlockZ() + 1; z < bottomLeft.getBlockZ() + size.getBlockZ() - 1; z++ ) {
				for ( int y = bottomLeft.getBlockY() + 1; y < bottomLeft.getBlockY() + size.getBlockY() - 1; y++ ) {
					world.getBlockAt(x, y, z).setType(Material.AIR);
				}
			}
		}
	}
	
	public void initRegion() {
		boolean other = true;
		for ( int x = bottomLeft.getBlockX(); x < bottomLeft.getBlockX() + size.getBlockX(); x++ ) {
			other = !other;
			for ( int z = bottomLeft.getBlockZ(); z < bottomLeft.getBlockZ() + size.getBlockZ(); z++ ) {
				world.getBlockAt(x, bottomLeft.getBlockY(), z).setType(other ? Material.WOOD : Material.STONE);
				world.getBlockAt(x, bottomLeft.getBlockY() + size.getBlockY() - 1, z).setType(Material.GLASS);
				other = !other;
				for ( int y = bottomLeft.getBlockY() + 1; y < bottomLeft.getBlockY() + size.getBlockY() - 1; y++ ) {
					if ( z == bottomLeft.getBlockZ()
					  || z == bottomLeft.getBlockZ() + size.getBlockZ() - 1
					  || x == bottomLeft.getBlockX()
					  || x == bottomLeft.getBlockX() + size.getBlockX() - 1 ) {
						world.getBlockAt(x, y, z).setType(Material.GLASS);
					} else {
						world.getBlockAt(x, y, z).setType(Material.AIR);
					}
				}
			}
		}
		
		damageOwner = new BomberPlayer[size.getBlockX()][size.getBlockZ()];
	}
	public void addComplexity() {
		addRandomBlocks(hardDensity, false);
		addRandomBlocks(softDensity, true);
		addColumns(hardSpacing);
	}
	
	private void bringPlayer(BomberPlayer player, int numTries) {
		int bx = bottomLeft.getBlockX() + 1 + random.nextInt(size.getBlockX() - 2), by =  getBombY(), bz = bottomLeft.getBlockZ() + 1 + random.nextInt(size.getBlockZ() - 2);
		Block b = world.getBlockAt(bx, by, bz);
		if ( b.getType() == Material.AIR || numTries >= 10 ) {
			player.player.teleport(new Location(world, bx + 0.5, by, bz + 0.5));
			player.player.sendMessage(ChatColor.GOLD + "To arena " + ChatColor.GREEN + id + ChatColor.GOLD + "!");
		} else {
			bringPlayer(player, numTries + 1);
		}
	}
	
	public void bringPlayer(BomberPlayer player) {
		if ( hasPlayer(player)) {
			bringPlayer(player, 0);
		}
	}
	
	public BomberGame(BomberSteve plugin, int id, Vector bottomLeft, Vector size) {
		this.plugin = plugin;
		bStarted = false;
		this.id = id;
		players = new java.util.Vector<BomberPlayer>();
		
		this.bottomLeft = bottomLeft;
		this.size = size;
		
		world = plugin.getServer().getWorld("world");
		
		hardSpacing = 2;
		hardDensity = 10;
		softDensity = 25;
		
		random = new Random(System.currentTimeMillis());
	}
}
