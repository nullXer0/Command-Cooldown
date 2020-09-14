package me.Darrionat.CommandCooldown.commands.subcommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Darrionat.CommandCooldown.CommandCooldown;
import me.Darrionat.CommandCooldown.commands.BaseCommand;
import me.Darrionat.CommandCooldown.editors.AddAliasEditor;
import me.Darrionat.CommandCooldown.editors.AddArgumentsEditor;
import me.Darrionat.CommandCooldown.editors.AddCommandEditor;
import me.Darrionat.CommandCooldown.editors.Editor;
import me.Darrionat.CommandCooldown.editors.RemoveCommandEditor;
import me.Darrionat.CommandCooldown.files.FileManager;
import me.Darrionat.CommandCooldown.handlers.BypassHandler;
import me.Darrionat.CommandCooldown.utils.Utils;

public class SubCommands extends BaseCommand {

	public SubCommands(CommandCooldown plugin) {
		super(plugin);
		this.plugin = plugin;
		fileManager = new FileManager(plugin);
	}

	private CommandCooldown plugin;
	private FileManager fileManager;
	private Player p;
	private String[] args;

	// Permissions
	private final String commandBasePermission = "commandcooldown.command.";
	private final String helpPermission = commandBasePermission + "help";
	private final String bypassPermission = commandBasePermission + "bypass";
	private final String reloadPermission = commandBasePermission + "reload";
	private final String addPermission = commandBasePermission + "add";
	private final String addArgumentsPermission = commandBasePermission + "addarguments";
	private final String removePermission = commandBasePermission + "remove";
	private final String addAliasPermission = commandBasePermission + "addalias";
	private final String listPermission = commandBasePermission + "list";

	// Returns if the command is a subcommand or not
	public boolean isSubCommand(Player p, String[] args) {
		this.p = p;
		this.args = args;

		if (args[0].equalsIgnoreCase("help")) {
			help();
			return true;
		}
		if (args[0].equalsIgnoreCase("bypass")) {
			bypass();
			return true;
		}
		if (args[0].equalsIgnoreCase("reload")) {
			reload();
			return true;
		}
		if (args[0].equalsIgnoreCase("list")) {
			list();
			return true;
		}
		if (args[0].equalsIgnoreCase("add")) {
			add();
			return true;
		}
		if (args[0].equalsIgnoreCase("remove")) {
			remove();
			return true;
		}

		if (args[0].equalsIgnoreCase("addAlias")) {
			addAlias();
			return true;
		}
		if (args[0].equalsIgnoreCase("addArguments") || args[0].equalsIgnoreCase("addArgs")) {
			addArguments();
			return true;
		}
		if (args[0].equalsIgnoreCase("cancel")) {
			cancel();
			return true;
		}

		return false;
	}

	private void help() {
		if (!playerHasPermission(helpPermission))
			return;
		if (args.length == 1) {
			sendHelpPage(p, "1");
			return;
		}
		sendHelpPage(p, args[1]);
	}

	private void bypass() {
		if (!playerHasPermission(bypassPermission))
			return;

		UUID uuid = p.getUniqueId();
		ArrayList<UUID> bypassList = BypassHandler.bypassList;

		String toggleBypassMessage = messages.getMessage(messages.toggleBypass);

		if (bypassList.contains(uuid)) {
			bypassList.remove(uuid);
			toggleBypassMessage = toggleBypassMessage.replace("%status%", "false");
			p.sendMessage(toggleBypassMessage);
			return;
		}

		bypassList.add(uuid);

		toggleBypassMessage = toggleBypassMessage.replace("%status%", "true");
		p.sendMessage(toggleBypassMessage);
	}

	private void reload() {
		if (!playerHasPermission(reloadPermission))
			return;
		plugin.reloadConfig();
		p.sendMessage(messages.getMessage(messages.reloadSuccessful));
	}

