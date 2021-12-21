package fr.xenocraft.customMaps.mapRenderers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

public class Infos extends MapRenderer {
	
	private String pseudo;
	private int height;
	private boolean firstRender = true;
	private int cycle = 0;

	public Infos(String pseudo) {
		this.pseudo = pseudo;
	}
	
	@Override
	public void render(MapView view, MapCanvas canvas, Player p) {
		
		if (firstRender == true) {
			for (int i = 0; i <= 128; i++){
				canvas.setPixel(i, 0, (byte) -73);
				canvas.setPixel(i, 127, (byte) -73);
				canvas.setPixel(0, i, (byte) -73);
				canvas.setPixel(127, i, (byte) -73);
			}
			firstRender = false;
		}
		
		for (int x = 1; x < 127; x++) {
			for (int y = 1; y < 127; y++) {
				canvas.setPixel(x, y, (byte) 34);
			}
		}
		
		height = 4;
		
		p = Bukkit.getPlayer(pseudo);
		
		printLine(canvas, "Player:");
		
		if (p == null) {
			printLine(canvas, pseudo + "§117; - §17;Offline");

			cycle++;

			square(canvas, 59, 64, (byte) 13);
			square(canvas, 63, 64, (byte) 13);
			square(canvas, 67, 64, (byte) 13);

			int state = Math.floorDiv(cycle, 5);
			if (state == 3) {
				state = 0;
				cycle = 0;
			}
			if (state == 0) square(canvas, 59, 64, (byte) 117);
			if (state == 1) square(canvas, 63, 64, (byte) 117);
			if (state == 2) square(canvas, 67, 64, (byte) 117);


		} else {
			printLine(canvas, pseudo + "§117; - §-122;Online");
			printLine(canvas, "");
			printLine(canvas, "Holding:");
			printLine(canvas, p.getInventory().getItemInMainHand().getType().toString());
			printLine(canvas, "");
			printLine(canvas, "Position:");
			int yaw = Math.round(p.getLocation().getYaw());
			if (yaw < 0) yaw += 360;
			canvas.drawText(80, height, MinecraftFont.Font, "Yaw " + yaw);
			printLine(canvas, "x " + p.getLocation().getBlockX());
			canvas.drawText(80, height, MinecraftFont.Font, "Pitch " + Math.round(p.getLocation().getPitch()));
			printLine(canvas, "y " + p.getLocation().getBlockY());
			printLine(canvas, "z " + p.getLocation().getBlockZ());
			printLine(canvas, "");
			
	 		if (p.getAbsorptionAmount() > 0) printLine(canvas, "HP §17;" + Math.round(p.getHealth()) + "§-75; + §74;" + Math.round(p.getAbsorptionAmount()));
	 		else printLine(canvas, "HP §17;" + Math.round(p.getHealth()));
		}
	}
	
	private void printLine(MapCanvas canvas, String text) {
		canvas.drawText(5, height, MinecraftFont.Font, text);
		height += 9;
	}

	private void square(MapCanvas canvas, int x, int y, byte color) {

		for (int x2 = x; x2 < x + 2; x2++) {
			for (int y2 = y; y2 < y + 2; y2++) {
				canvas.setPixel(x2, y2, color);
			}
		}

	}

}
