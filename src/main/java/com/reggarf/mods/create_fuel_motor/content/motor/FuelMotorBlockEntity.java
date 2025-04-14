package com.reggarf.mods.create_fuel_motor.content.motor;


import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reggarf.mods.create_fuel_motor.Register.CFMBlocks;
import com.reggarf.mods.create_fuel_motor.recipe.MotorFuelRecipe;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;


public class FuelMotorBlockEntity extends GeneratingKineticBlockEntity {
	private ItemStack fuel = ItemStack.EMPTY;
	public static final int DEFAULT_SPEED = 16;
	public static final int MAX_SPEED = 256;
	private MotorFuelRecipe activeFuelRecipe = null;

	//private static final int BURN_TIME_TICK_RATE = 120;
	protected ScrollValueBehaviour generatedSpeed;

	private int burnTime;
	private int maxBurnTime;



	public FuelMotorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
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
		if (!CFMBlocks.FUEL_MOTOR.has(getBlockState()) || burnTime <= 0)
			return 0;
		return convertToDirection(generatedSpeed.getValue(), getBlockState().getValue(CreativeMotorBlock.FACING));
	}


	@Override
	public void tick() {
		super.tick();
		if (level == null || level.isClientSide) return;

		float speedFactor = Math.max(1, Math.abs(getGeneratedSpeed()) / 16f); // default speed = 16
		int burnConsumption = Math.max(1, Math.round(speedFactor));

		if (burnTime > 0) {
			burnTime -= burnConsumption;
			if (burnTime < 0) burnTime = 0;
		} else {
			fuel = ItemStack.EMPTY;
			activeFuelRecipe = null;
			tryConsumeFuel();
		}

		updateGeneratedRotation();
		sendData();
	}


	private void tryConsumeFuel() {
		if (fuel.isEmpty()) {
			for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class,
					new AABB(worldPosition.above()).inflate(0.5))) {
				ItemStack stack = entity.getItem();
                //for custome recipe
				Optional<MotorFuelRecipe> recipe = level.getRecipeManager()
						.getAllRecipesFor(MotorFuelRecipe.Type.INSTANCE)
						.stream()
						.filter(r -> r.getIngredient().test(stack))
						.findFirst();

				if (recipe.isPresent()) {
					MotorFuelRecipe r = recipe.get();
					burnTime = r.getBurnTime();
					maxBurnTime = burnTime;
					fuel = stack.split(1);
					activeFuelRecipe = r; // store recipe

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

	@Override
	public float calculateAddedStressCapacity() {
		float capacity = 0f;

		if (activeFuelRecipe != null && burnTime > 0) {
			capacity = activeFuelRecipe.getStressGenerated() / 256f;
		}
		this.lastCapacityProvided = capacity;
		return capacity;
	}


	@Override
	public void write(CompoundTag tag, boolean clientPacket) {
		super.write(tag, clientPacket);
		tag.putInt("BurnTime", burnTime);
		tag.putInt("MaxBurnTime", maxBurnTime);
		//tag.put("Inventory", inventory.serializeNBT());
		if (activeFuelRecipe != null) {
			tag.putFloat("StoredStress", activeFuelRecipe.getStressGenerated());
		}
	}

	@Override
	public void read(CompoundTag tag, boolean clientPacket) {
		super.read(tag, clientPacket);
		burnTime = tag.getInt("BurnTime");
		maxBurnTime = tag.getInt("MaxBurnTime");
		//inventory.deserializeNBT(tag.getCompound("Inventory"));
		if (tag.contains("StoredStress")) {
			activeFuelRecipe = new MotorFuelRecipe(
					Ingredient.EMPTY, burnTime, tag.getFloat("StoredStress"), new ResourceLocation("stored:virtual")
			);
		}
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
