package fr.xenocraft.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.xenocraft.Main;
import fr.xenocraft.teleporters.services.InitializeTp;
import fr.xenocraft.teleporters.services.Teleporter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class TeleporterCmd implements CommandExecutor {

	private Main plugin;

	public TeleporterCmd(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (args.length > 0) {

			if (args[0].equals("list")) {
				Set<String> ids = InitializeTp.teleportsMap.keySet();

				if (ids.size() == 0) {
					sender.sendMessage("There is currently no active teleporter.");
				} else {
					sender.sendMessage("There are currently §a" + ids.size() + "§r active teleporters:");
					sender.sendMessage("§e" + String.join("§7, §e", ids));
				}
			}

			if (args[0].equals("info")) {
				info(sender, args);
			}

			if (args[0].equals("create")) {
				createTp(sender, args);
			}

			if (args[0].equals("remove")) {
				removeTp(sender, args);
			}

			if (args[0].equals("modify")) {
				modifyTp(sender, args);
			}

		}
		
		return true;
	}

	private void info(CommandSender sender, String[] args) {

		if (args.length == 1) {

			// give info about nearest teleporter
			if (sender instanceof Player) {
				Player p = (Player) sender;

				double minDistance = 100;
				Teleporter nearestTp = null;

				for (Teleporter tp : InitializeTp.teleportsMap.values()) {
					try {
						double distance = p.getLocation().distanceSquared(tp.bottomLoc);

						if (distance < minDistance) {
							minDistance = distance;
							nearestTp = tp;
						}
					} catch (IllegalArgumentException e) {
						continue;
					}
				}
				if (nearestTp != null) {
					p.sendMessage("Information about the nearest teleporter:");
					displayInfo(sender, nearestTp);
				} else {
					p.sendMessage("§cCan't find a teleporter within a 10 block range");
					p.sendMessage("§cSearching info about a specific teleporter? Use:");
					p.sendMessage("§c/teleporter info <id>");
				}

			} else {
				sender.sendMessage("§cCorrect syntax: /teleporter info <id>");
			}

			return;
		}
		if (args.length > 2) {
			sender.sendMessage("§cThe id can only be one word");
			return;
		}

		Teleporter tp = InitializeTp.teleportsMap.get(args[1]);

		if (tp == null) {
			sender.sendMessage("§cCan't find a teleporter with the id \"" + args[1] + "\"");
			return;
		}

		sender.sendMessage("Information about §e" + tp.id + "§r:");
		displayInfo(sender, tp);
	}

	private void displayInfo(CommandSender sender, Teleporter tp) {
		TextComponent message = new TextComponent("§9Id: §e" + tp.id);
		message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§eYou can't edit the id!")));
		sender.spigot().sendMessage(message);

		message = new TextComponent("§9World: §r" + tp.getWorld().getName());
		message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to edit the world")));
		message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
				"/teleporter modify " + tp.id + " world " + tp.getWorld().getName()));
		sender.spigot().sendMessage(message);

		message = new TextComponent("§9Position: §r" + tp.bottomLoc.getBlockX() + " " + tp.bottomLoc.getBlockY() + " "
				+ tp.bottomLoc.getBlockZ());
		message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to edit the coordinates")));
		message.setClickEvent(
				new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/teleporter modify " + tp.id + " position "
						+ tp.bottomLoc.getBlockX() + " " + tp.bottomLoc.getBlockY() + " " + tp.bottomLoc.getBlockZ()));
		sender.spigot().sendMessage(message);

		message = new TextComponent(
				"§9Rotation: §rYaw: " + tp.bottomLoc.getYaw() + "  Pitch: " + tp.bottomLoc.getPitch());
		message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to edit the rotation")));
		message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
				"/teleporter modify " + tp.id + " rotation " + tp.bottomLoc.getYaw() + " " + tp.bottomLoc.getPitch()));
		sender.spigot().sendMessage(message);

		message = new TextComponent("§9ForceRotation: §r" + tp.forceRotation);
		message.setHoverEvent(
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to edit the forceRotation parameter")));
		message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
				"/teleporter modify " + tp.id + " forceRotation " + tp.forceRotation));
		sender.spigot().sendMessage(message);


		message = new TextComponent(TextComponent.fromLegacyText("§9Color: §c" + tp.color.getRed() + " §a"
				+ tp.color.getGreen() + " §9" + tp.color.getBlue() + "§7 ==> " + tp.getChatColor() + "||||||||"));
		message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to edit the color")));
		message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/teleporter modify " + tp.id
				+ " color " + tp.color.getRed() + " " + tp.color.getGreen() + " " + tp.color.getBlue()));
		sender.spigot().sendMessage(message);
		
		message = new TextComponent("§9Display name: §r" + tp.displayName);
		message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to edit the display name")));
		message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/teleporter modify " + tp.id
				+ " displayName " + tp.displayName.replace('§', '&')));
		sender.spigot().sendMessage(message);
		
		String destinationMsg;
		String suggestCommand;
		if (tp.destinationId == null) {
			destinationMsg = "§9Destination: §7none";
			suggestCommand = "/teleporter modify " + tp.id + " destination ";
		} else {
			destinationMsg = "§9Destination: §r" + tp.destinationId;
			suggestCommand = "/teleporter modify " + tp.id + " destination " + tp.destinationId;
		}
		message = new TextComponent(destinationMsg);
		message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to edit the destination")));
		message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestCommand));
		sender.spigot().sendMessage(message);
	}

	private void createTp(CommandSender sender, String[] args) {

		if (args.length == 1) {
			sender.sendMessage("§cCorrect syntax: /teleporter create <id>");
			return;
		}
		if (args.length > 2) {
			sender.sendMessage("§cThe id can only be one word");
			return;
		}

		if (InitializeTp.teleportsMap.containsKey(args[1])) {
			sender.sendMessage("§cThis id is unavailable, another teleporter is using it");
			return;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage(
					"§cThis command creates a new teleporter on the player's location. You need to execute this command as a player!");
			return;
		}

		Player p = (Player) sender;

		Teleporter tp = new Teleporter();
		tp.createNew(args[1], p.getLocation());
		tp.saveToConfig(plugin);
		new InitializeTp(plugin).updateMap();

		p.sendMessage("Created a new teleporter \"" + args[1] + "\" on your location");
	}

	private void removeTp(CommandSender sender, String[] args) {

		if (args.length == 1) {
			sender.sendMessage("§cCorrect syntax: /teleporter remove <id> <verification>");
			return;
		}
		
		if (args.length == 2) {
			if (!InitializeTp.teleportsMap.containsKey(args[1])) {
				sender.sendMessage("§cCan't find any teleporter with the id \"" + args[1] + "\"");
				return;
			}
			sender.sendMessage("§cPlease write two times the id as a verification:");
			sender.sendMessage("§c/teleporter remove <id> <verification>");
		}

		if (args.length == 3) {
			if (!args[1].equals(args[2])) {
				sender.sendMessage("§cError: The the two ids are different");
				return;
			}

			if (!InitializeTp.teleportsMap.containsKey(args[1])) {
				sender.sendMessage("§cCan't find any teleporter with the id \"" + args[1] + "\"");
				return;
			}

			plugin.getCustomConfig().getConfigurationSection("teleporters").set(args[1], null);
			plugin.saveCustomConfig();
			new InitializeTp(plugin).updateMap();

			sender.sendMessage("Deleted teleporter \"" + args[1] + "\"");
		}

	}

	private void modifyTp(CommandSender sender, String[] args) {

		if (args.length < 3) {
			sender.sendMessage("§cCorrect syntax: /teleporter modify <id> <parameter> <value>");
			return;
		}

		Teleporter tp = InitializeTp.teleportsMap.get(args[1]);

		if (tp == null) {
			sender.sendMessage("§cCan't find a teleporter with the id \"" + args[1] + "\"");
			return;
		}
		
		if (args[2].equals("forceRotation")) {
			if (args.length != 4 || (!args[3].equals("true") && !args[3].equals("false"))) {
				sender.sendMessage("§cThe value for this parameter must be either §otrue§r§c or §ofalse");
				return;
			}

			if (args[3].equals("true")) tp.forceRotation = true;
			if (args[3].equals("false")) tp.forceRotation = false;

			sender.sendMessage("The parameter forceRotation for §e" + tp.id + "§r is now: §9" + tp.forceRotation);
		}
		
		if (args[2].equals("displayName")) {
			if (args.length == 3) {
				sender.sendMessage("§cYou must provide a name");
				return;
			}

			String name = "";

			for (int i = 3; i < args.length; i++) {
				name += args[i];
				if (i < args.length - 1) name += " ";
			}

			name = ChatColor.translateAlternateColorCodes('&', name);
			tp.displayName = name;
			sender.sendMessage("The display name for §e" + tp.id + "§r is now: \"" + name + "§r\"");
		}

		if (args[2].equals("color")) {
			if (args.length != 6) {
				sender.sendMessage("§cCorrect syntax:");
				sender.sendMessage("§c/teleporter modify <id> color <r> <g> <b>");
				sender.sendMessage("§c(r, g and b must be integers between 0 and 255)");
				return;
			}

			for (int i = 0; i < 3; i++) {

				if (args[i + 3].matches("[0-9]+")) {
					int value = Integer.parseInt(args[i + 3]);

					if (value > 255) {
						sender.sendMessage("§cThe values r, g and b must be integers between 0 and 255");
						return;
					}
				} else {
					sender.sendMessage("§cThe values r, g and b must be integers between 0 and 255");
					return;
				}
			}

			tp.color = Color.fromRGB(Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]));
			sender.sendMessage("The color for §e" + tp.id + "§r is now: " + tp.getChatColor() + args[3] + " " + args[4]
					+ " " + args[5]);
		}

		if (args[2].equals("destination")) {

			if (args.length > 4) {
				sender.sendMessage("§cThe id cannot contain spaces");
				return;
			}

			if (args.length == 3) {

				if (tp.destinationId == null) {
					sender.sendMessage("Nothing changed. §e" + tp.id + "§r already had no destination");
					return;
				}
				tp.destinationId = null;
				sender.sendMessage("§e" + tp.id + "§r has no longer a destination");
			}

			if (args.length == 4) {

				if (!InitializeTp.teleportsMap.containsKey(args[3])) {
					sender.sendMessage("§cCan't find a teleporter with the id \"" + args[3] + "\"");
					return;
				}

				if (args[3].equals(args[1])) {
					sender.sendMessage("§cA teleporter can't be its own destination");
					return;
				}

				tp.destinationId = args[3];
				sender.sendMessage("The destination for §e" + tp.id + "§r is now: §e" + args[3]);
			}

		}

		if (args[2].equals("position")) {
			if (args.length != 6) {
				sender.sendMessage("§cCorrect syntax:");
				sender.sendMessage("§c/teleporter modify <id> position <x> <y> <z>");
				return;
			}

			List<Integer> coords = new ArrayList<>();

			for (int i = 0; i < 3; i++) {

				String s = args[i + 3];
				int value = 0;

				if (s.equals("~")) s += "0";
				
				if (s.matches("~?-?\\d+")) {

					if (s.startsWith("~")) {
						value = Integer.parseInt(s.substring(1));

						if (sender instanceof Player) {

							Location loc = ((Player) sender).getLocation();
							if (i == 0) value += loc.getBlockX();
							if (i == 1) value += loc.getBlockY();
							if (i == 2) value += loc.getBlockZ();

						} else {
							sender.sendMessage("§cOnly players may use relative coordinates");
							return;
						}
					} else {
						value = Integer.parseInt(s);
					}
					coords.add(value);

				} else {
					sender.sendMessage("§cThe values x, y and z must be coordinates");
					return;
				}
			}

			tp.setCoords(coords.get(0), coords.get(1), coords.get(2));
			sender.sendMessage("§e" + tp.id + "§r has been moved to §9"
					+ coords.get(0) + " " + coords.get(1) + " " + coords.get(2));

		}

		if (args[2].equals("rotation")) {
			if (args.length != 5) {
				sender.sendMessage("§cCorrect syntax:");
				sender.sendMessage("§c/teleporter modify <id> rotation <yaw> <pitch>");
				return;
			}

			if (!args[3].matches("-?\\d+(\\.\\d+)?") || !args[4].matches("-?\\d+(\\.\\d+)?")) {
				sender.sendMessage("§cPitch and yaw must be numbers");
				return;
			}

			float yaw = Float.parseFloat(args[3]);
			float pitch = Float.parseFloat(args[4]);

			if (pitch < -90 || pitch > 90) {
				sender.sendMessage("§cPitch must be between -90 and 90");
				return;
			}

			yaw %= 360;

			tp.bottomLoc.setYaw(yaw);
			tp.bottomLoc.setPitch(pitch);
			sender.sendMessage("The rotation for §e" + tp.id + "§r is now:\n§9Yaw: §r" + yaw + "\n§9Pitch: §r" + pitch);
		}

		if (args[2].equals("world")) {
			if (args.length == 3) {
				sender.sendMessage("§cYou must provide a world name");
				return;
			}

			if (args.length > 4) {
				sender.sendMessage("§cA world name cannot contain spaces");
				return;
			}

			World world = Bukkit.getWorld(args[3]);

			if (world == null) {
				sender.sendMessage("§cCan't find a world with this name");
				return;
			}

			tp.setWorld(world);
			sender.sendMessage("§e" + tp.id + "§r is now in the world §9" + args[3]);
		}

		tp.saveToConfig(plugin);

	}

}
