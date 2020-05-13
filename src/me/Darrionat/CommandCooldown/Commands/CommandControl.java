package me.Darrionat.CommandCooldown.Commands;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Darrionat.CommandCooldown.Main;
import me.Darrionat.CommandCooldown.Utils.Utils;

public class CommandControl implements CommandExecutor {

	private Main plugin;

	public CommandControl(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("commandcooldown").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration config = plugin.getConfig();
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (!p.hasPermission("commandcooldown.admin")) {
				p.sendMessage(Utils
						.chat(config.getString("Messages.NoPermission").replace("%perm%", "commandcooldown.admin")));
				return true;
			}
		}

		if (args.length == 0) {
			sender.sendMessage(Utils.chat("&a&lCOMMAND COOLDOWN v" + plugin.getDescription().getVersion()));
			sender.sendMessage(Utils.chat("  &7Author: Darrionat"));
			sender.sendMessage(Utils.chat("  &7Support: https://discord.gg/xNKrH5Z"));
			sender.sendMessage(Utils.chat("  &7/" + label + " help - &oFor additional information"));
			return true;
		}
		if (args[0].equalsIgnoreCase("help")) {
			// /bansplus help
			if (args.length == 1) {
				helpMessagePage(sender, "1");
				return true;
			}
			// /bansplus help #
			helpMessagePage(sender, args[1]);

			return true;
		}

		if (args[0].equalsIgnoreCase("list")) {
			List<String> list = new ArrayList<String>();
			for (String key : config.getKeys(false)) {

				if (key.equalsIgnoreCase("Messages") || key.equalsIgnoreCase("checkUpdates")
						|| key.equalsIgnoreCase("SendBypassMessage")) {
					continue;
				}

				list.add("&a&l[" + key + "]");

				// Cooldown
				int cooldown = config.getInt(key + ".cooldown");
				long hours = cooldown / 3600;
				long minutes = (cooldown % 3600) / 60;
				long seconds = cooldown % 60;
				String timeString = String.format("%02dh %02dm %02ds", hours, minutes, seconds);
				list.add("  &7Cooldown: " + String.valueOf(timeString));

				// Aliases
				String aliases = "  &7Aliases: ";
				for (String s : config.getConfigurationSection(key).getStringList("aliases")) {
					aliases = aliases + s + ", ";
				}
				list.add(aliases);
			}
			for (String s : list) {
				sender.sendMessage(Utils.chat(s));
			}
			return true;
		}

		// /cc remove command
		if (args[0].equalsIgnoreCase("remove")) {
			String command = args[1];
			String fullCommand = command;
			for (int i = 2; i <= 100; i++) {
				try {
					String cmdArg = args[i];
					fullCommand = fullCommand + " " + cmdArg;
					// cArgs.add(args[i]);
				} catch (ArrayIndexOutOfBoundsException exe) {
					break;
				}
			}
			if (config.getConfigurationSection(fullCommand) == null) {
				sender.sendMessage(Utils.chat(config.getString("Messages.DoesNotExist")));
				return true;
			}
			config.set(fullCommand, null);
			plugin.saveConfig();
			sender.sendMessage(Utils.chat(config.getString("Messages.DeleteSuccessful")));
			return true;
		}

