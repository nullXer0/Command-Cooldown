package me.darrionat.commandcooldown.prompts;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import net.wesjd.anvilgui.AnvilGUI;

/**
 * Represents a visual pop-up that a player may type into in order to give an output of a string.
 */
public class AnvilPrompt extends Prompt {
    private final CommandCooldownPlugin plugin;

    public AnvilPrompt(CommandCooldownPlugin plugin, Task task) {
        super(task);
        this.plugin = plugin;
    }

    @Override
    public void openPrompt() {
        new AnvilGUI.Builder()
                .onComplete((player, text) -> {  // Called when the inventory output slot is clicked
                    if (!task.valid(text))
                        return AnvilGUI.Response.text(task.onFail());
                    if (task.complete())
                        return AnvilGUI.Response.openInventory(task.run());
                    else
                        return AnvilGUI.Response.text(task.promptText());

                })
                .preventClose()            // Prevents the inventory from being closed
                .text(task.promptText())   // Sets the text the GUI should start with
                .plugin(plugin)
                .open(p);                  // Opens the GUI for the player provided
    }
}