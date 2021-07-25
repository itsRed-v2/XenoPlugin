package fr.xenocraft.commands.tabCompleters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import fr.xenocraft.Main;
import fr.xenocraft.teleporters.services.InitializeTp;

public class TeleporterCmdTab implements TabCompleter {

	@SuppressWarnings("unused")
	private Main plugin;

	public TeleporterCmdTab(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

		List<String> list = new ArrayList<String>();

		if (args.length == 1) {
			list.add("list");
			list.add("info");
			list.add("modify");
			list.add("create");
			list.add("remove");
		}

		if (args.length == 2) {

			if (args[0].equals("info") || args[0].equals("remove") || args[0].equals("modify")) {
				list = new ArrayList<String>(InitializeTp.teleportsMap.keySet());
			}

		}

		if (args.length == 3) {
			if (args[0].equals("modify")) {
				list.add("world");
				list.add("position");
				list.add("rotation");
				list.add("forceRotation");
				list.add("color");
				list.add("displayName");
				list.add("destination");
			}
		}

		if (args.length == 4) {

			if (args[2].equals("forceRotation")) {
				list.add("true");
				list.add("false");
			}

			if (args[2].equals("destination")) {
				list = new ArrayList<String>(InitializeTp.teleportsMap.keySet());
				list.remove(args[1]);
			}

			if (args[2].equals("world")) {
				for (World world : Bukkit.getWorlds()) {
					list.add(world.getName());
				}
			}
		}

		if (args.length > 3 && args.length < 7 && args[2].equals("position") && sender instanceof Player) {
			list.add("~");
		}

		for (int i = 0; i < list.size(); i++) {
			if (!(list.get(i).contains(args[args.length - 1]))) {
				list.remove(i);
				i -= 1;
			}
		}

		return list;
	}

}
