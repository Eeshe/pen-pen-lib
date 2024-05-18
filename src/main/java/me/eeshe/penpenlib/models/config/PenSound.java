package me.eeshe.penpenlib.models.config;

import me.eeshe.penpenlib.files.config.ConfigWrapper;
import me.eeshe.penpenlib.util.ConfigUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class PenSound extends Configurable {
    private String path;
    private boolean defaultEnabled;
    private Sound defaultSound;
    private float defaultVolume;
    private float defaultPitch;

    public PenSound(String path, boolean defaultEnabled, Sound defaultSound, float defaultVolume, float defaultPitch) {
        this.path = path;
        this.defaultEnabled = defaultEnabled;
        this.defaultSound = defaultSound;
        this.defaultVolume = defaultVolume;
        this.defaultPitch = defaultPitch;

        getSounds().add(this);
    }

    /**
     * Used solely with the purpose of calling the register() function.
     */
    public PenSound() {

    }

    /**
     * Registers the PenSound module.
     */
    public void register() {
        writeDefaults();
    }

    public void writeDefaults() {
        FileConfiguration config = getConfig();
        for (PenSound libSound : getSounds()) {
            String path = libSound.getPath();
            config.addDefault(path + ".sound", libSound.getDefaultSound().name());
            config.addDefault(path + ".enable", libSound.getDefaultEnabled());
            config.addDefault(path + ".volume", libSound.getDefaultVolume());
            config.addDefault(path + ".pitch", libSound.getDefaultPitch());
        }
        config.options().copyDefaults(true);

        ConfigWrapper configWrapper = getConfigWrapper();
        configWrapper.saveConfig();
        configWrapper.reloadConfig();
    }

    public void play(CommandSender sender) {
        if (!(sender instanceof Player player)) return;

        fetchConfigSound().play(player);
    }

    public void play(Location location) {
        ConfigSound configSound = fetchConfigSound();
        if (location.getWorld() == null) return;

        configSound.play(location);
    }

    private ConfigSound fetchConfigSound() {
        ConfigSound configParticle = ConfigUtil.fetchConfigSound(getConfig(), path);
        if (configParticle == null) return createDefaultConfigSound();

        return configParticle;
    }

    private ConfigSound createDefaultConfigSound() {
        return new ConfigSound(defaultSound, defaultEnabled, defaultVolume, defaultPitch);
    }

    public String getPath() {
        return path;
    }

    public boolean getDefaultEnabled() {
        return defaultEnabled;
    }

    public Sound getDefaultSound() {
        return defaultSound;
    }

    public float getDefaultVolume() {
        return defaultVolume;
    }

    public float getDefaultPitch() {
        return defaultPitch;
    }

    public abstract List<PenSound> getSounds();
}