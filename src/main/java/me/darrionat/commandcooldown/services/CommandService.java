package me.darrionat.commandcooldown.services;

import me.darrionat.commandcooldown.cooldowns.Cooldown;
import me.darrionat.commandcooldown.cooldowns.SavedCommand;
import me.darrionat.commandcooldown.interfaces.ICommandService;
import me.darrionat.commandcooldown.interfaces.ICooldownsRepository;

public class CommandService implements ICommandService {
    private final ICooldownsRepository cooldownsRepo;

    public CommandService(ICooldownsRepository cooldownsRepo) {
        this.cooldownsRepo = cooldownsRepo;
    }

    @Override
    public void addAlias(SavedCommand command, String alias) {
        cooldownsRepo.removeCommandCooldown(command);
        command.addAlias(alias.toLowerCase());
        cooldownsRepo.addCommandCooldown(command);
    }

    @Override
    public void removeAlias(SavedCommand command, String alias) {
        cooldownsRepo.removeCommandCooldown(command);
        command.removeAlias(alias);
        cooldownsRepo.addCommandCooldown(command);
    }

    @Override
    public void addCooldown(SavedCommand command, Cooldown cooldown) {
        cooldownsRepo.removeCommandCooldown(command);
        command.addCooldown(cooldown);
        cooldownsRepo.addCommandCooldown(command);
    }

    @Override
    public void removeCooldown(SavedCommand command, Cooldown cooldown) {
        cooldownsRepo.removeCommandCooldown(command);
        command.removeCooldown(cooldown);
        cooldownsRepo.addCommandCooldown(command);
    }

    @Override
    public void setCooldown(Cooldown cooldown, double duration) {
        SavedCommand command = cooldown.getCommand();
        cooldownsRepo.removeCommandCooldown(command);
        cooldown.setDuration(duration);
        cooldownsRepo.addCommandCooldown(command);
    }

    @Override
    public SavedCommand getCommand(String label) {
        return cooldownsRepo.getSavedCommand(label);
    }

    @Override
    public SavedCommand getCommand(SavedCommand command) {
        return getCommand(command.getLabel());
    }
}