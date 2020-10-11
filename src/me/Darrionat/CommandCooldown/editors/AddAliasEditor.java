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

public class AddAliasEditor implements Listener, Editor {

	private CommandCooldown plugin;
	private MessageService messages;

	public AddAliasEditor(CommandCooldown plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
		messages = new MessageService(plugin);

		queueSetsList.add(awaitingAliasSet);
		queueSetsList.add(awaitingLabelSet);
	}

	// Contains all queue sets
	private List<Set<UUID>> queueSetsList = new ArrayList<>();

	// Sets up the players command for saving
	public static HashMap<UUID, Command> commandUUIDMap = new HashMap<>();

	// Indicator that the player is ready to send a label in chat
	public static Set<UUID> awaitingLabelSet = new HashSet<>();

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
		awaitingAliasSet.add(uuid);
		commandUUIDMap.put(uuid, new Command(sentMessage, plugin));

		p.sendMessage(messages.getMessage(messages.waitingForAlias).replace("%command%", sentMessage));
	}

	// Indicator that the player is ready to send the alias in chat
	public static Set<UUID> awaitingAliasSet = new HashSet<>();

	@EventHandler
	public void onAliasMessage(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		String sentMessage = e.getMessage();

		if (!awaitingAliasSet.contains(uuid))
			return;

		e.setCancelled(true);

		if (sentMessage.contains(" ")) {
			p.sendMessage(messages.getMessage(messages.notACommandLabel));
			return;
		}

		Command command = commandUUIDMap.get(uuid);
		List<String> aliases = command.aliases;
		aliases.add(sentMessage);
		command.aliases = aliases;
		command.save();

		awaitingAliasSet.remove(uuid);
		commandUUIDMap.remove(uuid);

		p.sendMessage(messages.getMessage(messages.newAlias));
	}

	@Override
	public List<Set<UUID>> getQueueSets() {
		return queueSetsList;
	}

	@Override
	public HashMap<UUID, Command> getCommandMap() {
		return commandUUIDMap;
	}

}