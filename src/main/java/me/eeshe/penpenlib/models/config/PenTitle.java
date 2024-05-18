package me.eeshe.penpenlib.models.config;

import me.eeshe.penpenlib.files.config.ConfigWrapper;
import me.eeshe.penpenlib.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PenTitle extends Configurable {
    private String path;
    private String defaultTitle;
    private String defaultSubtitle;
    private int defaultFadeInTicks;
    private int defaultDurationTicks;
    private int defaultFadeOutTicks;

    public PenTitle(String path, String defaultTitle, String defaultSubtitle, int defaultFadeInTicks,
                    int defaultDurationTicks, int defaultFadeOutTicks) {
        this.path = path;
        this.defaultTitle = defaultTitle;
        this.defaultSubtitle = defaultSubtitle;
        this.defaultFadeInTicks = defaultFadeInTicks;
        this.defaultDurationTicks = defaultDurationTicks;
        this.defaultFadeOutTicks = defaultFadeOutTicks;

        getTitles().add(this);
    }

    /**
     * Used solely with the purpose of calling the register() function.
     */
    public PenTitle() {

    }

    /**
     * Registers the PenTitle module.
     */
    public void register() {
        writeDefaults();
    }

    public void writeDefaults() {
        FileConfiguration config = getConfig();
        for (PenTitle libTitle : getTitles()) {
            String path = libTitle.getPath();
            config.addDefault(path + ".title", libTitle.getDefaultTitle());
            config.addDefault(path + ".subtitle", libTitle.getDefaultSubtitle());
            config.addDefault(path + ".fade-in", libTitle.getDefaultFadeInTicks());
            config.addDefault(path + ".duration", libTitle.getDefaultDurationTicks());
            config.addDefault(path + ".fade-out", libTitle.getDefaultFadeOutTicks());
        }
        config.options().copyDefaults(true);

        ConfigWrapper configWrapper = getConfigWrapper();
        configWrapper.saveConfig();
        configWrapper.reloadConfig();
    }

    public void sendGlobal() {
        sendGlobal(null, new HashMap<>());
    }

    public void sendGlobal(Map<String, String> placeholders) {
        sendGlobal(null, placeholders);
    }

    public void sendGlobal(PenSound sound, Map<String, String> placeholders) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (placeholders == null) {
                send(online);
            } else {
                send(online, placeholders);
            }
            if (sound == null) continue;

            sound.play(online);
        }
    }

    public void send(Player player, PenSound sound) {
        send(player, sound, new HashMap<>());
    }

    public void send(Player player, PenSound sound, Map<String, String> placeholders) {
        fetchConfigTitle().send(player, placeholders);
        sound.play(player);
    }

    public void send(Player player) {
        send(player, new HashMap<>());
    }

    public void send(Player player, Map<String, String> placeholders) {
        fetchConfigTitle().send(player, placeholders);
    }

    private ConfigTitle fetchConfigTitle() {
        ConfigTitle configTitle = ConfigUtil.fetchConfigTitle(getConfig(), path);
        if (configTitle == null) return createDefaultConfigTitle();

        return configTitle;
    }

    private ConfigTitle createDefaultConfigTitle() {
        return new ConfigTitle(defaultTitle, defaultSubtitle, defaultFadeInTicks, defaultDurationTicks, defaultFadeOutTicks);
    }

    public String getPath() {
        return path;
    }

    public String getDefaultTitle() {
        return defaultTitle;
    }

    public String getDefaultSubtitle() {
        return defaultSubtitle;
    }

    public long getDefaultFadeInTicks() {
        return defaultFadeInTicks;
    }

    public long getDefaultDurationTicks() {
        return defaultDurationTicks;
    }

    public long getDefaultFadeOutTicks() {
        return defaultFadeOutTicks;
    }

    public abstract List<PenTitle> getTitles();
}
