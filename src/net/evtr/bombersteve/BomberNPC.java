package net.evtr.bombersteve;

import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;

public class BomberNPC {
	public static final EntityType entityType = EntityType.CREEPER;
	public Creeper ent;
	public int range;
	
	public BomberNPC(Location loc) {
		try {
			ent = (Creeper)loc.getWorld().spawnCreature(loc, entityType);
		} catch ( Exception e ) {
			e.printStackTrace();
			ent = null;
		}
		range = 4;
	}
}
