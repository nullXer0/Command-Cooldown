package me.darrionat.commandcooldown.cooldowns;

import java.util.ArrayList;
import java.util.List;

public abstract class Command {
    private final String label;
    private final List<String> aliases;

    public Command(String label) {
        this(label, new ArrayList<>());
    }

    public Command(String label, List<String> aliases) {
        this.label = label;
        this.aliases = aliases;
    }

    /**
     * Gets the label of the command. The label defines that command itself.
     * <p>
     * For example, the command {@code /tp} has the label "{@code tp}".
     *
     * @return returns the label of the command.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Adds an alternative label that still executes the command.
     *
     * @param label The alias.
     */
    public void addAlias(String label) {
        aliases.add(label);
    }

    /**
     * Removes an alternative label that still executes the command.
     *
     * @param label The alias.
     */
    public void removeAlias(String label) {
        aliases.remove(label);
    }

    /**
     * Get all aliases of the command.
     *
     * @return The aliases of the command.
     */
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Command))
            return false;
        Command b = (Command) obj;
        return label.equalsIgnoreCase(b.label);
    }
}