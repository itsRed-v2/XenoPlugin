package fr.xenocraft.commands;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import fr.xenocraft.Main;
import fr.xenocraft.virtualStorages.guis.VsmUI;

public class OpenMaterializer implements CommandExecutor {
	
	private Main plugin;
	
	public OpenMaterializer(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			return true;
		}
		
		Player p = (Player) sender;
		
		if (args.length == 0) {
			p.sendMessage("§cYou must specify a materializer");
			return false;
		}
		
		if (!plugin.getConfig().getConfigurationSection("materializers").contains(args[0])) {
			p.sendMessage("§cError: No materializer matches the argument");
			return true;
		}
		
		ConfigurationSection customConfig = plugin.getCustomConfig();
		if (!customConfig.contains("virtualStorages")) {
			customConfig.createSection("virtualStorages");
		}

		ConfigurationSection virtualStorages = customConfig.getConfigurationSection("virtualStorages");
		if (!virtualStorages.contains(args[0])) {
			virtualStorages.createSection(args[0]);
			virtualStorages.set(args[0], new ArrayList<Map<String, Object>>());
		}
		
		PersistentDataContainer data = p.getPersistentDataContainer();
		if (!data.has(new NamespacedKey(plugin, "vsm.sort"), PersistentDataType.STRING)) {
			data.set(new NamespacedKey(plugin, "vsm.sort"), PersistentDataType.STRING, "add_order");
		}
		
		plugin.saveCustomConfig();
		
		p.openInventory(new VsmUI(plugin).GUI(p, args[0], 0));
		
		return true;
	}
}
