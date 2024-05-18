package me.eeshe.penpenlib.models.config;

import me.eeshe.penpenlib.PenPenLib;
import me.eeshe.penpenlib.files.config.ConfigWrapper;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.List;

public class CommonSound extends PenSound {
    private static final List<PenSound> SOUNDS = new ArrayList<>();
    private static final ConfigWrapper CONFIG_WRAPPER = new ConfigWrapper(PenPenLib.getInstance(), null, "sounds.yml");

    public static final CommonSound SUCCESS = new CommonSound("success", true, Sound.BLOCK_NOTE_BLOCK_PLING, 0.6F, 1.6F);
    public static final CommonSound ERROR = new CommonSound("error", true, Sound.BLOCK_NOTE_BLOCK_BASS, 0.6F, 0.6F);
    public static final CommonSound INPUT_REQUEST = new CommonSound("input-request", true, Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 0.6F, 1.2F);
    public static final CommonSound TELEPORT_REQUEST = new CommonSound("teleport", true, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 0.6F, 1.3F);
    public static final CommonSound BACK = new CommonSound("back", true, org.bukkit.Sound.UI_BUTTON_CLICK, 0.6F, 1.1F);
    public static final CommonSound PREVIOUS_PAGE = new CommonSound("previous-page", true, org.bukkit.Sound.ITEM_BOOK_PAGE_TURN, 0.6F, 1.1F);
    public static final CommonSound NEXT_PAGE = new CommonSound("next-page", true, org.bukkit.Sound.ITEM_BOOK_PAGE_TURN, 0.6F, 1.1F);

    public CommonSound(String path, boolean defaultEnabled, Sound defaultSound, float defaultVolume, float defaultPitch) {
        super(path, defaultEnabled, defaultSound, defaultVolume, defaultPitch);
    }

    public CommonSound() {

    }

    @Override
    public List<PenSound> getSounds() {
        return SOUNDS;
    }

    @Override
    public ConfigWrapper getConfigWrapper() {
        return CONFIG_WRAPPER;
    }
}
