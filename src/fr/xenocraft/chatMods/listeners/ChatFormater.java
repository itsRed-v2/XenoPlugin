package fr.xenocraft.chatMods.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.xenocraft.Main;

public class ChatFormater implements Listener {
	
	private Main plugin;
	
	public ChatFormater(Main plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
    @EventHandler
    public void chatFormat(AsyncPlayerChatEvent e) {
    	
		e.setMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
    	
    	String config_format = plugin.getConfig().getString("chat_format");
		String format = config_format.replace("%player%", "%1$s").replace("%message%", "%2$s");
		e.setFormat(format);
    }
}