package me.eeshe.penpenlib.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SerializationUtil {

    /**
     * Serializes an ItemStack to a Base64 encoded string.
     *
     * @param item The ItemStack to serialize
     * @return A Base64 encoded string representation of the ItemStack
     * @throws IllegalStateException if serialization fails
     */
    public static String serializeItemStack(ItemStack item) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeObject(item);
            dataOutput.close();

            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to serialize ItemStack", e);
        }
    }

    /**
     * Deserializes an ItemStack from a Base64 encoded string.
     *
     * @param data The Base64 encoded string to deserialize
     * @return The deserialized ItemStack
     */
    public static ItemStack deserializeItemStack(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();

            return item;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
