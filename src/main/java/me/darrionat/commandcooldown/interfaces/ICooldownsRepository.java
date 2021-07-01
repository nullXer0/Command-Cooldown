package me.darrionat.commandcooldown.interfaces;

import me.darrionat.commandcooldown.cooldowns.SavedCommand;

import java.util.List;

public interface ICooldownsRepository extends Repository {
    List<SavedCommand> getCommandCooldowns();

    /**
     * Adds a command with a cooldown.
     *
     * @param command The command to add.
     */
    void addCommandCooldown(SavedCommand command);

    /**
     * Removes a command from having a cooldown.
     *
     * @param command The command to remove.
     */
    void removeCommandCooldown(SavedCommand command);
}