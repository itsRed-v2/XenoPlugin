package fr.xenocraft;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import fr.xenocraft.chatMods.listeners.ChatFormater;
import fr.xenocraft.chatMods.listeners.Join;
import fr.xenocraft.chatMods.listeners.Quit;
import fr.xenocraft.commands.Citem;
import fr.xenocraft.commands.Map;
import fr.xenocraft.commands.Msg;
import fr.xenocraft.commands.OpenMaterializer;
import fr.xenocraft.commands.TeleporterCmd;
import fr.xenocraft.commands.Test;
import fr.xenocraft.commands.Warp;
import fr.xenocraft.commands.tabCompleters.TeleporterCmdTab;
import fr.xenocraft.commands.tabCompleters.WarpTab;
import fr.xenocraft.customMaps.listeners.MapClick;
import fr.xenocraft.deathRay.items.DeathRay;
import fr.xenocraft.deathRay.items.Modules;
import fr.xenocraft.deathRay.listeners.ClickRayMenu;
import fr.xenocraft.deathRay.listeners.DeathRayInHand;
import fr.xenocraft.deathRay.listeners.DeathRaySwap;
import fr.xenocraft.deathRay.listeners.UseDeathRay;
import fr.xenocraft.permissions.Permissions;
import fr.xenocraft.permissions.listeners.PermJoin;
import fr.xenocraft.permissions.listeners.PermQuit;
import fr.xenocraft.teleporters.listeners.PlayerMove;
import fr.xenocraft.teleporters.listeners.TpJoin;
import fr.xenocraft.teleporters.services.InitializeTp;
import fr.xenocraft.virtualStorages.guis.MaterializeUI;
import fr.xenocraft.virtualStorages.guis.VsmUI;

public class Main extends JavaPlugin {
	
	private File customConfigFile;
	private FileConfiguration customConfig;

	@Override
    public void onEnable() {

		saveDefaultConfig();
		createCustomConfig();
		
		// Items
		new DeathRay(this).init();
		new Modules(this).init();

		// Listeners
		Bukkit.getPluginManager().registerEvents(new Join(this), this);
		Bukkit.getPluginManager().registerEvents(new Quit(this), this);
		Bukkit.getPluginManager().registerEvents(new MapClick(this), this);
		Bukkit.getPluginManager().registerEvents(new UseDeathRay(this), this);
		Bukkit.getPluginManager().registerEvents(new DeathRaySwap(this), this);
		Bukkit.getPluginManager().registerEvents(new DeathRayInHand(this), this);
		Bukkit.getPluginManager().registerEvents(new Modules(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerMove(this), this);
		Bukkit.getPluginManager().registerEvents(new TpJoin(this), this);
		Bukkit.getPluginManager().registerEvents(new PermJoin(this), this);
		Bukkit.getPluginManager().registerEvents(new PermQuit(this), this);
		// Gui Listeners
		Bukkit.getPluginManager().registerEvents(new ClickRayMenu(this), this);
		Bukkit.getPluginManager().registerEvents(new VsmUI(this), this);
		Bukkit.getPluginManager().registerEvents(new MaterializeUI(this), this);

		// Formater
		new ChatFormater(this);
		
		// Commands & tab completers
		getCommand("test").setExecutor(new Test(this));
		getCommand("openmaterializer").setExecutor(new OpenMaterializer(this));
		getCommand("warp").setExecutor(new Warp(this));
		getCommand("warp").setTabCompleter(new WarpTab(this));
		getCommand("map").setExecutor(new Map(this));
		getCommand("citem").setExecutor(new Citem(this));
		getCommand("msg").setExecutor(new Msg(this));
		getCommand("teleporter").setExecutor(new TeleporterCmd(this));
		getCommand("teleporter").setTabCompleter(new TeleporterCmdTab(this));
		
		// Loading custom worlds
		loadWorlds();
		
		// Activating teleporters
		new InitializeTp(this).Init();

		// Setting PermissionAttachments for connectedPlayers
		new Permissions(this).setAllAttachments();

		// Initializing custom config
		ConfigurationSection customConfig = this.getCustomConfig();
		if (!customConfig.contains("teleporters")) {
			customConfig.createSection("teleporters");
			saveCustomConfig();
		}
    }
    
	@Override
    public void onDisable() {
		// Removing permissionAttachments
		Permissions.removeAllAttachments();
    }
	
	private void loadWorlds() {
		ConfigurationSection config = this.getConfig();

		if (config.contains("worlds")) {
			List<String> worldlist = config.getStringList("worlds");

			for (String worldname : worldlist) {
				System.out.println("Loading world " + worldname);
				new WorldCreator(worldname).createWorld();
			}
		}

	}

	public FileConfiguration getCustomConfig() {
		return this.customConfig;
	}
	
	public void saveCustomConfig() {
		try {
			this.customConfig.save(this.customConfigFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	 private void createCustomConfig() {
		 
	 	customConfigFile = new File(getDataFolder(), "custom.yml");
	    if (!customConfigFile.exists()) {
	        customConfigFile.getParentFile().mkdirs();
	        saveResource("custom.yml", false);
	    }
	
	    customConfig = new YamlConfiguration();
	    try {
	        customConfig.load(customConfigFile);
	    } catch (IOException | InvalidConfigurationException e) {
	        e.printStackTrace();
	    }
	}
}