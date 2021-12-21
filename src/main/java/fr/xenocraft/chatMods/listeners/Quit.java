package fr.xenocraft.chatMods.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.xenocraft.Main;

public class Quit implements Listener {
	
	private Main plugin;
	
	public Quit(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void OnQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		event.setQuitMessage(plugin.getConfig().getString("leave_msg").replace("%player%", p.getDisplayName()));
	}

}
