package net.evtr.bombersteve;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class BomberNPC {
	public static final EntityType entityType = EntityType.CHICKEN;
	public Entity ent;
	
	public BomberNPC(Location loc) {
		try {
			ent = loc.getWorld().spawnCreature(loc, entityType);
		} catch ( Exception e ) {
			e.printStackTrace();
			ent = null;
		}
	}
}
