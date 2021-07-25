package fr.xenocraft.customMaps.mapRenderers;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class Palette extends MapRenderer {

	private boolean hasRendered = false;
	
	public Palette() {

	}
	
	@Override
	public void render(MapView view, MapCanvas canvas, Player p) {
		
		if (this.hasRendered) return;
		
		for (byte color = -128; color < 128; color++ ) {
			int x = (color + 128)%16;
			int y = (int) Math.floor((color+128) / 16);
			square(canvas, x*8, y*8, color);
			if (color == 127) return;
		}
		
		this.hasRendered = true;
		
	}
	
	private void square(MapCanvas canvas, int x, int y, byte color) {
		
		for (int x2 = x; x2 < x+8; x2++) {
			for (int y2 = y; y2 < y+8; y2++) {
				canvas.setPixel(x2, y2, color);
			}
		}
		
	}

}
