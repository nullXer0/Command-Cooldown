package me.Darrionat.CommandCooldown.commands.subcommands;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Darrionat.CommandCooldown.CommandCooldown;
import me.Darrionat.CommandCooldown.commands.BaseCommand;
import me.Darrionat.CommandCooldown.handlers.BypassHandler;
import me.Darrionat.CommandCooldown.handlers.CommandEditor;
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

		return false;
	}

	public void help() {
		if (!playerHasPermission(helpPermission))
			return;
		if (args.length == 1) {
			sendHelpMessage(p, 1);
			return;
		}
		helpMessagePage(p, args[1]);
	}

	public void bypass() {
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

	public void reload() {
		if (!playerHasPermission(reloadPermission))
			return;
		plugin.reloadConfig();
		p.sendMessage(Utils.chat(config.getString("Messages.ReloadSuccessful")));
	}

	// /cc add
	// Say a command, without arguments and without a /, that you'd like to add
	// a cooldown to
	// shop
	// What would you like the base cooldown to be in seconds?
	// 60
	public void add() {
		if (!playerHasPermission(addPermission))
			return;
		if (playerIsInEditor()) {
			p.sendMessage(messages.getMessage(p, messages.playerIsInEditor));
			return;
		}
		p.sendMessage(messages.getMessage(p, messages.waitingForLabel));
		CommandEditor.awaitingLabelSet.add(p.getUniqueId());
	}

	// Say a command, without arguments and without a /, that'd you'd like to add a
	// cooldown to
	// shop
	// %command% selected, please say in chat what arguments you want to add a
	// cooldown to
	// notch *
	// What cooldown would you like to add to %command_with_arguments%?
	public void addArguments() {
		if (!playerHasPermission(addArgumentsPermission))
			return;

	}

	public void cancel() {

	}

	public void remove() {
		if (!playerHasPermission(removePermission))
			return;
	}

	public void addAlias() {
		if (!playerHasPermission(addAliasPermission))
			return;
	}

	private boolean playerIsInEditor() {
		UUID uuid = p.getUniqueId();
		// /cc add
		if (CommandEditor.awaitingCooldownSet.contains(uuid) || CommandEditor.awaitingLabelSet.contains(uuid)) {
			return true;
		}
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