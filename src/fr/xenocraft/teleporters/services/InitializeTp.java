package fr.xenocraft.teleporters.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import fr.xenocraft.Main;

public class InitializeTp {

	private Main plugin;
	public static Map<String, Teleporter> teleportsMap = new HashMap<String, Teleporter>();

	public InitializeTp(Main plugin) {
		this.plugin = plugin;
	}

	public void init() {

		ConfigurationSection tpSection = plugin.getCustomConfig().getConfigurationSection("teleporters");
		if (tpSection == null) return;

		Set<String> keys = tpSection.getKeys(false);

		Bukkit.getLogger().info("Loading teleporters");

		for (String key : keys) {

			Bukkit.getLogger().info("Initializing " + key);

			ConfigurationSection entry = tpSection.getConfigurationSection(key);

			Teleporter tp = new Teleporter();
			if (tp.loadFromConfig(entry)) {
				teleportsMap.put(key, tp);
			}

		}

		new AnimationRenderer().runTaskTimer(plugin, 120, 1);
	}

	public void updateMap() {

		teleportsMap = new HashMap<String, Teleporter>();

		ConfigurationSection tpSection = plugin.getCustomConfig().getConfigurationSection("teleporters");
		if (tpSection == null) return;

		Set<String> keys = tpSection.getKeys(false);

		Bukkit.getLogger().info("(Re)Loading teleporters");

		for (String key : keys) {

			Bukkit.getLogger().info("Initializing " + key);

			ConfigurationSection entry = tpSection.getConfigurationSection(key);

			Teleporter tp = new Teleporter();
			if (tp.loadFromConfig(entry)) {
				teleportsMap.put(key, tp);
			}

		}
	}
}
