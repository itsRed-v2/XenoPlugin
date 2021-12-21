package fr.xenocraft.permissions.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachment;

import fr.xenocraft.Main;
import fr.xenocraft.permissions.Permissions;

public class PermJoin implements Listener {

	private Main plugin;

	public PermJoin(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {

		Player p = e.getPlayer();
		
		PermissionAttachment attachment = p.addAttachment(plugin);
		Permissions.permAttachments.put(p.getUniqueId(), attachment);
	}

}
