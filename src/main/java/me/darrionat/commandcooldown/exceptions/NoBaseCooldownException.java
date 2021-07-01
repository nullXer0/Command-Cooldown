package me.darrionat.commandcooldown.exceptions;

public class NoBaseCooldownException extends RuntimeException {
    public NoBaseCooldownException(String commandLabel) {
        super("The command /" + commandLabel + " has no base cooldown.");
    }
}