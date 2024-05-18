package me.eeshe.penpenlib.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.eeshe.penpenlib.models.config.*;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ConfigUtil {

    public static void writeConfigItemStack(FileConfiguration config, String path, ItemStack item) {
        if (item == null) return;
        if (config.contains(path)) return;

        config.set(path + ".material", item.getType().name());
        config.set(path + ".amount", item.getAmount());

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (meta.hasDisplayName()) {
            config.set(path + ".name", meta.getDisplayName());
        }
        List<String> lore = meta.getLore();
        if (meta.hasEnchants()) {
            String enchantmentsPath = path + ".enchantments";
            for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                config.set(enchantmentsPath + "." + entry.getKey().getName(), entry.getValue());
            }
        }
        if (lore != null) {
            config.set(path + ".lore", lore);
        }
        if (meta.hasCustomModelData()) {
            config.set(path + ".custom-model-data", meta.getCustomModelData());
        }
        if (!meta.getItemFlags().isEmpty()) {
            config.set(path + ".item-flags", meta.getItemFlags().stream().map(ItemFlag::name).toList());
        }
    }

    public static ItemStack fetchConfigItemStack(FileConfiguration config, String path) {
        ConfigurationSection itemSection = config.getConfigurationSection(path);
        if (itemSection == null) return null;

        String materialName = itemSection.getString("material", "");
        Material material = Material.matchMaterial(materialName);
        if (material == null) {
            LogUtil.sendWarnLog("Unknown material '" + materialName + "' for item in '" + path + "'.");
            return null;
        }
        int amount = itemSection.getInt("amount", 1);
        String displayName = itemSection.getString("name");
        List<String> lore = itemSection.getStringList("lore");
        int customModelData = itemSection.getInt("custom-model-data", -1);
        Map<Enchantment, Integer> enchantments = fetchEnchantments(config, path + ".enchantments");
        Multimap<Attribute, AttributeModifier> attributes = fetchAttributes(config, path + ".attributes");
        ItemFlag[] itemFlags = fetchItemFlags(config, path + ".item-flags");

        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        if (displayName != null) {
            meta.setDisplayName(StringUtil.formatColor(displayName));
        }
        if (customModelData != -1) {
            meta.setCustomModelData(customModelData);
        }
        if (!enchantments.isEmpty()) {
            if (!Arrays.asList(itemFlags).contains(ItemFlag.HIDE_ENCHANTS)) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                    lore.add(0, StringUtil.formatColor("&7" + StringUtil
                            .getEnchantmentName(entry.getKey().getKey()) + " " + StringUtil.parseToRoman(entry.getValue())));
                }
            }
        }
        meta.setLore(lore);
        meta.setAttributeModifiers(attributes);
        meta.addItemFlags(itemFlags);
        item.setItemMeta(meta);

        item.addUnsafeEnchantments(enchantments);

        return item;
    }

    private static Map<Enchantment, Integer> fetchEnchantments(FileConfiguration config, String path) {
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        ConfigurationSection enchantmentSection = config.getConfigurationSection(path);
        if (enchantmentSection == null) return enchantments;
        for (String enchantmentName : enchantmentSection.getKeys(false)) {
            Enchantment enchantment = Enchantment.getByName(enchantmentName);
            if (enchantment == null) {
                LogUtil.sendWarnLog("Unknown enchantment '" + enchantmentName + "' configured for '" + path + "'.");
                continue;
            }
            enchantments.put(enchantment, enchantmentSection.getInt(enchantmentName));
        }
        return enchantments;
    }

    private static Multimap<Attribute, AttributeModifier> fetchAttributes(FileConfiguration config, String path) {
        Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();
        ConfigurationSection attributeSection = config.getConfigurationSection(path);
        if (attributeSection == null) return attributes;
        for (String attributeName : attributeSection.getKeys(false)) {
            Attribute attribute;
            try {
                attribute = Attribute.valueOf(attributeName);
            } catch (Exception e) {
                LogUtil.sendWarnLog("Unknown attribute '" + attributeName + "' configured for '" + path + "'.");
                continue;
            }
            String operationName = attributeSection.getString(attributeName + ".operation");
            AttributeModifier.Operation operation;
            try {
                operation = AttributeModifier.Operation.valueOf(operationName);
            } catch (Exception e) {
                LogUtil.sendWarnLog("Unknown operation '" + operationName + "' configured for '" + path + "'.");
                continue;
            }
            String slotName = attributeSection.getString(attributeName + ".slot");
            EquipmentSlot slot;
            try {
                slot = EquipmentSlot.valueOf(slotName);
            } catch (Exception e) {
                LogUtil.sendWarnLog("Unknown slot '" + slotName + "' configured for '" + path + "'.");
                continue;
            }
            double amount = attributeSection.getDouble(attributeName + ".amount");

            attributes.put(attribute, new AttributeModifier(UUID.randomUUID(), attribute.name(), amount, operation, slot));
        }
        return attributes;
    }

    private static ItemFlag[] fetchItemFlags(FileConfiguration config, String path) {
        List<ItemFlag> itemFlags = new ArrayList<>();
        for (String flagName : config.getStringList(path)) {
            ItemFlag itemFlag;
            try {
                itemFlag = ItemFlag.valueOf(flagName);
            } catch (Exception e) {
                LogUtil.sendWarnLog("Unknown item flag '" + flagName + "' configured in '" + path + "'.");
                continue;
            }
            itemFlags.add(itemFlag);
        }
        return itemFlags.toArray(new ItemFlag[0]);
    }

    /**
     * Fetches the configured ConfigSound.
     *
     * @return Configured ConfigSound.
     */
    public static ConfigSound fetchConfigSound(FileConfiguration config, String path) {
        ConfigurationSection soundSection = config.getConfigurationSection(path);
        if (soundSection == null) return null;

        String soundName = soundSection.getString("sound");
        org.bukkit.Sound sound;
        try {
            sound = org.bukkit.Sound.valueOf(soundName);
        } catch (Exception e) {
            LogUtil.sendWarnLog("Unknown sound '" + soundName + "' configured for '" + path + "'.");
            return null;
        }
        boolean enabled = config.getBoolean(path + ".enable");
        float volume = (float) config.getDouble(path + ".volume");
        float pitch = (float) config.getDouble(path + ".pitch");

        return new ConfigSound(sound, enabled, volume, pitch);
    }

    /**
     * Fetches the configured ConfigParticle.
     *
     * @param config Configuration file.
     * @param path   Path of the particle.
     * @return Configured ConfigParticle.
     */
    public static ConfigParticle fetchConfigParticle(FileConfiguration config, String path) {
        ConfigurationSection particleSection = config.getConfigurationSection(path);
        if (particleSection == null) return null;

        String particleName = particleSection.getString("particle");
        Particle particle;
        try {
            particle = Particle.valueOf(particleName);
        } catch (Exception e) {
            LogUtil.sendWarnLog("Unknown particle '" + particleName + "' configured for '" + path + "'.");
            return null;
        }
        boolean enabled = particleSection.getBoolean("enable");
        int amount = particleSection.getInt("amount");
        double xOffSet = particleSection.getDouble("x-off-set");
        double yOffSet = particleSection.getDouble("y-off-set");
        double zOffSet = particleSection.getDouble("z-off-set");
        double extra = particleSection.getDouble("extra");
        String dataString = particleSection.getString("data");
        Object data = null;
        if (dataString != null) {
            if (particle.getDataType().equals(BlockData.class)) {
                Material material = Material.matchMaterial(dataString);
                if (material == null) {
                    LogUtil.sendWarnLog("Unknown block configured for block particle '" + dataString + "' in '" + path + "'.");
                } else {
                    data = material.createBlockData();
                }
            } else if (particle.getDataType().equals(Particle.DustOptions.class)) {
                Color color = Color.fromRGB(particleSection.getInt("data.color"));
                float size = (float) particleSection.getDouble("data.size");
                data = new Particle.DustOptions(color, size);
            } else if (particle.getDataType().equals(Float.class)) {
                data = (float) particleSection.getDouble("data");
            }
        }
        return new ConfigParticle(enabled, particle, amount, xOffSet, yOffSet, zOffSet, extra, data);
    }

    /**
     * Fetches the configured ConfigTitle.
     *
     * @param config Config the title will be fetched from.
     * @param path   Configuration path to search.
     * @return Configured ConfigTitle.
     */
    public static ConfigTitle fetchConfigTitle(FileConfiguration config, String path) {
        ConfigurationSection titleSection = config.getConfigurationSection(path);
        if (titleSection == null) return null;

        String title = titleSection.getString("title");
        String subtitle = titleSection.getString("subtitle");
        int fadeInTicks = titleSection.getInt("fade-in");
        int durationTicks = titleSection.getInt("duration");
        int fadeOutTicks = titleSection.getInt("fade-out");

        return new ConfigTitle(title, subtitle, fadeInTicks, durationTicks, fadeOutTicks);
    }

    /**
     * Fetches the configured material in the passed path within the passed config file.
     *
     * @param config Configuration file the material will be fetched from.
     * @param path   Configuration path the material will be fetched from.
     * @return Configured material in the passed path within the passed config file.
     */
    public static Material fetchMaterial(FileConfiguration config, String path) {
        String generatedItemName = config.getString(path, "");
        Material generatedItem = Material.matchMaterial(generatedItemName);
        if (generatedItem == null) {
            LogUtil.sendWarnLog("Unknown item '" + generatedItemName + "' configured in '" + path + "'.");
            return null;
        }
        return generatedItem;
    }

    public static void writeIntRange(IntRange intRange, FileConfiguration config, String path) {
        config.set(path + ".min", intRange.getMin());
        config.set(path + ".max", intRange.getMax());
    }

    public static IntRange fetchIntRange(FileConfiguration config, String path) {
        ConfigurationSection rangeSection = config.getConfigurationSection(path);
        if (rangeSection == null) return new IntRange(0, 0);

        int max = rangeSection.getInt("max");
        int min = rangeSection.getInt("min");

        return new IntRange(max, min);
    }

    public static void writeConfigExplosion(ConfigExplosion configExplosion, FileConfiguration config, String path) {
        config.set(path + ".power", configExplosion.getPower());
        config.set(path + ".set-fire", configExplosion.shouldSetFire());
        config.set(path + ".break-blocks", configExplosion.shouldBreakBlocks());
    }

    public static ConfigExplosion fetchConfigExplosion(FileConfiguration config, String path) {
        ConfigurationSection explosionSection = config.getConfigurationSection(path);
        if (explosionSection == null) return null;

        float power = (float) explosionSection.getDouble("power");
        boolean shouldSetFire = explosionSection.getBoolean("set-fire");
        boolean shouldBreakBlocks = explosionSection.getBoolean("break-blocks");

        return new ConfigExplosion(power, shouldSetFire, shouldBreakBlocks);
    }

    /**
     * Writes the passed additional configs to the passed path.
     *
     * @param path              Path the additional configs will be written to.
     * @param additionalConfigs Additional configs that will be written.
     */
    public static void writeAdditionalConfigs(FileConfiguration config, String path, Map<?, Object> additionalConfigs) {
        for (Map.Entry<?, Object> entry : additionalConfigs.entrySet()) {
            String newPath = path + "." + entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map<?, ?>) {
                Map<?, ?> map = (Map<?, ?>) value;
                writeAdditionalConfigs(config, newPath, (Map<?, Object>) map);
            } else if (value instanceof IntRange intRange) {
                config.addDefault(newPath + ".min", intRange.getMin());
                config.addDefault(newPath + ".max", intRange.getMax());
            } else {
                config.addDefault(newPath, entry.getValue());
            }
        }
    }
}
