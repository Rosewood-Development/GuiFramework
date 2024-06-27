package dev.rosewood.guiframework.framework.util;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;

@SuppressWarnings("deprecation")
public final class VersionUtils {

    private static final int VERSION_NUMBER;
    private static final int MINOR_VERSION_NUMBER;
    static {
        String bukkitVersion = Bukkit.getBukkitVersion();
        String[] parts = bukkitVersion.split("-")[0].split("\\.");
        VERSION_NUMBER = Integer.parseInt(parts[1]);
        MINOR_VERSION_NUMBER = parts.length >= 3 ? Integer.parseInt(parts[2]) : 0;
    }

    public static final Enchantment INFINITY;
    static {
        if (VERSION_NUMBER > 20 || (VERSION_NUMBER == 20 && MINOR_VERSION_NUMBER >= 5)) {
            INFINITY = Registry.ENCHANTMENT.get(NamespacedKey.minecraft("infinity"));
        } else {
            INFINITY = findEnchantmentLegacy("infinity", "arrow_infinite");
        }
    }

    private static Enchantment findEnchantmentLegacy(String... names) {
        for (String name : names) {
            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.fromString(name));
            if (enchantment != null)
                return enchantment;
        }
        return null;
    }

}
