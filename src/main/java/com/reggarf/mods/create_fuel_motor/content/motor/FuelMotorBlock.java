package com.reggarf.mods.create_fuel_motor.content.motor;




import com.reggarf.mods.create_fuel_motor.Register.CFMBlockEntityTypes;
import com.reggarf.mods.create_fuel_motor.config.CommonConfig;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
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
	public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
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
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {


			tooltip.add(CreateLang.translate("tooltip.create_fuel_motor.generates").style(ChatFormatting.WHITE)
					.component());
		    tooltip.add(CreateLang.translate("tooltip.create_fuel_motor.generates_fuel").style(ChatFormatting.GRAY)
				.component());
			tooltip.add(CreateLang.translate("tooltip.create_fuel_motor.burns").style(ChatFormatting.WHITE)
					.component());
			tooltip.add(CreateLang.text(" ").translate("tooltip.create_fuel_motor.fuel_burn_time").style(ChatFormatting.GRAY)
					.component());

			tooltip.add(CreateLang.translate("tooltip.create_fuel_motor.max_speed").style(ChatFormatting.WHITE)
					.component());
			tooltip.add(CreateLang.text(" ").translate("tooltip.create_fuel_motor.rpm",
					StringFormattingTool.formatLong(CommonConfig.fuel_motor_rpm_range.get())).style(ChatFormatting.GRAY).component());

	}
	@Override
	public BlockEntityType<? extends FuelMotorBlockEntity> getBlockEntityType() {
		return CFMBlockEntityTypes.FUEL_MOTOR.get();
	}

}
