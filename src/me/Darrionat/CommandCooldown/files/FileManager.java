package me.Darrionat.CommandCooldown.files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.Darrionat.CommandCooldown.CommandCooldown;
import me.Darrionat.CommandCooldown.utils.Utils;

public class FileManager {

	private CommandCooldown plugin;

	public FileManager(CommandCooldown plugin) {
		this.plugin = plugin;
	}

	// Files and File configurations here
	public FileConfiguration dataConfig;
	public File dataFile;
	// -------------------------------------

	public void setup(String fileName) {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		dataFile = new File(plugin.getDataFolder(), fileName + ".yml");

		if (!dataFile.exists()) {
			try {
				dataFile.createNewFile();
				dataConfig = YamlConfiguration.loadConfiguration(dataFile);
				String successMessage = "&e[" + plugin.getName() + "] &aCreated the " + fileName + ".yml file";
				Bukkit.getServer().getConsoleSender().sendMessage(Utils.chat(successMessage));
			} catch (IOException exe) {
				String failMessage = "&e[" + plugin.getName() + "] &cFailed to create the " + fileName + ".yml file";
				Bukkit.getServer().getConsoleSender().sendMessage(Utils.chat(failMessage));
				exe.printStackTrace();
			}
		}

	}

	public boolean fileExists(String fileName) {
		dataFile = new File(plugin.getDataFolder(), fileName + ".yml");
		if (dataFile.exists()) {
			return true;
		}
		return false;
	}

	public void deleteFile(String fileName) {
		dataFile = new File(plugin.getDataFolder(), fileName + ".yml");
		dataFile.delete();
		return;
	}

	public FileConfiguration getDataConfig(String fileName) {
		dataFile = new File(plugin.getDataFolder(), fileName + ".yml");
		dataConfig = YamlConfiguration.loadConfiguration(dataFile);
		return dataConfig;
	}

	public File getDataFile(String fileName) {
		dataFile = new File(plugin.getDataFolder(), fileName + ".yml");
		return dataFile;
	}

	public void reloadCustomConfig(String fileName) {
		dataConfig = getDataConfig(fileName);
		// Look for defaults in the jar
		Reader defConfigStream;
		try {
			defConfigStream = new InputStreamReader(plugin.getResource(fileName + ".yml"), "UTF8");
		} catch (Exception exe) {
			exe.printStackTrace();
			return;
		}
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			dataConfig.setDefaults(defConfig);
		}
	}

	public void saveConfigFile(FileConfiguration dataConfig, String fileName) {
		dataFile = new File(plugin.getDataFolder(), fileName + ".yml");
		try {
			dataConfig.save(dataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void matchConfig(String fileName) {
		InputStream is = plugin.getResource(fileName + ".yml");
		FileConfiguration config = getDataConfig(fileName);
		if (is == null) {
			return;
		}
		YamlConfiguration jarConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(is));
		for (String key : jarConfig.getKeys(true))
			if (!config.contains(key)) {
				config.createSection(key);
				config.set(key, jarConfig.get(key));
			}
		for (String key : config.getConfigurationSection("").getKeys(true))
			if (!jarConfig.contains(key))
				config.set(key, null);
		config.set("version", plugin.getDescription().getVersion());
		saveConfigFile(config, fileName);

	}
}
