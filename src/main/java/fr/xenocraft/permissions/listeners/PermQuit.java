package fr.xenocraft.permissions.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

import fr.xenocraft.Main;
import fr.xenocraft.permissions.Permissions;

public class PermQuit implements Listener {

	@SuppressWarnings("unused")
	private Main plugin;

	public PermQuit(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {

		Player p = e.getPlayer();

		PermissionAttachment attachment = Permissions.permAttachments.remove(p.getUniqueId());
		p.removeAttachment(attachment);
	}

}
