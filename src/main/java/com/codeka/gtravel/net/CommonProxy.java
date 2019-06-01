package com.codeka.gtravel.net;

import com.codeka.gtravel.Registry;
import com.codeka.gtravel.enchantment.GlobalTravelerEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;

public abstract class CommonProxy {
    public abstract boolean isClient();

    protected Logger logger;
    private Configuration config;

    protected void onPreInit(FMLPreInitializationEvent e) {}
    protected void onInit(FMLInitializationEvent e) {}
    protected void onPostInit(FMLPostInitializationEvent e) {}

    public void preInit(FMLPreInitializationEvent e, Logger logger) {
        this.logger = logger;
        logger.info("preInit");

        File configFile = new File(e.getModConfigurationDirectory(), "gtravel.cfg");
        config = new Configuration(configFile);
        config.load();
        Registry.init(config);
        config.save();

        onPreInit(e);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public void init(FMLInitializationEvent e) {
        onInit(e);
    }

    public void postInit(FMLPostInitializationEvent e) {
        onPostInit(e);
    }

    @SubscribeEvent
    public void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        logger.info("registering...");
        event.getRegistry().register(new GlobalTravelerEnchantment());
    }
}
