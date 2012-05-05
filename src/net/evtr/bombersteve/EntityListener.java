package net.evtr.bombersteve;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityCombustEvent;
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
	public void playerBurned(EntityCombustEvent event) {
		plugin.log.info("Combustion event!");
		return;/*
		if ( event.getEntity() != null && event.getEntity() instanceof Player) {
			BomberPlayer player = plugin.getPlayer((Player)event.getEntity());
			BomberGame game = plugin.getGame(player.gameID);
			if ( game != null ) {
				BomberPlayer damager = game.getDamageOwner(player.player.getLocation().getBlockX(), player.player.getLocation().getBlockZ());
				player.player.damage(9001, damager.player);
			}
		}*/
	}
	
	@EventHandler
	public void playerDied(PlayerDeathEvent event) {
		BomberPlayer player = plugin.getPlayer((Player)event.getEntity());
		BomberPlayer killer = player.player.getKiller() != null ? plugin.getPlayer(player.player.getKiller()) : null;
		
		if ( killer != null ) {
			killer.points++;
			event.setDeathMessage(ChatColor.GREEN + killer.player.getDisplayName() + ChatColor.GOLD + " defeated (in game " + ChatColor.GREEN + killer.gameID + ChatColor.GOLD + ") " + ChatColor.RED + player.player.getDisplayName());
		} else {
			event.setDeathMessage(ChatColor.RED + player.player.getDisplayName() + ChatColor.GOLD + " magically died.");
		}
		
		player.hasDied = true;
	}
	
	@EventHandler
	public void creatureSpawn(CreatureSpawnEvent event) {
		if(event.getSpawnReason() != SpawnReason.SPAWNER_EGG) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void entityPrimed(ExplosionPrimeEvent event) {
		plugin.log.info("Entity primed! Canceled.");
		event.setCancelled(true);
	}
	
	public EntityListener(BomberSteve plugin) {
		this.plugin = plugin;
	}
}
