package com.reggarf.mods.create_fuel_motor.motor;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class FuelMotorRenderer extends KineticBlockEntityRenderer<FuelMotorBlockEntity> {

	public FuelMotorRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected SuperByteBuffer getRotatedModel(FuelMotorBlockEntity be, BlockState state) {
		return CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state);
	}

}
