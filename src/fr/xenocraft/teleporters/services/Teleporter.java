package fr.xenocraft.teleporters.services;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import fr.xenocraft.Main;
import net.md_5.bungee.api.ChatColor;

public class Teleporter {

	public String id;
	public Location bottomLoc;
	public Location middleLoc;
	public boolean forceRotation;
	public Color color;
	public String displayName;
	public String destinationId;
	public double loop = Math.random() * Math.PI * 2;

	public boolean loadFromConfig(ConfigurationSection section) {

		id = section.getName();
		bottomLoc = section.getLocation("location");
		middleLoc = bottomLoc.clone().add(0, 1.5, 0);
		forceRotation = section.getBoolean("forceRotation");
		color = section.getColor("color");
		destinationId = section.getString("destination");
		displayName = section.getString("displayName");

		return true;
	}

	public void createNew(String id, Location loc) {

		this.id = id;
		bottomLoc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		bottomLoc.add(.5, 0, .5);
		middleLoc = bottomLoc.clone().add(0, 1.5, 0);
		forceRotation = false;
		color = Color.fromRGB(0, 0, 0);
		displayName = "Unnamed";
		destinationId = null;

	}

	public void saveToConfig(Main plugin) {

		ConfigurationSection tpSection = plugin.getCustomConfig().getConfigurationSection("teleporters");

		if (!tpSection.contains(id)) tpSection.createSection(id);

		ConfigurationSection section = tpSection.getConfigurationSection(id);

		section.set("location", bottomLoc);
		section.set("forceRotation", forceRotation);
		section.set("color", color);
		section.set("displayName", displayName);
		section.set("destination", destinationId);

		plugin.saveCustomConfig();
	}

	public World getWorld() {
		return bottomLoc.getWorld();
	}

	public ChatColor getChatColor() {
		return ChatColor.of(String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue()));
	}

	public void setCoords(int x, int y, int z) {
		bottomLoc.setX(x + .5);
		bottomLoc.setY(y);
		bottomLoc.setZ(z + .5);

		middleLoc = bottomLoc.clone().add(0, 1.5, 0);
	}

	public void setWorld(World world) {
		bottomLoc.setWorld(world);
		middleLoc.setWorld(world);
	}

}