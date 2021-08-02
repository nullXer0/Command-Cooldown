package me.darrionat.commandcooldown.prompts;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.cooldowns.Cooldown;
import me.darrionat.commandcooldown.cooldowns.SavedCommand;
import me.darrionat.commandcooldown.gui.CooldownEditorGui;
import me.darrionat.pluginlib.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CreateCooldownTask extends DurationTask {
    private final SavedCommand command;
    private String args;

    public CreateCooldownTask(CommandCooldownPlugin plugin, SavedCommand command, Player p) {
        super(plugin, null, p);
        this.command = command;
    }

    @Override
    public Inventory run() {
        if (!complete())
            throw new IllegalStateException("Task is not complete");
        Cooldown cooldown = new Cooldown(command, args, duration);
        commandService.addCooldown(command, cooldown);
        return new CooldownEditorGui(plugin, cooldown).getInventory(p);
    }

    @Override
    public String promptText() {
        if (args == null)
            return Utils.toColor("&aEnter command arguments");
        return super.promptText();
    }

    @Override
    public String onFail() {
        if (args == null)
            return Utils.toColor("&cProvide command arguments (excluding the label)");
        return super.onFail();
    }

    @Override
    public boolean valid(String input) {
        if (args == null) {
            args = input;
            return true;
        }
        return super.valid(input);
    }

    @Override
    public boolean complete() {
        return args != null && super.complete();
    }
}