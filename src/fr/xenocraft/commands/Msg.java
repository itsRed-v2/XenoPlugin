package fr.xenocraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.xenocraft.Main;

public class Msg implements CommandExecutor {

	@SuppressWarnings("unused")
	private Main plugin;

	public Msg(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) return true;

		if (args.length < 2) return false;

		Player p1 = (Player) sender;
		Player p2 = Bukkit.getPlayer(args[0]);

		if (p2 == null) {
			p1.sendMessage("§cUnable to find a player with the username §o" + args[0] + "");
			return true;
		}

		String message = "";

		for (int i = 1; i < args.length; i++) {
			message += args[i] + " ";
		}

		String coloredMessage = ChatColor.translateAlternateColorCodes('&', message);

		p1.sendMessage("§6[§rYOU §7---> §r" + p2.getDisplayName() + "§6]§r " + coloredMessage);
		p2.sendMessage("§6[§r" + p2.getDisplayName() + "§7 ---> §rYOU§6]§r " + coloredMessage);

		return true;
	}

}