		// /cc add 30d command args...
		if (args[0].equalsIgnoreCase("add")) {
			if (args.length < 3) {
				sender.sendMessage(Utils.chat("  &7/" + label + " add cooldown command arg1 arg2 arg3..."));
				return true;
			}
			if (args[1].contains("/")) {
				args[1].replace("/", "");
			}
			int cooldown;
			try {
				cooldown = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				sender.sendMessage(Utils.chat(config.getString("Messages.NotNumber")));
				return true;
			}

			String command = args[2];
			String fullCommand = command;
			for (int i = 3; i <= 100; i++) {
				try {
					String cmdArg = args[i];
					fullCommand = fullCommand + " " + cmdArg;
				} catch (ArrayIndexOutOfBoundsException exe) {
					break;
				}
			}
			LinkedHashMap<ConfigurationSection, Integer> cooldownMap = new LinkedHashMap<ConfigurationSection, Integer>();
			LinkedHashMap<ConfigurationSection, List<String>> aliasMap = new LinkedHashMap<ConfigurationSection, List<String>>();
			for (String key : config.getKeys(false)) {
				if (key.equalsIgnoreCase("checkUpdates") || key.equalsIgnoreCase("SendBypassMessage")
						|| key.equalsIgnoreCase("Messages")) {
					continue;
				}
				ConfigurationSection section = config.getConfigurationSection(key);
				cooldownMap.put(section, section.getInt("cooldown"));
				if (section.getStringList("aliases") != null) {
					aliasMap.put(section, section.getStringList("aliases"));
				}
				config.set(key, null);
			}
			if (args.length > 3) {
				config.createSection(fullCommand);
				ConfigurationSection section = config.getConfigurationSection(fullCommand);
				section.set("cooldown", cooldown);
			}
			// Old things
			for (ConfigurationSection section : cooldownMap.keySet()) {
				config.createSection(section.getName());
				ConfigurationSection cSection = config.getConfigurationSection(section.getName());
				int sectionCooldown = cooldownMap.get(section);
				cSection.set("cooldown", sectionCooldown);
			}
			for (ConfigurationSection section : aliasMap.keySet()) {
				ConfigurationSection cSection = config.getConfigurationSection(section.getName());
				cSection.set("aliases", aliasMap.get(section));
			}
			// End of old things
			if (args.length == 3) {
				config.createSection(fullCommand);
				ConfigurationSection section = config.getConfigurationSection(fullCommand);
				section.set("cooldown", cooldown);
			}
			plugin.saveConfig();
			sender.sendMessage(Utils.chat(config.getString("Messages.CreationSuccessful")));
			return true;
		}
		// /cc newalias alias command args...
		if (args[0].equalsIgnoreCase("newalias")) {
			if (args.length < 3) {
				sender.sendMessage(Utils.chat("  &7/" + label + " newalias alias command..."));
				return true;
			}
			String command = args[2];
			String fullCommand = command;
			for (int i = 3; i <= 100; i++) {
				try {
					String cmdArg = args[i];
					fullCommand = fullCommand + " " + cmdArg;

				} catch (ArrayIndexOutOfBoundsException exe) {
					break;
				}
			}
			ConfigurationSection section = config.getConfigurationSection(fullCommand);
			if (section == null) {
				sender.sendMessage(Utils.chat(config.getString("Messages.DoesNotExist")));
				return true;
			}
			if (section.getStringList("aliases") == null) {
				List<String> list = new ArrayList<String>();
				section.set("aliases", list);
			}
			List<String> aliases = section.getStringList("aliases");
			aliases.add(args[1]);
			section.set("aliases", aliases);
			plugin.saveConfig();
			sender.sendMessage(Utils.chat(config.getString("Messages.NewAlias")));
			return true;

		}
		if (args[0].equalsIgnoreCase("reload")) {
			plugin.reloadConfig();
			sender.sendMessage(Utils.chat(config.getString("Messages.ReloadSuccessful")));
			return true;
		}

		if (args[0].equalsIgnoreCase("bypass")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(Utils.chat(config.getString("Messages.NotAPlayer")));
				return true;
			}
			Player p = (Player) sender;
			String name = p.getName();
			ArrayList<String> bypassList = Utils.getBypassList();

			if (bypassList.contains(name)) {
				p.sendMessage(Utils.chat(config.getString("Messages.BypassOff")));
				bypassList.remove(name);
				return true;
			}
			p.sendMessage(Utils.chat(config.getString("Messages.BypassOn")));
			bypassList.add(name);
			return true;

		}

		// At the end, so if none of the conditions are met, it will send this message.
		sender.sendMessage(Utils.chat("&a&lCOMMAND COOLDOWN v" + plugin.getDescription().getVersion()));
		sender.sendMessage(Utils.chat("  &7Author: Darrionat"));
		sender.sendMessage(Utils.chat("  &7Support: https://discord.gg/xNKrH5Z"));
		sender.sendMessage(Utils.chat("  &7/" + label + " help - &oFor additional information"));
		return true;
	}

	public List<String> getCmdMsgs() {
		List<String> cmds = new ArrayList<String>();
		cmds.add("  &7/cc list &aShows a list of all commands");
		cmds.add("  &7/cc bypass &aBypasses cooldowns");
		cmds.add("  &7/cc reload &aReloads the config.yml");
		cmds.add("  &7/cooldowns &aShows a player's cooldowns");
		cmds.add("  &7/cd &aShows a player's cooldowns");
		cmds.add("  &7/cc add [cooldown] [command] [arg1] [arg2]...");
		cmds.add("  &7/cc newalias [alias] [command]");
		cmds.add("  &7/cc remove [command...]");
		cmds.add("  &7Permission &aAdd 'commandcooldowns.byass.command_name' To bypass one command");
		return cmds;
	}

	private int listSize;
	private int pageAmt;

	public void helpMessagePage(CommandSender sender, String pageStr) {
		int page = 1;
		try {
			page = Integer.parseInt(pageStr);
		} catch (NumberFormatException exe) {
			sendHelpMessage(sender, getCmdMsgs(), 1);
			return;
		}
		listSize = getCmdMsgs().size();
		pageAmt = (listSize + 5 - 1) / 5;
		if (page > pageAmt || page < 1) {
			helpMessagePage(sender, "1");
			return;
		}
		sendHelpMessage(sender, getCmdMsgs(), page);
		return;
	}

	public void sendHelpMessage(CommandSender sender, List<String> cmds, int page) {
		String topMsg = "&a&lCommand Cooldowns v" + plugin.getDescription().getVersion() + " Commands";
		sender.sendMessage(Utils.chat(topMsg));
		for (int i = page * 5 - 5; i <= (page * 5 - 1); i++) {
			if (i == (listSize)) {
				break;
			}
			sender.sendMessage(Utils.chat(cmds.get(i)));
		}
		sender.sendMessage(Utils.chat("&7Page " + String.valueOf(page) + "/" + pageAmt));
	}
}