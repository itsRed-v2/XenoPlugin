package fr.xenocraft.permissions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import fr.xenocraft.Main;

public class Permissions {

	private Main plugin;
	public static Map<UUID, PermissionAttachment> permAttachments = new HashMap<UUID, PermissionAttachment>();

	public Permissions(Main plugin) {
		this.plugin = plugin;
	}
	
	public static void removeAllAttachments() {
		for (UUID uuid : permAttachments.keySet()) {

			Player p = Bukkit.getPlayer(uuid);
			System.out.println(p);

			PermissionAttachment attachment = Permissions.permAttachments.remove(uuid);
			p.removeAttachment(attachment);
		}
	}
	
	public void setAllAttachments() {

		for (Player p : Bukkit.getOnlinePlayers()) {

			PermissionAttachment attachment = p.addAttachment(plugin);
			Permissions.permAttachments.put(p.getUniqueId(), attachment);
		}
	}

}
