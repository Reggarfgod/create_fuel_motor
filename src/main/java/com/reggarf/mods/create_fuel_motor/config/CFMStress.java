package com.reggarf.mods.create_fuel_motor.config;


import com.reggarf.mods.create_fuel_motor.Create_fuel_motor;
import com.simibubi.create.Create;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.createmod.catnip.config.ConfigBase;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleSupplier;

public class CFMStress extends ConfigBase {

	private static final int VERSION = 2;

	private static final Object2DoubleMap<ResourceLocation> DEFAULT_IMPACTS = new Object2DoubleOpenHashMap<>();
	private static final Object2DoubleMap<ResourceLocation> DEFAULT_CAPACITIES = new Object2DoubleOpenHashMap<>();

	protected final Map<ResourceLocation, ConfigValue<Double>> capacities = new HashMap<>();
	protected final Map<ResourceLocation, ConfigValue<Double>> impacts = new HashMap<>();

	@Override
	public void registerAll(Builder builder) {
		builder.comment(".", Comments.su, Comments.impact)
				.push("impact");
		DEFAULT_IMPACTS.forEach((id, value) -> this.impacts.put(id, builder.define(id.getPath(), value)));
		builder.pop();

		builder.comment(".", Comments.su, Comments.capacity)
				.push("capacity");
		DEFAULT_CAPACITIES.forEach((id, value) -> this.capacities.put(id, builder.define(id.getPath(), value)));
		builder.pop();
	}

	@Override
	public String getName() {
		return "stressValues.v" + VERSION;
	}

	@Nullable
	public DoubleSupplier getImpact(Block block) {
		ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow(block);
		ConfigValue<Double> value = this.impacts.get(id);
		return value == null ? null : value::get;
	}

	@Nullable
	public DoubleSupplier getCapacity(Block block) {
		ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow(block);
		ConfigValue<Double> value = this.capacities.get(id);
		return value == null ? null : value::get;
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setNoImpact() {
		return setImpact(0);
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setImpact(double value) {
		return builder -> {
			assertFromCreateCasing(builder);
			ResourceLocation id = Create.asResource(builder.getName());
			DEFAULT_IMPACTS.put(id, value);
			return builder;
		};
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setCapacity(double value) {
		return builder -> {
			assertFromCreateCasing(builder);
			ResourceLocation id = Create_fuel_motor.asResource(builder.getName());
			DEFAULT_CAPACITIES.put(id, value);
			return builder;
		};
	}

	private static void assertFromCreateCasing(BlockBuilder<?, ?> builder) {
		if (!builder.getOwner().getModid().equals(Create_fuel_motor.MOD_ID)) {
			throw new IllegalStateException("Non-Create Casing blocks cannot be added to Create Casing's config.");
		}
	}

	private static class Comments {
		static String su = "[in Stress Units]";
		static String impact = "Configure the individual stress impact of mechanical blocks. Note that this cost is doubled for every speed increase it receives.";
		static String capacity = "Configure how much stress a source can accommodate for.";
	}

}