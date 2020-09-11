package me.Darrionat.CommandCooldown.commands.subcommands;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Darrionat.CommandCooldown.CommandCooldown;
import me.Darrionat.CommandCooldown.commands.BaseCommand;
import me.Darrionat.CommandCooldown.handlers.AddArgumentsEditor;
import me.Darrionat.CommandCooldown.handlers.AddCommandEditor;
import me.Darrionat.CommandCooldown.handlers.BypassHandler;
import me.Darrionat.CommandCooldown.utils.Utils;

public class SubCommands extends BaseCommand {

	public SubCommands(CommandCooldown plugin) {
		super(plugin);
		this.plugin = plugin;
		config = plugin.getConfig();
	}

	private CommandCooldown plugin;
	private Player p;
	private String[] args;
	private FileConfiguration config;

	// Permissions
	private final String commandBasePermission = "commandcooldown.command.";
	private final String helpPermission = commandBasePermission + "help";
	private final String bypassPermission = commandBasePermission + "bypass";
	private final String reloadPermission = commandBasePermission + "reload";
	private final String addPermission = commandBasePermission + "add";
	private final String addArgumentsPermission = commandBasePermission + "addarguments";
	private final String removePermission = commandBasePermission + "remove";
	private final String addAliasPermission = commandBasePermission + "addalias";

	/*
	 * cmds.add("  &7/cc list &aShows a list of all commands");
	 * cmds.add("  &7/cc bypass &aBypasses cooldowns");
	 * cmds.add("  &7/cc reload &aReloads the config.yml");
	 * cmds.add("  &7/cooldowns &aShows a player's cooldowns");
	 * cmds.add("  &7/cd &aShows a player's cooldowns");
	 * cmds.add("  &7/cc add [cooldown] [command] [arg1] [arg2]...");
	 * cmds.add("  &7/cc newalias [alias] [command]");
	 * cmds.add("  &7/cc remove [command...]"); cmds.
	 * add("  &7Permission &aAdd 'commandcooldowns.byass.command_name' To bypass one command"
	 * );
	 */

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

		if (bypassList.contains(uuid)) {
			bypassList.remove(uuid);
			p.sendMessage(Utils.chat(config.getString("Messages.BypassOff")));
			return;
		}
		bypassList.add(uuid);
		p.sendMessage(Utils.chat(config.getString("Messages.BypassOn")));
	}

	private void reload() {
		if (!playerHasPermission(reloadPermission))
			return;
		plugin.reloadConfig();
		p.sendMessage(Utils.chat(config.getString("Messages.ReloadSuccessful")));
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
	}

	private void addAlias() {
		if (!playerHasPermission(addAliasPermission))
			return;
	}

	private void cancel() {
		if (!playerIsInEditor()) {
			p.sendMessage(messages.getMessage(messages.playerIsNotInEditor));
			return;
		}
		UUID uuid = p.getUniqueId();
		AddCommandEditor.awaitingLabelSet.remove(uuid);
		AddCommandEditor.awaitingCooldownSet.remove(uuid);
		AddCommandEditor.commandUUIDMap.remove(uuid);

		AddArgumentsEditor.awaitingLabelSet.remove(uuid);
		AddArgumentsEditor.awaitingArgumentsSet.remove(uuid);
		AddArgumentsEditor.awaitingCooldownSet.remove(uuid);
		AddArgumentsEditor.commandUUIDMap.remove(uuid);

		p.sendMessage(messages.getMessage(messages.cancelledEdit));
	}

	private boolean playerIsInEditor() {
		UUID uuid = p.getUniqueId();
		// /cc add
		if (AddCommandEditor.awaitingLabelSet.contains(uuid) || AddCommandEditor.awaitingCooldownSet.contains(uuid))
			return true;

		// /cc addarguments
		if (AddArgumentsEditor.awaitingLabelSet.contains(uuid) || AddArgumentsEditor.awaitingArgumentsSet.contains(uuid)
				|| AddArgumentsEditor.awaitingCooldownSet.contains(uuid))
			return true;

		return false;
	}

	private boolean playerHasPermission(String permission) {
		if (p.hasPermission(permission)) {
			return true;
		}
		p.sendMessage(Utils.chat(config.getString("Messages.NoPermission").replace("%perm%", permission)));
		return false;
	}
}