package com.codeka.gtravel;

import com.codeka.gtravel.net.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = GTravelMod.MODID,
        name = GTravelMod.MODNAME,
        version = GTravelMod.MODVERSION,
        dependencies = "required-after:forge@[14.23.5.2796,)",
        useMetadata = true)
public class GTravelMod {
    public static final String MODID = "gtravel";
    public static final String MODNAME = "Global Traveler Enchantment";
    public static final String MODVERSION= "0.1.0";

    @SidedProxy(
            clientSide = "com.codeka.gtravel.net.ClientProxy",
            serverSide = "com.codeka.gtravel.net.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static GTravelMod instance;

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(event, logger);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }
}
