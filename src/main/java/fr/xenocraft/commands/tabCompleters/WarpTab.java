package fr.xenocraft.commands.tabCompleters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import fr.xenocraft.Main;

public class WarpTab implements TabCompleter {
	
	private Main plugin;
	
	public WarpTab(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) return null;

		if (args.length > 1) return new ArrayList<>();

		ConfigurationSection warpSection = plugin.getConfig().getConfigurationSection("warp");
		List<String> warpList;

		if (warpSection == null) {
			warpList = new ArrayList<>();

		} else {
			warpList = new ArrayList<String>(warpSection.getKeys(false));
			
			for (int i = 0; i < warpList.size(); i++) {
				if (!(warpList.get(i).contains(args[0]))) {
					warpList.remove(i);
					i -= 1;
				}
			}

		}
		return warpList;
	}
	
}
