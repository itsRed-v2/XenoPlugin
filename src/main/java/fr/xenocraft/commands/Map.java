package fr.xenocraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import fr.xenocraft.Main;
import fr.xenocraft.customMaps.mapRenderers.Infos;
import fr.xenocraft.customMaps.mapRenderers.Logo;
import fr.xenocraft.customMaps.mapRenderers.OriginalLogo;
import fr.xenocraft.customMaps.mapRenderers.Palette;

public class Map implements CommandExecutor {

	private Main plugin;
	
	public Map(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) return true;
		
		Player p = (Player) sender;
		
		ItemStack map = new ItemStack(Material.FILLED_MAP);
		MapMeta mapMeta = (MapMeta) map.getItemMeta();
		MapView view = Bukkit.createMap(Bukkit.getWorld("world"));
		PersistentDataContainer data = mapMeta.getPersistentDataContainer();
		NamespacedKey typeKey = new NamespacedKey(plugin, "MapType");
		
		view.setScale(Scale.CLOSEST);
		
		for (MapRenderer renderer : view.getRenderers()) {
			view.removeRenderer(renderer);
		}
		
		
		if (args[0].equals("palette")) {
			view.addRenderer(new Palette());
			data.set(typeKey, PersistentDataType.STRING, "palette");
		}
		
		if (args[0].equals("info")) {
			
			if (args.length > 1) {
				view.addRenderer(new Infos(args[1]));
				data.set(typeKey, PersistentDataType.STRING, "info");
				data.set(new NamespacedKey(plugin, "player"), PersistentDataType.STRING, args[1]);
			} else {
				p.sendMessage("You should use:");
				p.sendMessage("/map info <player>");
				return true;
			}
		}
		
		if (args[0].equals("logo_original")) {
			view.addRenderer(new OriginalLogo(plugin));
			data.set(typeKey, PersistentDataType.STRING, "logo_original");
		}
		
		if (args[0].equals("logo")) {
			view.addRenderer(new Logo(plugin));
			data.set(typeKey, PersistentDataType.STRING, "logo");
		}
		
		
		if (view.getRenderers().size() > 0) {
			mapMeta.setMapView(view);
			map.setItemMeta(mapMeta);
			p.getInventory().addItem(map);
		}
		else p.sendMessage("§cThis map name doesn't exist");
		return true;
	}
	
}
