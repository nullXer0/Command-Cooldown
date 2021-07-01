package me.darrionat.commandcooldown.prompts;

import me.darrionat.commandcooldown.CommandCooldownPlugin;

public class ChatPrompt extends Prompt {
    public ChatPrompt(CommandCooldownPlugin plugin, Task task) {
        super(task);
    }

    @Override
    public void openPrompt() {
        p.closeInventory();
        p.sendMessage(task.promptText());
        ChatPromptListener.add(task);
    }
}