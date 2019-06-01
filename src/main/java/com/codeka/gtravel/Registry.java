package com.codeka.gtravel;

import com.codeka.gtravel.enchantment.GlobalTravelerEnchantment;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder("gtravel")
public class Registry {
    public static final GlobalTravelerEnchantment global_traveler = null;

    /**
     * Called during pre-init to initialize configs.
     * @param config A {@link Configuration} representing our config file.
     */
    public static void init(Configuration config) {
        config.setCategoryComment("globalTraveler", "Properties for the Global Traveler enchantment");
        GlobalTravelerEnchantment.init(config);
    }
}
