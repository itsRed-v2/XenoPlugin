package fr.xenocraft.chatMods.listeners;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import fr.xenocraft.Main;

public class Join implements Listener {
	
	private Main plugin;
	
	public Join(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		
		ScoreboardManager manager = Bukkit.getScoreboardManager();
	    Scoreboard board = manager.getMainScoreboard();
	    Set<Team> teams = board.getTeams();
	    for (Team team : teams) {
			if (team.hasEntry(p.getName())) {
				p.setDisplayName(team.getColor() + p.getName());
			}
		}
		
		event.setJoinMessage(plugin.getConfig().getString("join_msg").replace("%player%", p.getDisplayName()));
	}	
}