package me.Darrionat.CommandCooldown.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Darrionat.CommandCooldown.CommandCooldown;
import me.Darrionat.CommandCooldown.commands.subcommands.SubCommands;
import me.Darrionat.CommandCooldown.handlers.MessageService;
import me.Darrionat.CommandCooldown.utils.Utils;

public class BaseCommand implements CommandExecutor {

	private CommandCooldown plugin;
	protected MessageService messages;

	public BaseCommand(CommandCooldown plugin) {
		this.plugin = plugin;
		messages = new MessageService(plugin);
		plugin.getCommand("commandcooldown").setExecutor(this);
		setupCmdMsgs();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			String adminPerm = "commandcooldown.admin";
			if (!p.hasPermission(adminPerm)) {
				p.sendMessage(messages.getMessage(messages.noPermission).replace("%perm%", adminPerm));
				return true;
			}
		}

		if (args.length == 0) {
			sendBaseMessage(sender);
			return true;
		}

		SubCommands subCommands = new SubCommands(plugin);

		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (subCommands.isSubCommand(p, args))
				return true;
		}

		// At the end, so if none of the conditions are met, it will send this message.
		sendBaseMessage(sender);
		return true;
	}

	public void sendBaseMessage(CommandSender sender) {
		sender.sendMessage(Utils.chat("&a&lCOMMAND COOLDOWN v" + plugin.getDescription().getVersion()));
		sender.sendMessage(Utils.chat("  &7Author: Darrionat"));
		sender.sendMessage(Utils.chat("  &7Support: https://discord.gg/xNKrH5Z"));
		sender.sendMessage(Utils.chat("  &7/cc help - &oFor additional information"));
	}

	private List<String> cmds = new ArrayList<String>();
	private int listSize;
	private int pageAmt;

	public void sendHelpPage(CommandSender sender, String pageString) {
		int page = 1;
		try {
			page = Integer.parseInt(pageString);
		} catch (NumberFormatException exe) {
			// Keep page as 1
		}
		listSize = cmds.size();
		pageAmt = (listSize + 5 - 1) / 5;
		if (page > pageAmt || page < 1) {
			sendHelpPage(sender, "1");
			page = 1;
		}

		String topMsg = "&a&l" + plugin.getDescription().getName() + " v" + plugin.getDescription().getVersion()
				+ " Commands";
		sender.sendMessage(Utils.chat(topMsg));
		for (int i = page * 5 - 5; i <= (page * 5 - 1); i++) {
			if (i == (listSize)) {
				break;
			}
			sender.sendMessage(Utils.chat(cmds.get(i)));
		}
		sender.sendMessage(Utils.chat("&7Page " + String.valueOf(page) + "/" + pageAmt));
	}

	private void setupCmdMsgs() {
		cmds.add("  &7/cc help [page] &aInformation on commands");
		cmds.add("  &7/cc list &aShows a list of all cooldowned commands");
		cmds.add("  &7/cc bypass &aBypasses cooldowns");
		cmds.add("  &7/cc reload &aReloads the config.yml");
		cmds.add("  &7/cooldowns &aShows a player's cooldowns");
		cmds.add("  &7/cd &aShows a player's cooldowns");
		cmds.add("  &7/cc add [cooldown] [command] [arg1] [arg2]...");
		cmds.add("  &7/cc newalias [alias] [command]");
		cmds.add("  &7/cc remove [command...]");
		cmds.add("  &7Permission &aAdd 'commandcooldowns.byass.command_name' To bypass one command");
	}
}