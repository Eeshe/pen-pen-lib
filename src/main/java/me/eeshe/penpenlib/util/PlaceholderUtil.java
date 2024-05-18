package me.eeshe.penpenlib.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PlaceholderUtil {

    /**
     * Formats the placeholder for the passed text using the passed player.
     *
     * @param player Player to format the message with.
     * @param text   Text to format.
     * @return Formatted message.
     */
    public static String formatPlaceholders(OfflinePlayer player, String text) {
        boolean isPapiInstalled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        text = text.replace("%player%", player.getName());
        if (isPapiInstalled) {
            PlaceholderAPI.setPlaceholders(player, text);
        }
        return text;
    }
}
