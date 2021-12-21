package fr.xenocraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.xenocraft.Main;
import fr.xenocraft.deathRay.items.DeathRay;
import fr.xenocraft.deathRay.items.Modules;

public class Citem implements CommandExecutor {
	
	@SuppressWarnings("unused")
	private Main plugin;

	public Citem(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) return true;

		Player p = (Player) sender;

		if (args.length < 1) {
			return false;
		}

		if (args[0].equals("deathray")) {
			p.getInventory().addItem(DeathRay.deathRay);
		}
		if (args[0].equals("firemodule")) {
			p.getInventory().addItem(Modules.fireModule);
		}
		if (args[0].equals("powermodule")) {
			p.getInventory().addItem(Modules.powerModule);
		}
		if (args[0].equals("efficiencymodule")) {
			p.getInventory().addItem(Modules.efficiencyModule);
		}

		return true;
	}
}
