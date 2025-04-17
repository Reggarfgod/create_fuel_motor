package com.reggarf.mods.create_fuel_motor.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Mod.EventBusSubscriber
public class CommonConfig {

	public static final String catagory_general = "general";
	public static final String catagory_fuel_motor = "fuel_motor";
	public static ForgeConfigSpec.IntValue fuel_motor_rpm_range;
	public static ForgeConfigSpec.BooleanValue audio_Enabled;




		public CommonConfig(ForgeConfigSpec.Builder builder) {
			builder.comment("Make sure config changes are duplicated on both Clients and the Server when running a dedicated Server,")
					.comment(" as the config isnt synced between Clients and Server.");

			builder.comment("General Settings").push(catagory_general);
			audio_Enabled = builder.comment("If audio should be enabled or not.")
					.define("audio_enabled", true);
			builder.pop();

			builder.comment("Fuel Motor").push(catagory_fuel_motor);
			fuel_motor_rpm_range = builder.comment("Fuel Motor min/max RPM.")
					.defineInRange("motor_rpm_range", 256, 1, Integer.MAX_VALUE);
			builder.pop();
		}
}
