package fr.xenocraft.teleporters.services;

import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

public class AnimationRenderer extends BukkitRunnable {

	@Override
	public void run() {

		for (Teleporter tp : InitializeTp.teleportsMap.values()) {

			tp.loop += .05;
			if (tp.loop > Math.PI * 2) tp.loop = 0;

			double x = tp.middleLoc.getX();
			double y = tp.middleLoc.getY();
			double z = tp.middleLoc.getZ();

			double yOffset = Math.cos(tp.loop);
			double xOffset = Math.sin(tp.loop * 4) / 2f;
			double zOffset = Math.cos(tp.loop * 4) / 2f;

			tp.getWorld().spawnParticle(Particle.END_ROD, x, y, z, 1, 0, 1, 0, 0);
			tp.getWorld().spawnParticle(Particle.REDSTONE, x + xOffset, y + yOffset, z + zOffset, 1, 0, 0, 0, 0,
					new Particle.DustOptions(tp.color, 1f));
		}

	}

}
