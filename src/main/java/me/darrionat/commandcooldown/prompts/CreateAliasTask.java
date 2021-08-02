package me.darrionat.commandcooldown.prompts;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.cooldowns.SavedCommand;
import me.darrionat.commandcooldown.gui.AliasesEditorGui;
import me.darrionat.commandcooldown.interfaces.ICommandService;
import me.darrionat.pluginlib.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CreateAliasTask extends Task {
    private final CommandCooldownPlugin plugin;
    private final SavedCommand command;
    private final ICommandService commandService;
    private String label;

    public CreateAliasTask(CommandCooldownPlugin plugin, SavedCommand command, Player p) {
        super(p);
        this.plugin = plugin;
        this.command = command;
        this.commandService = plugin.getCommandService();
    }

    @Override
    public Inventory run() throws IllegalStateException {
        if (!complete())
            throw new IllegalStateException("Task is not complete");
        commandService.addAlias(command, label);
        return new AliasesEditorGui(plugin, commandService.getCommand(command), 1).getInventory(p);
    }

    @Override
    public String promptText() {
        return Utils.toColor("&aEnter an alternative label for the command");
    }

    @Override
    public String onFail() {
        return Utils.toColor("&cAn alias must only be one word");
    }

    @Override
    public boolean valid(String input) {
        if (complete()) // Already valid
            return true;
        if (input.contains(" ")) return false;
        label = input;
        return true;
    }

    @Override
    public boolean complete() {
        return label != null;
    }
}