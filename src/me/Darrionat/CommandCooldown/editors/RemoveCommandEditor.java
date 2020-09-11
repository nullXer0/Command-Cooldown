package me.Darrionat.CommandCooldown.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.Darrionat.CommandCooldown.Command;
import me.Darrionat.CommandCooldown.CommandCooldown;
import me.Darrionat.CommandCooldown.handlers.MessageService;
import me.Darrionat.CommandCooldown.utils.Utils;

public class RemoveCommandEditor implements Listener, Editor {

	private CommandCooldown plugin;
	private MessageService messages;

	public RemoveCommandEditor(CommandCooldown plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
		messages = new MessageService(plugin);
	}

	// Sets up the players command for removing
	public static HashMap<UUID, Command> commandUUIDMap = new HashMap<>();

	// Indicator that the player is ready to send a label in chat
	public static Set<UUID> awaitingLabelSet = new HashSet<>();

	// Recieves label /cc remove (Part 1)
	@EventHandler
	public void onLabelMessage(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		String sentMessage = e.getMessage();

		if (!awaitingLabelSet.contains(uuid))
			return;

		e.setCancelled(true);

		if (sentMessage.contains(" ")) {
			p.sendMessage(messages.getMessage(messages.notACommandLabel));
			return;
		}

		awaitingLabelSet.remove(uuid);
		awaitingArgumentsSet.add(uuid);
		commandUUIDMap.put(uuid, new Command(sentMessage, plugin));

		String addArugmentsMessage = messages.getMessage(messages.removeLabelChosen);
		addArugmentsMessage = addArugmentsMessage.replace("%command%", "/" + sentMessage);
		p.sendMessage(addArugmentsMessage);

	}

	// Indicator that the player is ready to send the arguments in chat
	public static Set<UUID> awaitingArgumentsSet = new HashSet<>();

	@EventHandler
	public void onArgumentsMessage(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		String sentMessage = e.getMessage();

		if (!awaitingArgumentsSet.contains(uuid))
			return;

		e.setCancelled(true);

		String[] args = Utils.getArgs(sentMessage);
		if (args == null) {
			p.sendMessage(messages.getMessage(messages.notArguments));
			return;
		}

		Command command = commandUUIDMap.get(uuid);
		// Remove the entire command from cooldown
		if (args[0].equalsIgnoreCase("*") && args.length == 1) {
			boolean success = command.remove();
			processRemovalOutcome(p, success);
			return;
		}
		// Make a new command object with the arguments to check for a cooldown
		Command commandWithArguments = new Command("/" + command.label + " " + sentMessage, plugin);

		if (commandWithArguments.hasCooldown) {
			p.sendMessage(messages.getMessage(messages.doesNotExist));
			return;
		}

		boolean success = commandWithArguments.remove();
		processRemovalOutcome(p, success);
	}

	private void processRemovalOutcome(Player p, boolean success) {
		UUID uuid = p.getUniqueId();

		if (success) {
			awaitingArgumentsSet.remove(uuid);
			commandUUIDMap.remove(uuid);
			String removeSuccessful = messages.getMessage(messages.deleteSuccessful);
			p.sendMessage(removeSuccessful);
			return;
		}
		String removalnotSuccessful = messages.getMessage(messages.doesNotExist);
		p.sendMessage(removalnotSuccessful);
	}

	@Override
	public List<Set<UUID>> getQueueSets() {
		List<Set<UUID>> queueSetsList = new ArrayList<>();
		queueSetsList.add(awaitingLabelSet);
		return queueSetsList;
	}

	@Override
	public HashMap<UUID, Command> getCommandMap() {
		return commandUUIDMap;
	}
}
