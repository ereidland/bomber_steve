package net.evtr.bombersteve;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockListener implements Listener {
	BomberSteve plugin;
	
	@EventHandler
	public void placeTNT(PlayerInteractEvent event) {
		BomberGame game = plugin.getGame(plugin.getPlayer(event.getPlayer()).gameID);
		Player player = event.getPlayer();
		if ( game != null ) {
			game.placeBomb(plugin.getPlayer(player), player.getLocation().getBlockX(), game.getBombY(), player.getLocation().getBlockZ());
		}
		event.setCancelled(true);
	}
	
	@EventHandler
	public void stopPlacement(BlockPlaceEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void stopBreaking(BlockBreakEvent event) {
		//if ( plugin.isPlayerInGame(event.getPlayer()) ) {
			event.setCancelled(true);
		//}
		
	}
	
	public BlockListener(BomberSteve plugin) {
		this.plugin = plugin;
	}
}
