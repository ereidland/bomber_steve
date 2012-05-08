package net.evtr.bombersteve;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EntityListener implements Listener {
	BomberSteve plugin;
	
	@EventHandler
	public void playerLeft(PlayerQuitEvent event) {
		plugin.removeMissingPlayers();
	}
	
	@EventHandler
	public void playerDamageEvent(EntityDamageEvent event) {
		if ( event.getEntityType() == EntityType.PLAYER ) {
			BomberPlayer player = plugin.getPlayer((Player)event.getEntity());
			BomberGame game = plugin.getGame(player.gameID);
			
			if ( game != null ) {
				switch ( event.getCause() ) {
					case FIRE:
						break;
					default:
						event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void playerBurned(EntityCombustEvent event) {
		try {
			if ( event.getEntity() != null && event.getEntity().getType() == EntityType.PLAYER) {
				BomberPlayer player = plugin.getPlayer((Player)event.getEntity());
				
				if ( player.hasDied ) return;
				
				BomberGame game = plugin.getGame(player.gameID);
				if ( game != null ) {
					BomberPlayer damager = game.getDamageOwner(player.player.getLocation().getBlockX(), player.player.getLocation().getBlockZ());
					//player.player.damage(9001, damager != null ? damager.player : player.player);
					player.killer = damager; 
					player.hasDied = true;
					player.player.damage(9001);
				}
			}
			event.setCancelled(true);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void playerDied(PlayerDeathEvent event) {
		BomberPlayer player = plugin.getPlayer((Player)event.getEntity());
		
		BomberGame game = plugin.getGame(player.gameID);
		
		if ( game != null ) {
			BomberPlayer killer = player.killer;
			if ( killer == null ) {
				event.setDeathMessage(ChatColor.RED + player.player.getDisplayName() + ChatColor.GOLD + " magically died in game " + ChatColor.GREEN + player.gameID + ChatColor.GOLD + ".");
			} else if ( killer != player) {
				killer.points++;
				event.setDeathMessage(ChatColor.GREEN + killer.player.getDisplayName() + ChatColor.GOLD + " defeated " + ChatColor.RED + player.player.getDisplayName() + ChatColor.GOLD + " in game " + ChatColor.GREEN + killer.gameID + ChatColor.GOLD + "." );
			} else {
				event.setDeathMessage(ChatColor.RED + killer.player.getDisplayName() + ChatColor.GOLD + " blew theirself up in game " + ChatColor.GREEN + killer.gameID + ChatColor.GOLD + ".");
			}
			player.hasDied = true;
		}
	}
	
	@EventHandler
	public void creatureSpawn(CreatureSpawnEvent event) {
		if ( plugin.containsBlock(event.getLocation().getBlock()) ) {			
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void entityPrimed(ExplosionPrimeEvent event) {
		if ( plugin.containsBlock(event.getEntity().getLocation().getBlock()) ) {
			event.setCancelled(true);
		}
	}
	
	public EntityListener(BomberSteve plugin) {
		this.plugin = plugin;
	}
}
