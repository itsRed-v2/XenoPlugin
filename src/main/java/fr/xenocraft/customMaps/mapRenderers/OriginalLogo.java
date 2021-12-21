package fr.xenocraft.customMaps.mapRenderers;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import fr.xenocraft.Main;
import fr.xenocraft.Utils;

public class OriginalLogo extends MapRenderer {
	
	private Main plugin;
	private boolean hasRendered = false;
	
	public OriginalLogo(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void render(MapView view, MapCanvas canvas, Player p) {
		
		if (hasRendered == true) return;
		
		Utils.asyncRenderImage(plugin, canvas, p, "http://xeocraft.fr/Logo_128.png");
		
		this.hasRendered = true;
		
	}

}
