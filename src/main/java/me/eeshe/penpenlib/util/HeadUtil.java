package me.eeshe.penpenlib.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class HeadUtil {

    /**
     * Adds the passed head texture to the passed item.
     *
     * @param item    Item to add the texture to.
     * @param texture Head texture to add.
     * @return Item with the passed texture.
     */
    public static ItemStack addHeadTexture(ItemStack item, String texture) {
        if (item == null) return null;
        if (!(item.getItemMeta() instanceof SkullMeta skullMeta)) return item;

        PlayerProfile playerProfile = Bukkit.createProfile(UUID.randomUUID());
        playerProfile.setProperty(new ProfileProperty("textures", texture));
        skullMeta.setPlayerProfile(playerProfile);
        item.setItemMeta(skullMeta);

        return item;
    }

    /**
     * Adds the passed player skin to the passed item.
     *
     * @param item Item to add the skin to.
     * @param playerName Player name of the skin.
     * @return Item with the passed skin.
     */
    public static ItemStack addHeadPlayerSkin(ItemStack item, String playerName) {
        if (item == null) return null;
        if (playerName == null) return item;
        if (!(item.getItemMeta() instanceof SkullMeta skullMeta)) return item;

        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(playerName));
        item.setItemMeta(skullMeta);

        return item;
    }
}
