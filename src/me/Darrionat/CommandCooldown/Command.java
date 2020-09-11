package me.Darrionat.CommandCooldown;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import me.Darrionat.CommandCooldown.exceptions.NoBaseCooldownException;
import me.Darrionat.CommandCooldown.files.FileManager;
import me.Darrionat.CommandCooldown.utils.Utils;

public class Command {

	public String message;
	public String label;
	public String[] args;
	public List<String> aliases = new ArrayList<>();
	public double cooldown;
	private ConfigurationSection commandSection = null;

	public boolean hasCooldown;

	private FileManager fileManager;
	private FileConfiguration cooldownsConfig;

	public Command(String command, CommandCooldown plugin) {
		fileManager = new FileManager(plugin);
		cooldownsConfig = fileManager.getDataConfig("cooldowns");

		this.message = command.replaceFirst("/", "");
		this.label = getLabel();
		this.args = getArgs();
		this.aliases = getAliases();
		this.hasCooldown = hasCooldown();
		this.cooldown = getCooldown();
	}

	private String getLabel() {
		String label;
		int i = message.indexOf(' ');
		try {
			label = message.substring(0, i);
		} catch (StringIndexOutOfBoundsException exe) {
			label = message;
		}
		return label;
	}

	private String[] getArgs() {
		String argsString = message.replace(label, "");
		try {
			return argsString.split(" ");
		} catch (PatternSyntaxException exe) {
			return null;
		}
	}

	private List<String> getAliases() {
		String commandSectionKey = "";
		for (String key : cooldownsConfig.getKeys(false)) {
			ConfigurationSection section = cooldownsConfig.getConfigurationSection(key);
			if (section.getConfigurationSection("aliases") == null) {
				continue;
			}
			List<String> aliases = section.getStringList("aliases");
			if (key.equalsIgnoreCase(label)) {
				commandSectionKey = key;
				break;
			}
			for (String alias : aliases) {
				if (alias.equalsIgnoreCase(label)) {
					commandSectionKey = key;
					break;
				}
			}
		}
		if (commandSectionKey.equals("")) {
			commandSection = cooldownsConfig.getConfigurationSection(label);
			return null;
		}
		commandSection = cooldownsConfig.getConfigurationSection(commandSectionKey);
		List<String> aliases = new ArrayList<>();
		aliases.add(commandSectionKey);
		aliases.addAll(commandSection.getStringList("aliases"));

		return aliases;
	}

	private boolean hasCooldown() {
		if (commandSection == null) {
			return false;
		}
		if (commandSection.getConfigurationSection("cooldowns") == null) {
			return false;
		}
		return true;
	}

	// Returns cooldown in seconds
	private double getCooldown() {
		if (!hasCooldown)
			return 0;
		String noBaseCooldownString = NoBaseCooldownException.ERROR_STRING.replace("sectionName",
				commandSection.getName());
		double baseCooldown = commandSection.getDouble("cooldowns.*");
		if (baseCooldown == 0) {
			new NoBaseCooldownException(noBaseCooldownString).printStackTrace();
			System.out.println(Utils.chat("&4Error: &c" + noBaseCooldownString));
			return 0;
		}

		// Map of the arguments and their cooldown
		HashMap<String[], Double> argumentCooldownMap = new HashMap<>();
		ConfigurationSection cooldownsSection = commandSection.getConfigurationSection("cooldowns");

		for (String cooldownKey : cooldownsSection.getKeys(false)) {
			if (cooldownKey.equalsIgnoreCase("*"))
				continue;

			argumentCooldownMap.put(cooldownKey.split(" "), cooldownsSection.getDouble(cooldownKey));
		}
		List<String[]> passedArgSet = getPassedArguments(argumentCooldownMap);

		// double baseCooldown = cooldownsSection.getDouble("*");

		if (passedArgSet.size() == 0)
			return baseCooldown;

		if (passedArgSet.size() > 1) {
			System.out.println(Utils.chat("&a&lCOMMAND COOLDOWN&7: &4An Error Has Occured."));
			System.out.println(Utils.chat(
					"&cThe provided command has triggered an issue that gives multiple outputs from available cooldowned arguments. You should only see this if your cooldowns.yml is not configured properly. Resorting to base cooldown (*)."));
			return baseCooldown;
		}

		// Gets the cooldown from the last remaining set
		double cooldown = argumentCooldownMap.get(passedArgSet.get(0));
		if (cooldown <= 0) {
			this.hasCooldown = false;
			return 0;
		}

		return cooldown;
	}

	/**
	 * Compares sent arguments to cooldowns.yml
	 * 
	 * @param argumentCooldownMap A HashMap of arguments with their cooldown
	 * @return
	 */
	private List<String[]> getPassedArguments(HashMap<String[], Double> argumentCooldownMap) {
		List<String[]> passedArgSet = new ArrayList<>();

		for (int i = 0; i < args.length; i++) {
			String sentArgument = args[i];
			for (String[] cooldownedArgs : argumentCooldownMap.keySet()) {

				// Defined more than the given command
				if (cooldownedArgs.length > args.length)
					continue;

				// A simple catch to prevent bypassing command with
				// spamming letters at the end. Example:
				// /warp home a a
				String arg;
				try {
					arg = cooldownedArgs[i];
				} catch (ArrayIndexOutOfBoundsException exe) {
					continue;
				}

				// Adding and removing valid matches
				if (arg.equalsIgnoreCase(sentArgument))
					passedArgSet.add(cooldownedArgs);
				else if (passedArgSet.contains(cooldownedArgs))
					passedArgSet.remove(cooldownedArgs);
			}
		}
		return passedArgSet;
	}

	// Save the command's cooldown
	// Called in message editor
	public void save() {
		ConfigurationSection section;
		ConfigurationSection cooldownSection;

		if (cooldownsConfig.getConfigurationSection(label) == null) {
			section = cooldownsConfig.createSection(label);
			cooldownSection = section.createSection("cooldowns");
		} else {
			section = cooldownsConfig.getConfigurationSection(label);
			cooldownSection = section.getConfigurationSection("cooldowns");
		}

		section.set("aliases", aliases);
		cooldownSection.set("*", cooldown);

		fileManager.saveConfigFile(cooldownsConfig, "cooldowns");

	}

	/**
	 * Deletes the cooldown on the command
	 * 
	 * @return: True if successful
	 */
	public boolean remove() {
		if (cooldownsConfig.getConfigurationSection(label) == null)
			return false;
		cooldownsConfig.set(label, null);
		fileManager.saveConfigFile(cooldownsConfig, "cooldowns");
		return true;
	}

}