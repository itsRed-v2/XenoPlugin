package fr.xenocraft.commands;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import fr.xenocraft.Main;

public class Test implements CommandExecutor {
	
	private Main plugin;

	public Test(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) return true;

		//sender.sendMessage("This command don't do anything! :D");
		Player p = (Player) sender;
		
		PersistentDataContainer data = p.getPersistentDataContainer();

		data.remove(new NamespacedKey(plugin, "vsm.amount"));
		data.remove(new NamespacedKey(plugin, "vsm.page"));
		data.remove(new NamespacedKey(plugin, "vsm.item_index"));
		data.remove(new NamespacedKey(plugin, "vsm.lastnetwork"));

		p.sendMessage(data.get(new NamespacedKey(plugin, "teleporter"), PersistentDataType.STRING));
		
		return true;
	}
}
