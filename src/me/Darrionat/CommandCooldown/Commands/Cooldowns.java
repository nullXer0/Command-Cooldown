package me.Darrionat.CommandCooldown.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Darrionat.CommandCooldown.CommandCooldown;
import me.Darrionat.CommandCooldown.Cooldown;
import me.Darrionat.CommandCooldown.handlers.MessageService;
import me.Darrionat.CommandCooldown.utils.Utils;

public class Cooldowns implements CommandExecutor {

	private CommandCooldown plugin;
	private MessageService messages;

	public Cooldowns(CommandCooldown plugin) {
		this.plugin = plugin;
		plugin.getCommand("cooldowns").setExecutor(this);
		messages = new MessageService(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(messages.getMessage(messages.onlyPlayers));
			return true;
		}
		Player p = (Player) sender;
		String cooldownPerm = "commandcooldown.cooldowns";
		if (!p.hasPermission(cooldownPerm)) {
			p.sendMessage(messages.getMessage(messages.noPermission.replace("perm%", cooldownPerm)));
			return true;
		}

		UUID uuid = p.getUniqueId();
		List<Cooldown> cooldowns = plugin.cooldownList;
		List<String> commands = new ArrayList<String>();

		for (Cooldown cooldown : cooldowns) {
			if (cooldown.getPlayerUUID() != uuid)
				continue;

			String command = cooldown.getCommand();
			if (cooldown.getTimeRemainingMillis() <= 0) {
				continue;
			}
			commands.add(command);

		}
		if (commands.isEmpty()) {
			p.sendMessage(messages.getMessage(messages.noCooldowns));
			return true;
		}
		p.sendMessage(messages.getMessage(messages.onCooldown));
		for (String s : commands) {
			p.sendMessage(Utils.chat("  &7" + s));

		}
		return true;
	}
}