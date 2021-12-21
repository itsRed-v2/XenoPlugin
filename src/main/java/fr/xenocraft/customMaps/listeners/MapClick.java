package fr.xenocraft.customMaps.listeners;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import fr.xenocraft.Main;
import fr.xenocraft.customMaps.mapRenderers.Infos;
import fr.xenocraft.customMaps.mapRenderers.Logo;
import fr.xenocraft.customMaps.mapRenderers.OriginalLogo;
import fr.xenocraft.customMaps.mapRenderers.Palette;

public class MapClick implements Listener {
	
	private Main plugin;
	
	public MapClick(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerUse(PlayerInteractEvent e) {

		if (e.getMaterial() == Material.FILLED_MAP) {
			
			ItemStack item = e.getItem();
			
			tryRecoverMap(item, e.getPlayer());
		}
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractAtEntityEvent e) {
		
		if (!(e.getRightClicked().getType() == EntityType.ITEM_FRAME)) return;
		
		ItemStack item = ((ItemFrame) e.getRightClicked()).getItem();
		
		if (item.getType() == Material.FILLED_MAP) {
			
			if (tryRecoverMap(item, e.getPlayer())) {
				ItemFrame frame = (ItemFrame) e.getRightClicked();
				frame.setRotation(frame.getRotation().rotateCounterClockwise());
				frame.setItem(item);
			}
		}
	}

	private boolean tryRecoverMap(ItemStack map_item, Player p) {
		MapMeta mapMeta = (MapMeta) map_item.getItemMeta();
		PersistentDataContainer data = mapMeta.getPersistentDataContainer();
		
		if (!data.has(new NamespacedKey(plugin, "MapType"), PersistentDataType.STRING)) return false;
		
		String type = data.get(new NamespacedKey(plugin, "MapType"), PersistentDataType.STRING);
		
		MapView view = mapMeta.getMapView();
		

		if (view.getRenderers().size() > 0
				&& view.getRenderers().get(0).getClass().getPackage().getName().startsWith("fr.xenocraft"))
			return false;
		
		for (MapRenderer renderer : view.getRenderers()) {
			view.removeRenderer(renderer);
		}
		
		if (searchRenderer(p, data, type, view)) {
			mapMeta.setMapView(view);
			map_item.setItemMeta(mapMeta);
			return true;
		} else
			return false;
	}
	
	private boolean searchRenderer(Player p, PersistentDataContainer data, String type, MapView view) {
		
		if (type.equals("palette")) {
			view.addRenderer(new Palette());
			return true;
		}
		
		if (type.equals("info")) {
			if (data.has(new NamespacedKey(plugin, "player"), PersistentDataType.STRING)) {
				view.addRenderer(new Infos(data.get(new NamespacedKey(plugin, "player"), PersistentDataType.STRING)));
				return true;
			} else {
				p.sendMessage("§cUnable to recover the info type map: Target player is not defined");
				return false;
			}
		}
		
		if (type.equals("logo_original")) {
			view.addRenderer(new OriginalLogo(plugin));
			return true;
		}
		
		if (type.equals("logo")) {
			view.addRenderer(new Logo(plugin));
			return true;
		}

		p.sendMessage("§cUnable to recover the map: this map type doesn't exist");
		return false;
	}

}
