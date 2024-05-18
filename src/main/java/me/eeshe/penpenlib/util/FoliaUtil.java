package me.eeshe.penpenlib.util;

import org.bukkit.Bukkit;

public class FoliaUtil {
    private static final boolean IS_FOLIA = Bukkit.getVersion().contains("Folia");

    public static boolean isFolia() {
        return IS_FOLIA;
    }
}
