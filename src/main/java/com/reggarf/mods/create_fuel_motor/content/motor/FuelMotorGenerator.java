package com.reggarf.mods.create_fuel_motor.content.motor;


import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;

public class FuelMotorGenerator extends SpecialBlockStateGen {

	@Override
	protected int getXRotation(BlockState state) {
		return state.getValue(FuelMotorBlock.FACING) == Direction.DOWN ? 180 : 0;
	}

	@Override
	protected int getYRotation(BlockState state) {
		return state.getValue(FuelMotorBlock.FACING)
			.getAxis()
			.isVertical() ? 0 : horizontalAngle(state.getValue(FuelMotorBlock.FACING));
	}

	@Override
	public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
		BlockState state) {
		return state.getValue(FuelMotorBlock.FACING)
			.getAxis()
			.isVertical() ? AssetLookup.partialBaseModel(ctx, prov, "vertical")
				: AssetLookup.partialBaseModel(ctx, prov);
	}

}
