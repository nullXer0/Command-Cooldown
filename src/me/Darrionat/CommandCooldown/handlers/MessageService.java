package me.Darrionat.CommandCooldown.handlers;

import org.bukkit.configuration.file.FileConfiguration;

import me.Darrionat.CommandCooldown.CommandCooldown;
import me.Darrionat.CommandCooldown.utils.Utils;

public class MessageService {

	private FileConfiguration messagesConfig;
	private String prefix = "";

	public MessageService(CommandCooldown plugin) {
		messagesConfig = plugin.fileManager.getDataConfig("messages");

		boolean prefixEnabled = messagesConfig.getBoolean("prefix.enabled");
		if (prefixEnabled)
			prefix = messagesConfig.getString("prefix.string");
	}

	public String cooldownMessage = "cooldownMessage";
	public String notifyConsole = "notifyConsole";
	public String doesNotExist = "doesNotExist";
	public String deleteSuccessful = "deleteSuccessful";
	public String creationSuccessful = "creationSuccessful";
	public String notNumber = "notNumber";
	public String noCommands = "noCommands";
	public String noPermission = "noPermission";
	public String reloadSuccessful = "reloadSuccessful";
	public String toggleBypass = "toggleBypass";
	public String notAPlayer = "notAPlayer";
	public String bypassingCooldown = "bypassingCooldown";
	public String newAlias = "newAlias";
	public String onlyPlayers = "onlyPlayers";
	public String onCooldown = "onCooldown";
	public String noCooldowns = "noCooldowns";
	public String waitingForLabel = "waitingForLabel";
	public String addCooldown = "addCooldown";
	public String notACommandLabel = "notACommandLabel";
	public String notACooldown = "notACooldown";
	public String createdCooldown = "createdCooldown";
	public String playerIsInEditor = "playerIsInEditor";
	public String addArguments = "addArguments";
	public String notArguments = "notArguments";
	public String addCooldownToArguments = "addCooldownToArguments";
	public String createdArgsCooldown = "createdArgsCooldown";
	public String playerIsNotInEditor = "playerIsNotInEditor";
	public String cancelledEdit = "cancelledEdit";
	public String removeLabelChosen = "removeLabelChosen";

	// Returns String in case placeholders are used
	public String getMessage(String messageKey) {
		return Utils.chat(prefix + messagesConfig.getString(messageKey));
	}
}