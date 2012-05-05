package net.evtr.bombersteve;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class BomberSteve extends JavaPlugin {
	
	private EntityListener entityListener;
	private BlockListener blockListener;
	
	private Timer logicTimer;
	
	public Logger log = Logger.getLogger("Minecraft");
	
	public java.util.Vector<BomberGame> games;
	public java.util.Vector<BomberPlayer> players;
	
	public int sizeX = 16, sizeY = 4, sizeZ = 16, density = 30, hDensity = 10, columnIncrement = 4;
	
	public void onEnable()
	{
		entityListener = new EntityListener(this);
		blockListener = new BlockListener(this);
		getServer().getPluginManager().registerEvents(blockListener, this);
		getServer().getPluginManager().registerEvents(entityListener, this);
		
		logicTimer = new Timer();
		logicTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				removeMissingPlayers();
				for ( int i = 0; i < games.size(); i ++) {
					games.get(i).onTimer();
				}
			}
		}, 1000, 1000);
	}
	
	public int getActiveGameCount() {
		int count = 0;
		for ( int i = 0; i < games.size(); i++ ) {
			if ( games.get(i).bStarted )  {
				count++;
			}
		}
		return count;
	}
	
	public BomberGame getGame(int id) {
		for ( int i = 0; i < games.size(); i++ ) {
			if ( games.get(i).getID() == id ) {
				return games.get(i);
			}
		}
		return null;
	}
	
	public int newID() {
		int id = 1;
		while ( getGame(id) != null ) {
			id++;
		}
		return id;
	}
	
	public void removeMissingPlayers() {
		java.util.Vector<BomberPlayer> toRemove = new java.util.Vector<BomberPlayer>();
		for ( int i = 0; i < players.size(); i++ ) {
			if ( !players.get(i).player.isOnline() ) {
				toRemove.add(players.get(i));
			}
		}
		
		for ( int i = 0; i < toRemove.size(); i++ ) {
			BomberPlayer rPlayer = toRemove.get(i);
			BomberGame game = getGame(rPlayer.gameID);
			if ( game != null ) {
				game.removePlayer(rPlayer);
			}
			players.remove(rPlayer);
		}
	}
	
	public BomberPlayer getPlayer(Player player) {
		for (int i = 0; i < players.size(); i++ ) {
			if ( players.get(i).player == player ) {
				return players.get(i);
			}
		}
		
		BomberPlayer newPlayer = new BomberPlayer(player, 0);
		players.add(newPlayer);
		return newPlayer;
	}
	
	public boolean isPlayerInGame(Player player) {
		BomberPlayer bsPlayer = getPlayer(player);
		return bsPlayer.gameID != 0;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		String command = cmd.getName();
		
		//essentially only allowing players to send messages
		if(!(sender instanceof Player))
		{
			return false;
		}
		
		Player player = (Player)sender;
		
		if ( command.equalsIgnoreCase("bs") ) {
			if ( args.length > 0 ) {
				if ( sender.isOp() ) {
					if ( args[0].equalsIgnoreCase("new") ) {
						BomberGame game = new BomberGame(this, newID(), player.getLocation().toVector().add(new Vector(-sizeX/2, -1, -sizeZ/2)), new Vector(sizeX, sizeY, sizeZ));
						game.hardDensity = hDensity;
						game.softDensity = density;
						game.hardSpacing = columnIncrement;
						games.add(game);
						sender.sendMessage(ChatColor.GREEN + "New game created with id " + game.getID() + ". Initializing region...");
						game.initRegion();
						sender.sendMessage(ChatColor.GREEN + "Adding complexity...");
						game.addComplexity();
						sender.sendMessage(ChatColor.GREEN + "Region initialized. Use /bs join " + ChatColor.GOLD + game.getID() + ChatColor.GREEN + " to join.");
					} else if ( args[0].equalsIgnoreCase("size") ) {
						try {
							if ( args.length > 3 ) {
								sizeX = Integer.valueOf(args[1]);
								sizeZ = Integer.valueOf(args[2]);
								sizeY = Integer.valueOf(args[3]);
							} else if ( args.length > 2) {
								sizeX = Integer.valueOf(args[1]);
								sizeZ = Integer.valueOf(args[2]);
							} else if ( args.length > 1 ) {
								sizeX = sizeZ = Integer.valueOf(args[1]);
							}
							
							if ( sizeX < 8 ) sizeX = 8;
							if ( sizeY < 4) sizeY = 4;
							if ( sizeZ < 8 ) sizeZ = 8;
							
							sender.sendMessage(ChatColor.YELLOW + "Current default region size: " + sizeX + "x" + sizeZ + " and " + sizeY + " tall.");
						} catch ( Exception e ) {
							e.printStackTrace();
							sender.sendMessage(ChatColor.RED + "Exception: " + e.getMessage());
						}
					} else if ( args[0].equalsIgnoreCase("density") ) {
						try {
							if ( args.length > 1 ) {
								density = Integer.valueOf(args[1]);
							}
							if ( density > 100 ) {
								density = 100;
							} else if ( density < 0 ) {
								density = 0;
							}
							sender.sendMessage(ChatColor.YELLOW + "Current default soft density: " + density);
						} catch ( Exception e ) {
							e.printStackTrace();
							sender.sendMessage(ChatColor.RED + "Exception: " + e.getMessage());
						}
					} else if ( args[0].equalsIgnoreCase("hdensity") ) {
						try {
							if ( args.length > 1 ) {
								hDensity = Integer.valueOf(args[1]);
							}
							if ( hDensity > 100 ) {
								hDensity = 100;
							} else if ( hDensity < 0 ) {
								hDensity = 0;
							}
							sender.sendMessage(ChatColor.YELLOW + "Current default hard (unbreakable) density: " + hDensity);
						} catch ( Exception e ) {
							e.printStackTrace();
							sender.sendMessage(ChatColor.RED + "Exception: " + e.getMessage());
						}
					} else if ( args[0].equalsIgnoreCase("increment") ) {
						try {
							if ( args.length > 1 ) {
								columnIncrement = Integer.valueOf(args[1]);
							}
							if ( columnIncrement > Math.max(sizeX, sizeZ) ) {
								columnIncrement = Math.max(sizeX, sizeZ);
							} else if ( columnIncrement < 2 ) {
								columnIncrement = 2;
							}
							sender.sendMessage(ChatColor.YELLOW + "Current default column increment: " + columnIncrement);
						} catch ( Exception e ) {
							e.printStackTrace();
							sender.sendMessage(ChatColor.RED + "Exception: " + e.getMessage());
						}
					}
				}
				BomberPlayer bsPlayer = getPlayer(player);
				if ( args[0].equalsIgnoreCase("join") ) {
					if ( args.length > 1 ) {
						if ( bsPlayer.gameID == 0 ) {
							try {
								int id = Integer.valueOf(args[1]);
								if ( id > 0 ) {
									BomberGame game = getGame(id);
									if ( game != null ) {
										if ( game.bStarted ) {
											sender.sendMessage(ChatColor.RED + "You cannot join game " + ChatColor.GREEN + id + ChatColor.RED + " because it is in progress.");
										} else {
											int numPlayers = game.players.size();
											getServer().broadcastMessage(ChatColor.GREEN + player.getDisplayName() + ChatColor.GOLD  + " joined game " + ChatColor.GREEN + id + ChatColor.GOLD + " (" + (numPlayers + 1) + " total).");
											game.addPlayer(bsPlayer);
											game.bringPlayer(bsPlayer);
										}
									} else {
										sender.sendMessage(ChatColor.RED + "Game id " + id + " does not exist.");
									}
								}
							} catch ( Exception e ) {
								sender.sendMessage(ChatColor.RED + "The format is /bs join <number>");
							}
						} else {
							sender.sendMessage(ChatColor.RED + "You are already in a game. Use /bs leave to leave.");
						}
					} else {
						int id = getPlayer(player).gameID;
						if ( id == 0 ) {
							sender.sendMessage(ChatColor.YELLOW + "Your current game ID is " + ChatColor.GREEN + id);
						} else {
							sender.sendMessage(ChatColor.YELLOW + "You are not in a game. Please use /bs join <gameid>");
						}
					}
				} else if ( args[0].equalsIgnoreCase("leave") ) {
					if ( bsPlayer.gameID != 0 ) {
						BomberGame game = getGame(bsPlayer.gameID);
						game.removePlayer(bsPlayer);
						bsPlayer.gameID = 0;
						sender.sendMessage(ChatColor.RED + "Leaver! :(");
					} else {
						sender.sendMessage(ChatColor.RED + "You are not even in a game. Use /bs join <gameid>");
					}
				}
			} else {
				int numGames = getActiveGameCount();
				sender.sendMessage(ChatColor.GREEN + "There " + (numGames == 0 || numGames != 1 ? "are " : "is ") + (numGames == 0 ? ChatColor.RED : ChatColor.GOLD) + numGames + " active " + (numGames == 0 || numGames != 1 ? "games." : "game."));
			}
			return true;
		}
		
		return false;
	}
	
	public BomberSteve() {
		logicTimer = null;
		entityListener = null;
		blockListener = null;
		games = new java.util.Vector<BomberGame>();
		players = new java.util.Vector<BomberPlayer>();
	}
}
