package me.Darrionat.CommandCooldown;

import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;

import me.Darrionat.CommandCooldown.files.FileManager;

public class Cooldown {

	private UUID uuid;
	private String command;
	private long endOfCooldown;

	public Cooldown(UUID uuid, String command, long endOfCooldown) {
		this.uuid = uuid;
		this.command = command;
		this.endOfCooldown = endOfCooldown;
	}

	public UUID getPlayerUUID() {
		return uuid;
	}

	public String getCommand() {
		return command;
	}

	public long getEndOfCooldown() {
		return endOfCooldown;
	}

	public long getTimeRemainingMillis() {
		return endOfCooldown - System.currentTimeMillis();
	}

	public String getYAMLKeyString() {
		return uuid.toString() + "/" + command.replace(" ", "_");
	}

	public static UUID getUUIDFromKey(String key) {
		return UUID.fromString(key.split("/")[0]);
	}

	public static String getCommandFromKey(String key) {
		return key.split("/")[1].replace("_", " ");
	}

	public static Cooldown getSavedCooldown(UUID uuid, String command, CommandCooldown plugin) {
		FileManager fileManager = new FileManager(plugin);
		FileConfiguration cooldownsConfig = fileManager.getDataConfig("cooldownData");

		String key = uuid.toString() + "/" + command.replace(" ", "_");
		long endOfCooldown = cooldownsConfig.getLong(key);

		return new Cooldown(uuid, command, endOfCooldown);
	}
}