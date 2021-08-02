package me.darrionat.commandcooldown.prompts;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.cooldowns.Cooldown;
import me.darrionat.commandcooldown.cooldowns.SavedCommand;
import me.darrionat.commandcooldown.gui.CommandEditorGui;
import me.darrionat.pluginlib.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CreateCommandTask extends DurationTask {
    private String label;

    public CreateCommandTask(CommandCooldownPlugin plugin, Player p) {
        super(plugin, null, p);
    }

    @Override
    public Inventory run() {
        if (!complete())
            throw new IllegalStateException("Task is not complete");
        SavedCommand command = new SavedCommand(label);
        Cooldown baseCooldown = new Cooldown(command, duration);
        commandService.addCooldown(command, baseCooldown);
        return new CommandEditorGui(plugin, commandService.getCommand(command), 1).getInventory(p);
    }

    @Override
    public String promptText() {
        if (label == null)
            return Utils.toColor("&aEnter a command label");
        return super.promptText();
    }

    @Override
    public String onFail() {
        if (label == null)
            return Utils.toColor("&cInput must be one word");
        return super.onFail();
    }

    @Override
    public boolean valid(String input) {
        if (label == null) {
            if (input.contains(" ")) return false;
            label = input;
            return true;
        }
        return super.valid(input);
    }

    @Override
    public boolean complete() {
        return label != null && super.complete();
    }
}