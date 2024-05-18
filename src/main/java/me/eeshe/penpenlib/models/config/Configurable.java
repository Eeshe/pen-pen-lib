package me.eeshe.penpenlib.models.config;

import me.eeshe.penpenlib.files.config.ConfigWrapper;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class Configurable {

    public abstract ConfigWrapper getConfigWrapper();

    public FileConfiguration getConfig() {
        ConfigWrapper configWrapper = getConfigWrapper();
        if (configWrapper == null) return null;

        return configWrapper.getConfig();
    }
}
