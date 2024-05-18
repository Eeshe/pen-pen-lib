package me.eeshe.penpenlib.util;

import me.eeshe.penpenlib.models.config.CommonSound;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class TeleportUtil {

    /**
     * Teleports the livingEntity with a different method depending on the server software.
     *
     * @param livingEntity LivingEntity to teleport.
     * @param location     Location the livingEntity will be teleported to.
     */
    public static void teleport(LivingEntity livingEntity, Location location) {
        teleport(livingEntity, location, false);
    }

    /**
     * Teleports the livingEntity with a different method depending on the server software.
     *
     * @param livingEntity LivingEntity to teleport.
     * @param location     Location the livingEntity will be teleported to.
     * @param playSound    Whether the default teleport sound should be played.
     */
    public static void teleport(LivingEntity livingEntity, Location location, boolean playSound) {
        if (FoliaUtil.isFolia()) {
            livingEntity.teleportAsync(location);
        } else {
            livingEntity.teleport(location);
        }
        if (!playSound) return;

        CommonSound.TELEPORT_REQUEST.play(livingEntity);
    }
}
