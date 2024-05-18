package me.eeshe.penpenlib.models.config;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public record ConfigSound(org.bukkit.Sound sound, boolean enabled, float volume, float pitch) {

    public void play(Player player) {
        player.playSound(player, sound, volume, pitch);
    }

    public void play(Location location) {
        location.getWorld().playSound(location, sound, volume, pitch);
    }
}
