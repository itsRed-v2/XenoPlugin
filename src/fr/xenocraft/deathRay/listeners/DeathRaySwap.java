package fr.xenocraft.deathRay.listeners;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import fr.xenocraft.Main;
import fr.xenocraft.deathRay.guis.DeathRayMenu;

public class DeathRaySwap implements Listener {

	private Main plugin;

	public DeathRaySwap(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onHandSwap(PlayerSwapHandItemsEvent e) {

		ItemStack item = e.getOffHandItem();

		if (item.getType() == Material.BOW && item.getItemMeta().getDisplayName().equals("§cDeath Ray")) {
			e.setCancelled(true);

			Player p = e.getPlayer();

			if (UseDeathRay.drawing.contains(p.getUniqueId())) return;

			p.playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
			p.openInventory(new DeathRayMenu(plugin).openGUI(item));
		}
	}

}
