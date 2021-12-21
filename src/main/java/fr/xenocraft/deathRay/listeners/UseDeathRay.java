package fr.xenocraft.deathRay.listeners;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import fr.xenocraft.Main;
import fr.xenocraft.deathRay.services.DeathRayStats;

public class UseDeathRay implements Listener {
	
	private Main plugin;
	public static Set<UUID> drawing = new HashSet<UUID>();

	public UseDeathRay(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onDraw(PlayerInteractEvent e) {
		if (e.getMaterial() == Material.BOW && e.getItem().getItemMeta().getDisplayName().equals("§cDeath Ray")
				&& (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {

			Player p = e.getPlayer();

			DeathRayStats stats = new DeathRayStats(plugin, e.getItem());

			if (stats.energy < stats.energyPerShot) {
				p.getWorld().playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
				return;
			}

			UUID uuid = p.getUniqueId();
			if (drawing.contains(uuid)) return;
			drawing.add(uuid);

			p.getWorld().playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 2, 2);

			Color rayColor = rayColorFromStats(stats.power, stats.destroyBlocks);

			new BukkitRunnable() {
				@Override
				public void run() {

					if (e.getHand() == EquipmentSlot.HAND && !p.getInventory().getItemInMainHand().equals(e.getItem()))
						drawing.remove(uuid);
					else if (e.getHand() == EquipmentSlot.OFF_HAND
							&& !p.getInventory().getItemInOffHand().equals(e.getItem()))
						drawing.remove(uuid);

					if (!drawing.contains(uuid)) this.cancel();

					RayTraceResult traceResult = p.rayTraceBlocks(150);

					if (traceResult == null) return;

					float Xoffset = 0;
					if (traceResult.getHitBlockFace() == BlockFace.EAST) Xoffset = .07f;
					if (traceResult.getHitBlockFace() == BlockFace.WEST) Xoffset = -.07f;

					float Yoffset = 0;
					if (traceResult.getHitBlockFace() == BlockFace.UP) Yoffset = .05f;
					if (traceResult.getHitBlockFace() == BlockFace.DOWN) Yoffset = -.1f;

					float Zoffset = 0;
					if (traceResult.getHitBlockFace() == BlockFace.SOUTH) Zoffset = .07f;
					if (traceResult.getHitBlockFace() == BlockFace.NORTH) Zoffset = -.07f;

					for (int a = -6; a <= 6; a += 2) {
						if (!(traceResult.getHitBlockFace() == BlockFace.UP
								|| traceResult.getHitBlockFace() == BlockFace.DOWN)) {
							p.getWorld().spawnParticle(Particle.REDSTONE, traceResult.getHitPosition().getX() + Xoffset,
									traceResult.getHitPosition().getY() + a / 10.0,
									traceResult.getHitPosition().getZ() + Zoffset,
									1, 0, 0, 0, 1, new Particle.DustOptions(rayColor, .7f), true);
						}
						if (!(traceResult.getHitBlockFace() == BlockFace.EAST
								|| traceResult.getHitBlockFace() == BlockFace.WEST)) {
							p.getWorld().spawnParticle(Particle.REDSTONE,
									traceResult.getHitPosition().getX() + a / 10.0,
									traceResult.getHitPosition().getY() + Yoffset,
									traceResult.getHitPosition().getZ() + Zoffset, 1, 0, 0, 0, 1,
									new Particle.DustOptions(rayColor, .7f), true);
						}
						if (!(traceResult.getHitBlockFace() == BlockFace.SOUTH
								|| traceResult.getHitBlockFace() == BlockFace.NORTH)) {
							p.getWorld().spawnParticle(Particle.REDSTONE, traceResult.getHitPosition().getX() + Xoffset,
									traceResult.getHitPosition().getY() + Yoffset,
									traceResult.getHitPosition().getZ() + a / 10.0, 1, 0, 0, 0, 1,
									new Particle.DustOptions(rayColor, .7f), true);
						}
					}

				}
			}.runTaskTimer(plugin, 0, 1);
		}
	}

	@EventHandler
	public void onShoot(EntityShootBowEvent e) {

		if (!(e.getEntity() instanceof Player)) return;
		if (!(e.getBow().getItemMeta().getDisplayName().equals("§cDeath Ray"))) return;

		e.setCancelled(true);

		Player p = (Player) e.getEntity();
		drawing.remove(p.getUniqueId()); // Removing player from the drawing lists

		if (!(e.getForce() == 1)) return; // Cancel if the shot is not fully charged

		RayTraceResult traceResult = p.rayTraceBlocks(150);

		// Cancel if player is pointing too far/nowhere
		if (traceResult == null) {
			p.sendMessage("You're shooting too far!");
			return;
		}

		ItemStack raygun = e.getBow();
		ItemMeta meta = raygun.getItemMeta();
		PersistentDataContainer data = meta.getPersistentDataContainer();
		DeathRayStats stats = new DeathRayStats(plugin, raygun);

		if (stats.energy < stats.energyPerShot) return;

		// Playing cool laser sound
		p.getWorld().playSound(p.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 2, 2);

		// Creating explosion
		Vector pos = traceResult.getHitPosition();
		Location impactLoc = new Location(p.getWorld(), pos.getX(), pos.getY(), pos.getZ());

		p.getWorld().createExplosion(impactLoc, stats.power, stats.setFire, stats.destroyBlocks, p);

		// Using energy
		data.set(new NamespacedKey(plugin, "energy"), PersistentDataType.INTEGER, stats.energy - stats.energyPerShot);
		raygun.setItemMeta(meta);

		// Rendering particle ray
		float particleFrequency = .1f;
		double distance = p.getLocation().distance(impactLoc);
		Vector direction = p.getLocation().getDirection().multiply(particleFrequency);
		Location laserDraw = p.getEyeLocation();

		Color rayColor = rayColorFromStats(stats.power, stats.destroyBlocks);

		for (int i = 0; i < distance * (1 / particleFrequency); i++) {
			laserDraw.add(direction);

			p.getWorld().spawnParticle(Particle.REDSTONE, laserDraw, 1, 0, 0, 0, 1,
					new Particle.DustOptions(rayColor, .5f), true);
		}

		// Updating action bar stats message
		// and making sure that the player is listed as holding the ray
		new DeathRayInHand(plugin).actionbar(p);
		new DeathRayInHand(plugin).testRay(p, raygun);

		// Updating lore
		stats.renderLore(raygun);

	}

	private Color rayColorFromStats(int power, boolean destroyBlocks) {
		Color rayColor;

		if (power == 0)
			rayColor = Color.fromRGB(128, 241, 255);
		else if (destroyBlocks == false)
			rayColor = Color.fromRGB(255, 150, 0);
		else
			rayColor = Color.RED;

		return rayColor;
	}	
}