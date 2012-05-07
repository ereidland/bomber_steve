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
			if ( plugin.containsBlock(modBlock) ) {
				event.setCancelled(true);
			}
			if ( modBlock.getType() == Material.AIR  ) {
				if ( game != null ) {
					game.placeBomb(plugin.getPlayer(player), modBlock.getX(), game.getBombY(), modBlock.getZ());
				}
			} else {
				player.sendMessage(ChatColor.RED + "Can't place bomb!");
			}
		}
		
	}
	
	@EventHandler
	public void stopPlacement(BlockPlaceEvent event) {
		if ( plugin.containsBlock(event.getBlock()) ) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void stopBreaking(BlockBreakEvent event) {
		if ( plugin.containsBlock(event.getBlock()) ) {
			event.setCancelled(true);
		}
	}
	
	public BlockListener(BomberSteve plugin) {
		this.plugin = plugin;
	}
}
