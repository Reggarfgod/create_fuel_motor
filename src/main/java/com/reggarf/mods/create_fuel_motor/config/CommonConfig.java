package com.reggarf.mods.create_fuel_motor.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CommonConfig {

	public static final String catagory_general = "general";
	public static final String catagory_fuel_motor = "fuel_motor";
	public static ForgeConfigSpec.IntValue fuel_motor_rpm_range;
	public static ForgeConfigSpec.BooleanValue audio_Enabled;
	public static ForgeConfigSpec.DoubleValue fuel_motor_pickup_range;

	private static final ForgeConfigSpec.Builder builder= new ForgeConfigSpec.Builder();

	public static ForgeConfigSpec COMMON_CONFIG;

	static {
			builder.comment("Make sure config changes are duplicated on both Clients and the Server when running a dedicated Server,")
					.comment(" as the config isnt synced between Clients and Server.");

			builder.comment("General Settings").push(catagory_general);
			audio_Enabled = builder.comment("If audio should be enabled or not.")
					.define("audio_enabled", true);
			builder.pop();

			builder.comment("Fuel Motor").push(catagory_fuel_motor);
			fuel_motor_rpm_range = builder.comment("Fuel Motor min/max RPM.")
					.defineInRange("motor_rpm_range", 256, 1, Integer.MAX_VALUE);

			fuel_motor_pickup_range = builder.comment("Pickup range (in blocks) for fuel items around the motor.")
					.defineInRange("fuel_pickup_range", 3, 0.0, 16.0);

			builder.pop();

			COMMON_CONFIG = builder.build();
		}

}
