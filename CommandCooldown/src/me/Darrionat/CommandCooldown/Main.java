package me.Darrionat.CommandCooldown;

import org.bukkit.plugin.java.JavaPlugin;

import me.Darrionat.CommandCooldown.Commands.CommandControl;
import me.Darrionat.CommandCooldown.Listeners.CommandProcess;
import me.Darrionat.CommandCooldown.Utils.UpdateChecker;
import me.Darrionat.CommandCooldown.Utils.Utils;
import me.Darrionat.CommandCooldown.bStats.Metrics;

public class Main extends JavaPlugin {

	public void onEnable() {
		new CommandProcess(this);
		new CommandControl(this);
		saveDefaultConfig();

		@SuppressWarnings("unused")
		Metrics metrics = new Metrics(this);
		if (getConfig().getBoolean("checkUpdates") == true) {
			int id = 73696;
			String version = "v" + this.getDescription().getVersion();
			String name = this.getDescription().getName();
			UpdateChecker updater = new UpdateChecker(this, id);
			try {
				if (updater.checkForUpdates()) {

					getServer().getConsoleSender()
							.sendMessage(Utils.chat("&7" + name + ": &bYou are currently running version " + version));
					getServer().getConsoleSender().sendMessage(Utils.chat("&bAn update for &7" + name + " &f("
							+ UpdateChecker.getLatestVersion() + ") &bis available at:"));
					getServer().getConsoleSender()
							.sendMessage(Utils.chat("https://www.spigotmc.org/resources/command-cooldown.73696/"));
				} else {
					getServer().getConsoleSender().sendMessage("[" + name + "] Plugin is up to date! - " + version);
				}
			} catch (Exception e) {
				getLogger().info("Could not check for updates! Stacktrace:");
				e.printStackTrace();
			}
		}
	}

	public void onDisable() {

	}

}
