package me.darrionat.commandcooldown.prompts;

import org.bukkit.entity.Player;

public abstract class Prompt {
    /**
     * The task to be run when the prompt is complete.
     */
    protected final Task task;
    protected final Player p;

    public Prompt(Task task) {
        this.task = task;
        this.p = task.getPlayer();
    }

    public Task getTask() {
        return task;
    }

    public Player getPlayer() {
        return p;
    }

    public abstract void openPrompt();
}