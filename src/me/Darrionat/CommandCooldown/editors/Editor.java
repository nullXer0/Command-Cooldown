package me.Darrionat.CommandCooldown.editors;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import me.Darrionat.CommandCooldown.Command;

public interface Editor {
	// Queue sets determine where the player is within the editing process
	public List<Set<UUID>> getQueueSets();

	public HashMap<UUID, Command> getCommandMap();
}
