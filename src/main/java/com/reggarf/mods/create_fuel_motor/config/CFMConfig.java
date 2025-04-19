package com.reggarf.mods.create_fuel_motor.config;


import net.createmod.catnip.config.ConfigBase;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class CFMConfig {
    private static final CFMConfig INSTANCE = new CFMConfig();
    private final CommonConfig common;
    public CFMConfig() {
        var common = new ModConfigSpec.Builder().configure(CommonConfig::new);
        this.common = common.getLeft();
        //ModLoadingContext.registerConfig(ModConfig.Type.COMMON, common.getRight());
    }

    public static CommonConfig getCommon() {
        return INSTANCE.common;
    }
}
