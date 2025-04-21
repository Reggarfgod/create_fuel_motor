package com.reggarf.mods.create_fuel_motor.motor;


import com.reggarf.mods.create_fuel_motor.config.CommonConfig;
import com.reggarf.mods.create_fuel_motor.registry.CFMBlockEntityTypes;
import com.reggarf.mods.create_fuel_motor.util.StringFormattingTool;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FuelMotorBlock extends DirectionalKineticBlock implements IBE<FuelMotorBlockEntity> {

	public FuelMotorBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return AllShapes.MOTOR_BLOCK.get(state.getValue(FACING));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction preferred = getPreferredFacing(context);
		if ((context.getPlayer() != null && context.getPlayer()
			.isShiftKeyDown()) || preferred == null)
			return super.getStateForPlacement(context);
		return defaultBlockState().setValue(FACING, preferred);
	}
	@Override
	public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!oldState.is(newState.getBlock())) {
			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof FuelMotorBlockEntity motor) {
				ItemStackHandler inv = motor.getInventory();
				for (int i = 0; i < inv.getSlots(); i++) {
					ItemStack stack = inv.getStackInSlot(i);
					if (!stack.isEmpty())
						Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
				}
			}
			super.onRemove(oldState, level, pos, newState, isMoving);
		}
	}


	// IRotate:

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face == state.getValue(FACING);
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.getValue(FACING)
			.getAxis();
	}

	@Override
	public boolean hideStressImpact() {
		return true;
	}


	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
		return false;
	}
	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onPlace(state, level, pos, oldState, isMoving);
		level.scheduleTick(pos, this, 1);
	}



	@Override
	public Class<FuelMotorBlockEntity> getBlockEntityClass() {
		return FuelMotorBlockEntity.class;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, tooltip, flag);

		tooltip.add(CreateLang.translate("tooltip.create_fuel_motor.generates").style(ChatFormatting.WHITE)
				.component());
		tooltip.add(CreateLang.translate("tooltip.create_fuel_motor.generates_fuel").style(ChatFormatting.DARK_GRAY)
				.component());
		tooltip.add(CreateLang.translate("tooltip.create_fuel_motor.burns").style(ChatFormatting.WHITE)
				.component());
		tooltip.add(CreateLang.text(" ").translate("tooltip.create_fuel_motor.fuel_burn_time").style(ChatFormatting.DARK_GRAY)
				.component());

		tooltip.add(CreateLang.translate("tooltip.create_fuel_motor.max_speed").style(ChatFormatting.WHITE)
				.component());
		tooltip.add(CreateLang.text(" ").translate("tooltip.create_fuel_motor.rpm",
				StringFormattingTool.formatLong(CommonConfig.fuel_motor_rpm_range.get())).style(ChatFormatting.DARK_GRAY).component());

	}

	@Override
	public BlockEntityType<? extends FuelMotorBlockEntity> getBlockEntityType() {
		return CFMBlockEntityTypes.FUEL_MOTOR.get();
	}

}
