package me.Darrionat.CommandCooldown.Commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Darrionat.CommandCooldown.Main;
import me.Darrionat.CommandCooldown.Listeners.CommandProcess;
import me.Darrionat.CommandCooldown.Utils.Utils;

public class Cooldowns implements CommandExecutor {

	private Main plugin;

	public Cooldowns(Main plugin) {
		this.plugin = plugin;
		plugin.getCommand("cooldowns").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration config = plugin.getConfig();
		if (!(sender instanceof Player)) {
			sender.sendMessage(Utils.chat(config.getString("Messages.OnlyPlayers")));
			return true;
		}
		Player p = (Player) sender;
		String cooldownPerm = "commandcooldown.cooldowns";
		if (!p.hasPermission(cooldownPerm)) {
			p.sendMessage(Utils.chat(config.getString("Messages.NoPermission").replace("perm%", cooldownPerm)));
			return true;
		}
		HashMap<String, Long> cooldowns = CommandProcess.cooldownMap;
		List<String> commands = new ArrayList<String>();
		for (String key : cooldowns.keySet()) {
			if (key.contains(p.getName())) {
				String command = key.replace(p.getName() + " ", "");
				ConfigurationSection section = config.getConfigurationSection(command);
				int cooldown = section.getInt("cooldown");
				long secondsLeft = ((cooldowns.get(key) / 1000) + cooldown) - (System.currentTimeMillis() / 1000);
				if (secondsLeft <= 0) {
					continue;
				}
				commands.add(command);
			}
		}
		if (commands.isEmpty()) {
			p.sendMessage(Utils.chat(config.getString("Messages.NoCooldowns")));
			return true;
		}
		p.sendMessage(Utils.chat(config.getString("Messages.OnCooldown")));
		for (String s : commands) {
			p.sendMessage(Utils.chat("  &7" + s));

		}
		return true;
	}
}
