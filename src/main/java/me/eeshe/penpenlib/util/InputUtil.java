package me.eeshe.penpenlib.util;

import me.eeshe.penpenlib.PenPenLib;
import me.eeshe.penpenlib.models.Scheduler;
import me.eeshe.penpenlib.models.config.CommonSound;
import me.eeshe.penpenlib.models.config.PenMessage;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class InputUtil {

    /**
     * Asks the player for confirmation and adds their UUID to the set if not already present.
     *
     * @param player  the player who is being asked for confirmation
     * @param message the message to send to the player
     * @param set     the set to add the player's UUID to if not already present
     * @return true if the player was asked for confirmation, false otherwise
     */
    public static boolean askPlayerConfirmation(Player player, PenMessage message, Set<UUID> set) {
        UUID playerUuid = player.getUniqueId();
        if (set.contains(playerUuid)) return false;

        set.add(playerUuid);
        Scheduler.runLater(PenPenLib.getInstance(), () -> set.remove(playerUuid), 100L);

        message.send(player, CommonSound.INPUT_REQUEST);
        return true;
    }

    /**
     * Attempts to cancel the input.
     *
     * @param player        the player that is making the input
     * @param input         the input to be cancelled
     * @param cancelMessage the message to send to the player when the input is cancelled
     * @param map           a map containing UUIDs and other objects
     * @param runnable      a runnable to execute when the input is cancelled
     * @return true if the input was cancelled, false otherwise
     */
    public static boolean attemptInputCancel(Player player, String input, PenMessage cancelMessage,
                                             Map<UUID, ?> map, Runnable runnable) {
        return attemptInputCancel(player, input, cancelMessage, map.keySet(), runnable);
    }

    /**
     * Attempts to cancel the input.
     *
     * @param player         Player that is making the input.
     * @param input          Input.
     * @param uuidCollection Collection to remove the player from.
     * @param runnable       Runnable to execute.
     * @return True if the input was cancelled.
     */
    public static boolean attemptInputCancel(Player player, String input, PenMessage cancelMessage,
                                             Collection<UUID> uuidCollection, Runnable runnable) {
        if (!input.equalsIgnoreCase("cancel")) return false;
        if (!uuidCollection.contains(player.getUniqueId())) return false;

        if (runnable != null) {
            runnable.run();
        }
        uuidCollection.remove(player.getUniqueId());
        cancelMessage.sendError(player);
        return true;
    }
}
