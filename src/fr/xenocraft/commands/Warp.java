package fr.xenocraft.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import fr.xenocraft.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class Warp implements CommandExecutor {
	
	private Main plugin;
	
	public Warp(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players may execute this command!");
			return true;
		}

		Player p = (Player) sender;
		
		ConfigurationSection configWarp = (ConfigurationSection) plugin.getConfig().get("warp");
		
		if (args.length > 0) {
			
			String locationName = args[0].toLowerCase();
			
			if (configWarp != null && configWarp.contains(locationName)) {
				
				ConfigurationSection entry = configWarp.getConfigurationSection(locationName);
				List<Double> coords = entry.getDoubleList("position");
				World world = Bukkit.getWorld(entry.getString("world"));

				if (world == null) {
					p.sendMessage("§cThe world you're trying to teleport in is not loaded.");
					return true;
				}
				
				if (entry.contains("rotation")) {
					List<Float> rotation = entry.getFloatList("rotation");
					p.teleport(new Location(world, coords.get(0), coords.get(1), coords.get(2), rotation.get(0),
							rotation.get(1)));
				} else {
					Float yaw = p.getLocation().getYaw();
					Float pitch = p.getLocation().getPitch();
					
					p.teleport(new Location(world, coords.get(0), coords.get(1),
							coords.get(2), yaw, pitch));
				}
				p.playSound(p.getLocation(), "minecraft:item.chorus_fruit.teleport", 1, 1);

				// Broadcasting clickable teleport message to everyone
				TextComponent message = new TextComponent(p.getDisplayName() + "§7: warping to §e" + locationName);
				message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§eClick to teleport!")));
				message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/warp " + locationName));
				Bukkit.spigot().broadcast(message);

				return true;
			} else {
				p.sendMessage("§cThe warp §o" + args[0] + "§r§c do not exist!");
				return true;
			}
		}
		
		p.sendMessage("Correct usage:");
		return false;
	}

}