	private void list() {
		if (!playerHasPermission(listPermission))
			return;
		p.sendMessage(Utils.chat("&a&lCOMMAND COOLDOWN LIST\n"));

		FileConfiguration cooldownsConfig = fileManager.getDataConfig("cooldowns");

		for (String label : cooldownsConfig.getKeys(false)) {
			ConfigurationSection section = cooldownsConfig.getConfigurationSection(label);

			// Corresponds argument with its specified cooldown
			HashMap<String, Double> argsCooldownMap = new HashMap<>();
			argsCooldownMap.put("*", section.getDouble("cooldowns.*"));

			// Get cooldowns from the cooldowns section and build argsCooldownMap
			for (String argsStr : section.getConfigurationSection("cooldowns").getKeys(false)) {
				if (argsStr.equals("*"))
					continue;
				double cooldown = section.getDouble("cooldowns." + argsStr);
				argsCooldownMap.put(argsStr, cooldown);
			}

			List<String> aliases = section.getStringList("aliases");

			sendCommandInformation(label, argsCooldownMap, aliases);
		}
	}

	/**
	 * Ran in void list();
	 * 
	 * @param label:          Command's label
	 * @param argsCooldownMap Associated argument strings with their cooldown
	 * @param aliases         The aliases that can replace the label
	 */
	private void sendCommandInformation(String label, HashMap<String, Double> argsCooldownMap, List<String> aliases) {
		double baseCooldown = argsCooldownMap.get("*");
		// Example: &7warp (&a60s&7)
		p.sendMessage(Utils.chat("&7" + label + " (&a" + baseCooldown + "s&7)"));

		for (String argsStr : argsCooldownMap.keySet()) {
			double cooldown = argsCooldownMap.get(argsStr);
			p.sendMessage(Utils.chat(" &7" + label + " " + argsStr + ": &a" + cooldown));
		}
		String aliasesStr = String.join(", ", aliases);
		p.sendMessage(Utils.chat(" &7Aliases: " + aliasesStr));
	}

	private void add() {
		if (!playerHasPermission(addPermission))
			return;
		if (playerIsInEditor()) {
			p.sendMessage(messages.getMessage(messages.playerIsInEditor));
			return;
		}
		p.sendMessage(messages.getMessage(messages.waitingForLabel));
		AddCommandEditor.awaitingLabelSet.add(p.getUniqueId());
	}

	private void addArguments() {
		if (!playerHasPermission(addArgumentsPermission))
			return;
		if (playerIsInEditor()) {
			p.sendMessage(messages.getMessage(messages.playerIsInEditor));
			return;
		}
		p.sendMessage(messages.getMessage(messages.waitingForLabel));
		AddArgumentsEditor.awaitingLabelSet.add(p.getUniqueId());
	}

	// Select a command to remove a cooldown from
	private void remove() {
		if (!playerHasPermission(removePermission))
			return;
		if (playerIsInEditor()) {
			p.sendMessage(messages.getMessage(messages.playerIsInEditor));
			return;
		}
		p.sendMessage(messages.getMessage(messages.waitingForLabel));
		RemoveCommandEditor.awaitingLabelSet.add(p.getUniqueId());

	}

	private void addAlias() {
		if (!playerHasPermission(addAliasPermission))
			return;
		if (playerIsInEditor()) {
			p.sendMessage(messages.getMessage(messages.playerIsInEditor));
			return;
		}
		p.sendMessage(messages.getMessage(messages.waitingForLabel));
		AddAliasEditor.awaitingLabelSet.add(p.getUniqueId());

	}

	private void cancel() {
		if (!playerIsInEditor()) {
			p.sendMessage(messages.getMessage(messages.playerIsNotInEditor));
			return;
		}

		UUID uuid = p.getUniqueId();

		for (Editor editor : plugin.editorList) {
			for (Set<UUID> set : editor.getQueueSets()) {
				set.remove(uuid);
			}
			editor.getCommandMap().remove(uuid);
		}
		p.sendMessage(messages.getMessage(messages.cancelledEdit));
	}

	private boolean playerIsInEditor() {
		UUID uuid = p.getUniqueId();

		for (Editor editor : plugin.editorList) {
			for (Set<UUID> set : editor.getQueueSets()) {
				if (set.contains(uuid))
					return true;
			}
			if (editor.getCommandMap().containsKey(uuid))
				return true;
		}
		return false;
	}

	private boolean playerHasPermission(String permission) {
		if (p.hasPermission(permission)) {
			return true;
		}
		String noPermissionMessage = messages.getMessage(messages.noPermission);
		noPermissionMessage = noPermissionMessage.replace("%perm%", permission);
		p.sendMessage(noPermissionMessage);
		return false;
	}
}