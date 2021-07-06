package me.darrionat.commandcooldown.prompts;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.cooldowns.Cooldown;
import me.darrionat.commandcooldown.cooldowns.SavedCommand;
import me.darrionat.commandcooldown.gui.CommandEditorGui;
import me.darrionat.commandcooldown.interfaces.ICommandService;
import me.darrionat.commandcooldown.utils.Duration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class DurationTask extends Task {
    protected final CommandCooldownPlugin plugin;
    protected final ICommandService commandService;
    private final Cooldown cooldown;

    protected double duration = -1;

    /**
     * Constructs a new {@code ChangeCooldownDurationTask} to change the duration of the cooldown.
     *
     * @param plugin   The plugin.
     * @param cooldown The cooldown that will be changed.
     */
    public DurationTask(CommandCooldownPlugin plugin, Cooldown cooldown, Player p) {
        super(p);
        this.plugin = plugin;
        this.commandService = plugin.getCommandService();
        this.cooldown = cooldown;
    }

    @Override
    public Inventory run() {
        if (!complete())
            throw new IllegalStateException("Task is not complete");
        commandService.setCooldown(cooldown, duration);
        SavedCommand command = commandService.getCommand(cooldown.getCommand().getLabel());
        return new CommandEditorGui(plugin, command, 1).getInventory(p);
    }

    @Override
    public String promptText() {
        return "Enter a duration (s/m/h/d/w/y)";
    }

    @Override
    public String onFail() {
        return "Invalid duration";
    }

    @Override
    public boolean valid(String input) {
        if (duration != -1) // Already valid
            return true;
        // Duration not valid yet
        try {
            // Valid duration
            double duration = Duration.parseDuration(input);
            if (duration < -1) return false;
            this.duration = duration;
            return true;
        } catch (NumberFormatException e) {
            // Invalid duration
            return false;
        }
    }

    @Override
    public boolean complete() {
        return duration != -1;
    }
}