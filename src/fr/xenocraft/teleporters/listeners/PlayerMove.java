package fr.xenocraft.teleporters.listeners;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import fr.xenocraft.Main;
import fr.xenocraft.teleporters.services.InitializeTp;
import fr.xenocraft.teleporters.services.Teleporter;

public class PlayerMove implements Listener {

	private Main plugin;

	public PlayerMove(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void OnMove(PlayerMoveEvent e) {

		if (e.getFrom().toVector().equals(e.getTo().toVector())) return;

		Player p = e.getPlayer();

		for (Teleporter tp : InitializeTp.teleportsMap.values()) {

			if (BoundingBox.of(tp.middleLoc, .5, 1.5, .5).contains(p.getLocation().toVector())) {

				PersistentDataContainer data = p.getPersistentDataContainer();
				NamespacedKey dataKey = new NamespacedKey(plugin, "teleporter");

				if (data.get(dataKey, PersistentDataType.STRING).equals("null")) enterTp(p, tp);
				if (data.get(dataKey, PersistentDataType.STRING).equals("immune")) outTp(p, tp);

				return;
			}
		}
		
	}

	private void enterTp(Player p, Teleporter tp) {

		PersistentDataContainer data = p.getPersistentDataContainer();
		NamespacedKey key = new NamespacedKey(plugin, "teleporter");
		
		data.set(key, PersistentDataType.STRING, tp.id);
		
		Teleporter destinationTp = InitializeTp.teleportsMap.get(tp.destinationId);
		
		if (destinationTp == null)
			noDestination(p, tp);
		else
			startTp(p, tp, data, key, destinationTp);
		
	}

	private void startTp(Player p, Teleporter tp, PersistentDataContainer data, NamespacedKey key,
			Teleporter destinationTp) {

		p.sendMessage("§7[§6Space-time network§7]§r Starting transportation from");
		p.sendMessage(" " + tp.displayName + "§r to " + destinationTp.displayName + "§r.");
		p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
		p.playSound(p.getLocation(), Sound.BLOCK_BEACON_AMBIENT, 2, 2);

		new BukkitRunnable() {

			private int progress = 0;
			private String subTitle = "§7Destination: " + destinationTp.displayName;

			@Override
			public void run() {

				progress += 1;
				String title = "";
				for (int i = 0; i < 50; i++) {
					if (i < progress)
						title += "§a|";
					else
						title += "§8|";
				}

				p.sendTitle(title, subTitle, 0, 3, 10);
				
				if (progress > 50) {
					data.set(key, PersistentDataType.STRING, "immune");
					p.sendTitle(title, subTitle, 0, 2, 20);
					p.getWorld().spawnParticle(Particle.PORTAL, tp.middleLoc, 100, 0, .5, 0, 1);

					if (destinationTp.forceRotation) {
						p.teleport(destinationTp.bottomLoc);
					} else {
						Location destination = destinationTp.bottomLoc.clone();
						destination.setYaw(p.getLocation().getYaw());
						destination.setPitch(p.getLocation().getPitch());
						p.teleport(destination);
					}
					p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
					p.getWorld().spawnParticle(Particle.REVERSE_PORTAL, p.getLocation().add(0, 1, 0), 50, 0, .5, 0, 1);

					this.cancel();
					return;
				}

				if (!BoundingBox.of(tp.middleLoc, .5, 1.5, .5).contains(p.getLocation().toVector())) {
					data.set(key, PersistentDataType.STRING, "null");
					p.sendMessage("§7[§6Space-time network§7]§r Transportation cancelled.");
					p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, .8f);

					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}

	private void noDestination(Player p, Teleporter tp) {

		p.sendMessage("§7[§6Space-time network§7]§r This teleporter has no destination.");
		p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, .5f);

		new BukkitRunnable() {

			@Override
			public void run() {
				p.sendTitle("§c----------", "§7No destination", 0, 5, 10);

				if (!BoundingBox.of(tp.middleLoc, .5, 1.5, .5).contains(p.getLocation().toVector())) {
					p.getPersistentDataContainer().set(new NamespacedKey(plugin, "teleporter"),
							PersistentDataType.STRING, "null");
					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 0, 3);
	}

	private void outTp(Player p, Teleporter tp) { // Method to rename?

		new BukkitRunnable() {
			@Override
			public void run() {
				if (!BoundingBox.of(tp.middleLoc, .5, 1.5, .5).contains(p.getLocation().toVector())) {
					p.getPersistentDataContainer().set(new NamespacedKey(plugin, "teleporter"),
							PersistentDataType.STRING, "null");
					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 0, 5);
	}
}
