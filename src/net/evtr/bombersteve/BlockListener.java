package net.evtr.bombersteve;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockListener implements Listener {
	BomberSteve plugin;
	
	@EventHandler
	public void placeTNT(PlayerInteractEvent event) {
		if ( event.getAction() == Action.RIGHT_CLICK_BLOCK ) {
			BomberGame game = plugin.getGame(plugin.getPlayer(event.getPlayer()).gameID);
			Player player = event.getPlayer();
			Block modBlock = player.getWorld().getBlockAt(event.getClickedBlock().getX() + event.getBlockFace().getModX(), event.getClickedBlock().getY() + event.getBlockFace().getModY(), event.getClickedBlock().getZ() + event.getBlockFace().getModZ());
			if ( modBlock.getType() == Material.AIR && game != null ) {
				game.placeBomb(plugin.getPlayer(player), modBlock.getX(), game.getBombY(), modBlock.getZ());
			} else {
				player.sendMessage(ChatColor.RED + "Can't place bomb!");
			}
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
