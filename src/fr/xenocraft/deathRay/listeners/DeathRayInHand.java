package fr.xenocraft.deathRay.listeners;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.xenocraft.Main;
import fr.xenocraft.deathRay.services.DeathRayStats;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class DeathRayInHand implements Listener {

	private Main plugin;
	public Set<UUID> holdingRay = new HashSet<UUID>();

	public DeathRayInHand(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onHeldItem(PlayerItemHeldEvent e) {
		Player p = e.getPlayer();
		ItemStack item = p.getInventory().getItem(e.getNewSlot());

		testRay(p, item);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		ItemStack item = p.getInventory().getItemInMainHand();

		testRay(p, item);
	}


	public void testRay(Player p, ItemStack item) {

		UUID uuid = p.getUniqueId();

		if (item != null && item.getType() == Material.BOW) {

			ItemMeta meta = item.getItemMeta();

			if (meta.getDisplayName().equals("§cDeath Ray")) {

				if (holdingRay.contains(uuid)) return;
				holdingRay.add(uuid);

				new BukkitRunnable() {
					@Override
					public void run() {

						if (!holdingRay.contains(uuid)) {
							this.cancel();
							return;
						}
						actionbar(p);

					}
				}.runTaskTimer(plugin, 0, 20);
				
			} else
				holdingRay.remove(uuid);
		} else {
			holdingRay.remove(uuid);
		}

	}


	public void actionbar(Player p) {

		ItemStack item = p.getInventory().getItemInMainHand();
		if (item.getItemMeta() == null || !item.getItemMeta().getDisplayName().equals("§cDeath Ray")) return;

		DeathRayStats stats = new DeathRayStats(plugin, p.getInventory().getItemInMainHand());

		String message;
		if (stats.energy < stats.energyPerShot)
			message = "§6Power: §c" + stats.power + "§e - Energy: §b" + stats.energy + "§e ⚡ §cOut of energy!";
		else
			message = "§6Power: §c" + stats.power + "§e - Energy: §b" + stats.energy + "§e ⚡";

		p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
	}
}
