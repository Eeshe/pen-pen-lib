package me.eeshe.penpenlib.models.config;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class ConfigExplosion {
    private final float power;
    private final boolean setFire;
    private final boolean breakBlocks;

    public ConfigExplosion(float power, boolean setFire, boolean breakBlocks) {
        this.power = power;
        this.setFire = setFire;
        this.breakBlocks = breakBlocks;
    }

    public void create(Location location) {
        location.getWorld().createExplosion(location, power, setFire, breakBlocks);
    }

    public void create(Location location, Entity entity) {
        location.getWorld().createExplosion(location, power, setFire, breakBlocks, entity);
    }

    public float getPower() {
        return power;
    }

    public boolean shouldSetFire() {
        return setFire;
    }

    public boolean shouldBreakBlocks() {
        return breakBlocks;
    }
}
