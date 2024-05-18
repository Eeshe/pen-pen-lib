package me.eeshe.penpenlib.models.config;

import me.eeshe.penpenlib.files.config.ConfigWrapper;
import me.eeshe.penpenlib.util.ConfigUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class PenParticle extends Configurable {
    private String path;
    private boolean defaultEnabled;
    private org.bukkit.Particle defaultParticle;
    private int defaultAmount;
    private double defaultXOffSet;
    private double defaultYOffSet;
    private double defaultZOffSet;
    private double defaultExtra;
    private Object defaultData;

    public PenParticle(String path, boolean defaultEnabled, org.bukkit.Particle defaultParticle, int defaultAmount,
                       double defaultXOffSet, double defaultYOffSet, double defaultZOffSet, double defaultExtra, Object defaultData) {
        this.path = path;
        this.defaultEnabled = defaultEnabled;
        this.defaultParticle = defaultParticle;
        this.defaultAmount = defaultAmount;
        this.defaultXOffSet = defaultXOffSet;
        this.defaultYOffSet = defaultYOffSet;
        this.defaultZOffSet = defaultZOffSet;
        this.defaultExtra = defaultExtra;
        this.defaultData = defaultData;

        getParticles().add(this);
    }

    /**
     * Used solely with the purpose of calling the register() function.
     */
    public PenParticle() {

    }

    /**
     * Registers the PenParticle module.
     */
    public void register() {
        writeDefaults();
    }

    public void writeDefaults() {
        FileConfiguration config = getConfig();
        for (PenParticle libParticle : getParticles()) {
            String path = libParticle.getPath();
            config.addDefault(path + ".particle", libParticle.getDefaultParticle().name());
            config.addDefault(path + ".enable", libParticle.getDefaultEnabled());
            config.addDefault(path + ".amount", libParticle.getDefaultAmount());
            config.addDefault(path + ".x-off-set", libParticle.getDefaultXOffSet());
            config.addDefault(path + ".y-off-set", libParticle.getDefaultYOffSet());
            config.addDefault(path + ".z-off-set", libParticle.getDefaultZOffSet());
            config.addDefault(path + ".extra", libParticle.getDefaultExtra());
            if (libParticle.getDefaultData() != null) {
                Object defaultData = libParticle.getDefaultData();
                if (defaultData instanceof org.bukkit.Particle.DustOptions dustOptions) {
                    config.addDefault(path + ".data.color", dustOptions.getColor().asRGB());
                    config.addDefault(path + ".data.size", dustOptions.getSize());
                } else {
                    config.addDefault(path + ".data", defaultData.toString());
                }
            }
        }
        config.options().copyDefaults(true);

        ConfigWrapper configWrapper = getConfigWrapper();
        configWrapper.saveConfig();
        configWrapper.reloadConfig();
    }

    public void spawn(Player player, Location location) {
        fetchConfigParticle().spawn(player, location);
    }

    public void spawn(Location location) {
        fetchConfigParticle().spawn(location);
    }

    private ConfigParticle fetchConfigParticle() {
        ConfigParticle configParticle = ConfigUtil.fetchConfigParticle(getConfig(), path);
        if (configParticle == null) return createDefaultConfigParticle();

        return configParticle;
    }

    private ConfigParticle createDefaultConfigParticle() {
        return new ConfigParticle(defaultEnabled, defaultParticle, defaultAmount, defaultXOffSet, defaultYOffSet, defaultZOffSet, defaultExtra, defaultData);
    }

    public String getPath() {
        return path;
    }

    public org.bukkit.Particle getDefaultParticle() {
        return defaultParticle;
    }

    public boolean getDefaultEnabled() {
        return defaultEnabled;
    }

    public int getDefaultAmount() {
        return defaultAmount;
    }

    public double getDefaultXOffSet() {
        return defaultXOffSet;
    }

    public double getDefaultYOffSet() {
        return defaultYOffSet;
    }

    public double getDefaultZOffSet() {
        return defaultZOffSet;
    }

    public double getDefaultExtra() {
        return defaultExtra;
    }

    public Object getDefaultData() {
        return defaultData;
    }

    public abstract List<PenParticle> getParticles();
}
