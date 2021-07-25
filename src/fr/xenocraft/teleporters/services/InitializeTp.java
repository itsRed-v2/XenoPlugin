package fr.xenocraft.teleporters.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import fr.xenocraft.Main;

public class InitializeTp {

	private Main plugin;
	public static Map<String, Teleporter> teleportsMap = new HashMap<String, Teleporter>();

	public InitializeTp(Main plugin) {
		this.plugin = plugin;
	}

	public void Init() {

		ConfigurationSection tpSection = plugin.getCustomConfig().getConfigurationSection("teleporters");
		if (tpSection == null) return;

		Set<String> keys = tpSection.getKeys(false);
		
		System.out.println("Loading teleporters");

		for (String key : keys) {

			System.out.println("Initializing " + key);

			ConfigurationSection entry = tpSection.getConfigurationSection(key);
			
			Teleporter tp = new Teleporter();
			if (tp.readConfig(entry)) {
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

		System.out.println("Loading teleporters");

		for (String key : keys) {

			System.out.println("Initializing " + key);

			ConfigurationSection entry = tpSection.getConfigurationSection(key);

			Teleporter tp = new Teleporter();
			if (tp.readConfig(entry)) {
				teleportsMap.put(key, tp);
			}

		}
	}
}
