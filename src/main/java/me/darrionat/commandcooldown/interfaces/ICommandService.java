package me.darrionat.commandcooldown.interfaces;

import me.darrionat.commandcooldown.cooldowns.Cooldown;
import me.darrionat.commandcooldown.cooldowns.SavedCommand;

public interface ICommandService {
    void addAlias(SavedCommand command, String alias);

    void removeAlias(SavedCommand command, String alias);

    void addCooldown(SavedCommand command, Cooldown cooldown);

    void removeCooldown(SavedCommand command, Cooldown cooldown);

    void setCooldown(Cooldown cooldown, double duration);

    SavedCommand getCommand(String label);

    SavedCommand getCommand(SavedCommand command);
}