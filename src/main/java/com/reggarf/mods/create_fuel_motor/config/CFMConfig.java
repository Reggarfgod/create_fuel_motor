package com.reggarf.mods.create_fuel_motor.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;



public class CFMConfig {
    private static final CFMConfig INSTANCE = new CFMConfig();
    private final CommonConfig common;
    public CFMConfig() {
        var common = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        this.common = common.getLeft();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, common.getRight());
    }

    public static CommonConfig getCommon() {
        return INSTANCE.common;
    }
}
