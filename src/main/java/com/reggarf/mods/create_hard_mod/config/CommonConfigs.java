package com.reggarf.mods.create_hard_mod.config;



import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfigs {

    public static final ForgeConfigSpec COMMON_CONFIG;

    public static final ForgeConfigSpec.IntValue COAL_MOTOR_SPEED;
    public static final ForgeConfigSpec.IntValue COAL_MOTOR_STRESS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("Coal Motor Settings");

        COAL_MOTOR_SPEED = builder
                .comment("The speed the Coal Motor generates when burning fuel")
                .defineInRange("coalMotorSpeed", 16, -256, 256);

        COAL_MOTOR_STRESS = builder
                .comment("The amount of stress units the Coal Motor can generate")
                .defineInRange("coalMotorStress", 35000, 0, 60024);

        builder.pop();

        COMMON_CONFIG = builder.build();
    }
}
