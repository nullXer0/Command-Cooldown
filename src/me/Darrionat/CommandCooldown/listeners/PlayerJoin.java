package me.Darrionat.CommandCooldown.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.Darrionat.CommandCooldown.CommandCooldown;
import me.Darrionat.CommandCooldown.utils.UpdateChecker;
import me.Darrionat.CommandCooldown.utils.Utils;

public class PlayerJoin implements Listener {

	private CommandCooldown plugin;

	public PlayerJoin(CommandCooldown plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (!plugin.getConfig().getBoolean("checkUpdates"))
			return;

		UpdateChecker updater = new UpdateChecker(plugin, 73696);
		try {
			if (!updater.checkForUpdates())
				return;
		} catch (Exception exe) {
			plugin.getLogger().info("Could not check for updates! Stacktrace:");
			exe.printStackTrace();
			return;

		}
		Player p = e.getPlayer();
		if (!p.isOp())
			return;

		String version = "v" + plugin.getDescription().getVersion();
		String latestVersion = UpdateChecker.getLatestVersion();

		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				p.sendMessage(Utils.chat("&eUpdate available! &bCommandCooldown is currently on " + version));
				p.sendMessage(Utils.chat("&bDownload version " + latestVersion + " here!"));
				p.sendMessage(
						Utils.chat("https://www.spigotmc.org/resources/command-cooldown-1-7-1-15-support.73696/"));
			}
		}, 30L);// 60 L == 3 sec, 20 ticks == 1 sec

	}
}
