package me.Darrionat.CommandCooldown;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.Darrionat.CommandCooldown.bStats.Metrics;
import me.Darrionat.CommandCooldown.commands.BaseCommand;
import me.Darrionat.CommandCooldown.commands.Cooldowns;
import me.Darrionat.CommandCooldown.files.FileManager;
import me.Darrionat.CommandCooldown.listeners.CommandProcess;
import me.Darrionat.CommandCooldown.listeners.PlayerJoin;
import me.Darrionat.CommandCooldown.utils.UpdateChecker;
import me.Darrionat.CommandCooldown.utils.Utils;

public class CommandCooldown extends JavaPlugin {

	public FileManager fileManager;
	public HashMap<String, Long> cooldownMap = new HashMap<String, Long>();

	public void onEnable() {
		fileManager = new FileManager(this);
		updateConfigs();

		loadCooldowns();

		new CommandProcess(this);
		new BaseCommand(this);
		new Cooldowns(this);
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
					new PlayerJoin(this);
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

	public void updateConfigs() {
		List<String> files = new ArrayList<String>();
		files.add("config");
		files.add("messages");
		for (String fileName : files) {
			if (!fileManager.fileExists(fileName)) {
				systemLog("Saving " + fileName + ".yml");
				saveResource(fileName + ".yml", false);
				continue;
			}
			systemLog("Updating " + fileName + ".yml");
			fileManager.matchConfig(fileName);
		}
		if (fileManager.fileExists("cooldowns"))
			saveResource("cooldowns.yml", false);
	}

	private String cooldownData = "cooldownData";

	public void onDisable() {
		saveCooldowns();
	}

	private void loadCooldowns() {
		FileConfiguration cooldownDataConfig = getCooldownDataConfig();
		for (String key : cooldownDataConfig.getKeys(false)) {
			cooldownMap.put(key, getConfig().getLong(key));
		}
	}

	private void saveCooldowns() {
		FileConfiguration cooldownDataConfig = getCooldownDataConfig();

		for (Entry<String, Long> entry : cooldownMap.entrySet()) {
			cooldownDataConfig.set(entry.getKey(), entry.getValue());
		}
		fileManager.saveConfigFile(cooldownDataConfig);

	}

	private FileConfiguration getCooldownDataConfig() {
		if (!fileManager.fileExists(cooldownData)) {
			fileManager.setup(cooldownData);
		}
		return fileManager.getDataConfig(cooldownData);
	}

	private void systemLog(String s) {
		s = "&a&l[COMMAND COOLDOWN] &7" + s;
		s = Utils.chat(s);
		System.out.println(s);
	}

}
