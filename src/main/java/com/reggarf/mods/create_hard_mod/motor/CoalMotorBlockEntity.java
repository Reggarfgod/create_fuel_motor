package com.reggarf.mods.create_hard_mod.motor;


import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reggarf.mods.create_hard_mod.CBMBlocks;
import com.reggarf.mods.create_hard_mod.CommonConfigs;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.motor.CreativeMotorBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.ItemStackHandler;


public class CoalMotorBlockEntity extends GeneratingKineticBlockEntity {
	private ItemStack fuel = ItemStack.EMPTY;
	public static final int DEFAULT_SPEED = 16;
	public static final int MAX_SPEED = 256;
	//private static final int BURN_TIME_TICK_RATE = 120;

	protected ScrollValueBehaviour generatedSpeed;
	//private final ItemStackHandler inventory = new ItemStackHandler(1);
	private int burnTime;
	private int maxBurnTime;

	public CoalMotorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		int max = MAX_SPEED;
		generatedSpeed = new KineticScrollValueBehaviour(CreateLang.translateDirect("kinetics.creative_motor.rotation_speed"),
				this, new MotorValueBox());
		generatedSpeed.between(-max, max);
		generatedSpeed.value = DEFAULT_SPEED;
		generatedSpeed.withCallback(i -> this.updateGeneratedRotation());
		behaviours.add(generatedSpeed);
	}

	@Override
	public void initialize() {
		super.initialize();
		if (!hasSource() || getGeneratedSpeed() > getTheoreticalSpeed())
			updateGeneratedRotation();
	}

	@Override
	public float getGeneratedSpeed() {
		if (!CBMBlocks.CREATIVE_MOTOR.has(getBlockState()) || burnTime <= 0)
			return 0;
		return convertToDirection(generatedSpeed.getValue(), getBlockState().getValue(CreativeMotorBlock.FACING));
	}


	@Override
	public void tick() {
		super.tick();
		if (level == null || level.isClientSide) return;

		if (burnTime > 0) {
			burnTime--;
		} else {
			fuel = ItemStack.EMPTY; // 🔥 Clear used fuel!
			tryConsumeFuel();
		}

		updateGeneratedRotation();
		sendData();
	}

	private void tryConsumeFuel() {
		// Try to find fuel dropped on top
		if (fuel.isEmpty()) {
			for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class,
					new net.minecraft.world.phys.AABB(worldPosition.above()).inflate(0.5))) {
				ItemStack stack = entity.getItem();
				int time = ForgeHooks.getBurnTime(stack, RecipeType.SMELTING);

				if (time > 0) {
					burnTime = time;
					maxBurnTime = time;
					fuel = stack.split(1);
					if (stack.isEmpty()) entity.discard();
					else entity.setItem(stack);

					if (level instanceof ServerLevel serverLevel) {
						serverLevel.sendParticles(ParticleTypes.FLAME,
								worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5,
								5, 0.1, 0.1, 0.1, 0.01);
					}


					break;
				}
			}
		}
	}
	public float calculateAddedStressCapacity() {
		float capacity = CommonConfigs.COAL_MOTOR_STRESS.get()/256f;
		this.lastCapacityProvided = capacity;
		return capacity;
	}

	@Override
	public void write(CompoundTag tag, boolean clientPacket) {
		super.write(tag, clientPacket);
		tag.putInt("BurnTime", burnTime);
		tag.putInt("MaxBurnTime", maxBurnTime);
		//tag.put("Inventory", inventory.serializeNBT());
	}

	@Override
	public void read(CompoundTag tag, boolean clientPacket) {
		super.read(tag, clientPacket);
		burnTime = tag.getInt("BurnTime");
		maxBurnTime = tag.getInt("MaxBurnTime");
		//inventory.deserializeNBT(tag.getCompound("Inventory"));
	}

	class MotorValueBox extends ValueBoxTransform.Sided {
		@Override
		protected Vec3 getSouthLocation() {
			return VecHelper.voxelSpace(8, 8, 12.5);
		}

		@Override
		public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
			Direction facing = state.getValue(CreativeMotorBlock.FACING);
			return super.getLocalOffset(level, pos, state).add(Vec3.atLowerCornerOf(facing.getNormal()).scale(-1 / 16f));
		}

		@Override
		public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
			super.rotate(level, pos, state, ms);
			Direction facing = state.getValue(CreativeMotorBlock.FACING);
			if (facing.getAxis() == Axis.Y)
				return;
			if (getSide() != Direction.UP)
				return;
			TransformStack.of(ms).rotateZDegrees(-AngleHelper.horizontalAngle(facing) + 180);
		}

		@Override
		protected boolean isSideActive(BlockState state, Direction direction) {
			Direction facing = state.getValue(CreativeMotorBlock.FACING);
			if (facing.getAxis() != Axis.Y && direction == Direction.DOWN)
				return false;
			return direction.getAxis() != facing.getAxis();
		}
	}
}
