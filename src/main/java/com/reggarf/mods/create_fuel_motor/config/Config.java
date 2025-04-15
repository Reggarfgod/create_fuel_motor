package com.reggarf.mods.create_fuel_motor.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class Config {

	public static final String CATAGORY_GENERAL = "general";
	public static final String CATAGORY_FUEL_MOTOR = "fuel_motor";

	private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
	public static ForgeConfigSpec COMMON_CONFIG;
	public static ForgeConfigSpec.IntValue FUEL_MOTOR_RPM_RANGE;
	public static ForgeConfigSpec.IntValue FE_RPM;
	public static ForgeConfigSpec.BooleanValue AUDIO_ENABLED;



	static {
		COMMON_BUILDER.comment("Make sure config changes are duplicated on both Clients and the Server when running a dedicated Server,")
					.comment(" as the config isnt synced between Clients and Server.");
		COMMON_BUILDER.comment("General Settings").push(CATAGORY_GENERAL);
		FE_RPM = COMMON_BUILDER.comment("Forge Energy conversion rate (in FE/t at 256 RPM, value is the FE/t generated and consumed is at 256rpm).")
				.defineInRange("fe_at_max_rpm", 480, 0, Integer.MAX_VALUE);

		AUDIO_ENABLED = COMMON_BUILDER.comment("If audio should be enabled or not.")
				.define("audio_enabled", true);
		COMMON_BUILDER.pop();


		COMMON_BUILDER.comment("Fuel Motor").push(CATAGORY_FUEL_MOTOR);
		FUEL_MOTOR_RPM_RANGE = COMMON_BUILDER.comment("Fuel Motor min/max RPM.")
				.defineInRange("motor_rpm_range", 256, 1, Integer.MAX_VALUE);

		COMMON_BUILDER.pop();


		COMMON_CONFIG = COMMON_BUILDER.build();
	}

	public static void loadConfig(ForgeConfigSpec spec, java.nio.file.Path path) {
		final CommentedFileConfig configData = CommentedFileConfig.builder(path)
			.sync()
			.autosave()
			.writingMode(WritingMode.REPLACE)
			.build();
		configData.load();
		spec.setConfig(configData);
	}
}
