package fr.xenocraft.teleporters.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

import fr.xenocraft.Main;

public class TpJoin implements Listener {

	private Main plugin;

	public TpJoin(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {

		e.getPlayer().getPersistentDataContainer().set(new NamespacedKey(plugin, "teleporter"),
				PersistentDataType.STRING, "null");

	}

}
