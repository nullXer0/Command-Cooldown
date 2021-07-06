package me.darrionat.commandcooldown.cooldowns;

import java.util.UUID;

public class PlayerCooldown {
    /**
     * The special character that is used to separate values when the {@code PlayerCooldown} is converted to a string.
     */
    public static final String SEP = "/";
    private final UUID uuid;
    private final Cooldown cooldown;
    private final long end;

    public PlayerCooldown(UUID uuid, Cooldown cooldown, long end) {
        this.uuid = uuid;
        this.cooldown = cooldown;
        this.end = end;
    }

    public UUID getPlayer() {
        return uuid;
    }

    public Cooldown getCooldown() {
        return cooldown;
    }

    public long getEnd() {
        return end;
    }

    public boolean expired() {
        return System.currentTimeMillis() > end;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String args = String.join(" ", cooldown.getArgs());

        builder.append(uuid).append(SEP)
                .append(cooldown.getCommand().getLabel()).append(SEP)
                .append(args).append(SEP)
                .append(cooldown.getDuration()).append(SEP)
                .append(end);

        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PlayerCooldown))
            return false;
        PlayerCooldown b = (PlayerCooldown) obj;
        return uuid.equals(b.uuid) && cooldown.equals(b.cooldown);
    }
}