package com.codeka.gtravel.net;

import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ServerProxy extends CommonProxy {
    @Override
    public boolean isClient() {
        return false;
    }
}
