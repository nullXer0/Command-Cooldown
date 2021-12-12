package me.darrionat.commandcooldown.cooldowns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a set of arguments with a defined cooldown
 */
public class Cooldown {
    public static final String BASE_COOLDOWN = "*";
    private final SavedCommand command;
    private final List<String> args;
    private double duration;

    /**
     * Creates a base cooldown.
     *
     * @param command  The {@link Command} these arguments belong to.
     * @param duration The duration of the cooldown in seconds.
     */
    public Cooldown(SavedCommand command, double duration) {
        this(command, BASE_COOLDOWN, duration);
    }

    /**
     * Constructs a new {@code Cooldown} with a string representing the arguments of the command.
     *
     * @param command  The {@link Command} these arguments belong to.
     * @param args     The arguments for the command as a string.
     * @param duration The duration of the cooldown in seconds.
     */
    public Cooldown(SavedCommand command, String args, double duration) {
        this(command, Arrays.asList(args.split(" ")), duration);
    }

    /**
     * Creates a cooldown with defined arguments.
     *
     * @param command  The {@link Command} these arguments belong to.
     * @param args     The arguments for the command.
     * @param duration The duration of the cooldown in seconds.
     */
    public Cooldown(SavedCommand command, List<String> args, double duration) {
        this.command = command;
        List<String> argsLower = new ArrayList<>();
        for (String arg : args)
            argsLower.add(arg.toLowerCase());
        this.args = argsLower;
        this.duration = duration;
    }

    public SavedCommand getCommand() {
        return command;
    }

    /**
     * Determines if this cooldown is a base cooldown of a {@link Command}.
     *
     * @return returns {@code true} if this cooldown is simply a base cooldown; otherwise {@code false}.
     */
    public boolean isBaseCooldown() {
        return args.size() == 1 && args.get(0).equals(BASE_COOLDOWN);
    }

    public List<String> getArgs() {
        return args;
    }

    /**
     * Gets the duration of the cooldown in seconds.
     *
     * @return The duration in seconds.
     */
    public double getDuration() {
        return duration;
    }

    /**
     * Change the duration of this cooldown.
     *
     * @param duration The new duration.
     */
    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String toCommandString() {
        return "/" + command.getLabel() + " " + String.join(" ", args);
    }

    public Cooldown clone() {
        return new Cooldown(command, args, duration);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Cooldown))
            return false;
        Cooldown b = (Cooldown) obj;
        // Does not check cooldown because there should only be one that exists anyways
        // and this fixes an issue with permissions
        return command.equals(b.command) && args.equals(b.args);
    }
}