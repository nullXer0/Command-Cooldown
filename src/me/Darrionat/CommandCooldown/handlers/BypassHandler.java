package me.Darrionat.CommandCooldown.handlers;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.Darrionat.CommandCooldown.Command;
import me.Darrionat.CommandCooldown.CommandCooldown;

public class BypassHandler {

	public static ArrayList<UUID> bypassList = new ArrayList<UUID>();

	public static boolean playerIsBypassing(Command command, Player p) {

		// Command Bypass
		ArrayList<UUID> bypassList = BypassHandler.bypassList;
		if (bypassList.contains(p.getUniqueId())) {
			return true;
		}

		// Permission bypass
		String message = command.message;
		if (p.hasPermission("commandcooldown.bypass." + message.replace(" ", "_"))) {
			return true;
		}

		return false;
	}

	// Used for obtaining the command from the saved string in cooldowns.yml
	public static Command getCommandFromSavedString(String s, CommandCooldown plugin) {
		String command = s.split("|")[0];
		return new Command(command, plugin);
	}

	// Used for obtaining the end of a cooldown from the saved string in
	// cooldowns.yml
	public static long getLongFromSavedString(String s) {
		return Long.parseLong(s.split("|")[1]);
	}
}