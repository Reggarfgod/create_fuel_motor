package com.reggarf.mods.create_fuel_motor.motor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reggarf.mods.create_fuel_motor.config.CommonConfig;
import com.reggarf.mods.create_fuel_motor.recipe.MotorFuelRecipe;
import com.reggarf.mods.create_fuel_motor.recipe.MotorFuelRecipeType;
import com.reggarf.mods.create_fuel_motor.registry.CFMBlocks;
import com.reggarf.mods.create_fuel_motor.registry.CFMRecipeTypes;
import com.reggarf.mods.create_fuel_motor.util.StringFormattingTool;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.motor.CreativeMotorBlock;
import com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;


import java.util.List;
import java.util.Optional;

public class FuelMotorBlockEntity extends GeneratingKineticBlockEntity {

	private int burnTime = 0;
	private int maxBurnTime = 0;
	private float stressGenerated = 0f;

	private final ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			setChanged();
		}

		@Override
		public int getSlotLimit(int slot) {
			return 1;
		}
	};
	//private final LazyOptional<ItemStackHandler> inventoryCapability = LazyOptional.of(() -> inventory);

	public ItemStackHandler getInventory() {
		return inventory;
	}

	private ScrollValueBehaviour generatedSpeed;

	public FuelMotorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		setLazyTickRate(10);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		generatedSpeed = new KineticScrollValueBehaviour(
				CreateLang.translateDirect("kinetics.creative_motor.rotation_speed"),
				this,
				new MotorValueBox()
		);
		generatedSpeed.between(-CommonConfig.fuel_motor_rpm_range.get(), CommonConfig.fuel_motor_rpm_range.get());
		generatedSpeed.value = 32;
		generatedSpeed.withCallback(i -> this.updateGeneratedRotation());
		behaviours.add(generatedSpeed);
	}

//	@Override
//	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
//		if (cap == ForgeCapabilities.ITEM_HANDLER)
//			return inventoryCapability.cast();
//		return super.getCapability(cap, side);
//	}
//
//	@Override
//	public void invalidateCaps() {
//		super.invalidateCaps();
//		inventoryCapability.invalidate();
//	}

	@Override
	public void tick() {
		super.tick();
		if (level.isClientSide) return;

		if (burnTime > 0) {
			burnTime--;
			sendData(); //Sync to client
		} else if (!tryConsumeFuelFromInventory() && !tryPickupFuel()) {
			updateGeneratedRotation();
		}
	}


	private boolean tryConsumeFuelFromInventory() {
		for (int i = 0; i < inventory.getSlots(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack.isEmpty()) continue;

			Optional<MotorFuelRecipe> optionalRecipe = getFuelRecipe(stack);
			if (optionalRecipe.isPresent()) {
				applyFuelRecipe(optionalRecipe.get());
				stack.shrink(1);
				return true;
			}
		}
		return false;
	}

	private boolean tryPickupFuel() {
		AABB area = new AABB(worldPosition).inflate(CommonConfig.fuel_motor_pickup_range.get());
		List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);

		for (ItemEntity itemEntity : items) {
			ItemStack stack = itemEntity.getItem();
			if (stack.isEmpty()) continue;

			Optional<MotorFuelRecipe> optionalRecipe = getFuelRecipe(stack);
			if (optionalRecipe.isPresent()) {
				applyFuelRecipe(optionalRecipe.get());
				stack.shrink(1);
				if (stack.isEmpty()) itemEntity.discard();
				return true;
			}
		}
		return false;
	}





	private Optional<MotorFuelRecipe> getFuelRecipe(ItemStack stack) {
		RecipeManager recipeManager = level.getRecipeManager();

		List<RecipeHolder<MotorFuelRecipe>> holders = recipeManager.getAllRecipesFor(CFMRecipeTypes.MOTOR_FUEL_TYPE.get());

		for (RecipeHolder<MotorFuelRecipe> holder : holders) {
			MotorFuelRecipe recipe = holder.value();
			if (recipe.getIngredient().test(stack)) {
				return Optional.of(recipe);
			}
		}

		return Optional.empty();
	}

	private void applyFuelRecipe(MotorFuelRecipe recipe) {
		burnTime = maxBurnTime = recipe.getBurnTime();
		stressGenerated = recipe.getStressGenerated();
		updateGeneratedRotation();
		spawnFuelParticles();
	}

	private void spawnFuelParticles() {
		if (!(level instanceof ServerLevel serverLevel)) return;

		Vec3 center = Vec3.atCenterOf(getBlockPos());
		serverLevel.sendParticles(ParticleTypes.FLAME, center.x, center.y + 0.25, center.z, 6, 0.25, 0.25, 0.25, 0.01);
		serverLevel.sendParticles(ParticleTypes.SMOKE, center.x, center.y + 0.3, center.z, 3, 0.2, 0.2, 0.2, 0.005);
	}

	@Override
	public float getGeneratedSpeed() {
		return isRunning() ? convertToDirection(generatedSpeed.getValue(), getBlockState().getValue(FuelMotorBlock.FACING)) : 0;
	}

	@Override
	public float calculateAddedStressCapacity() {
		float speed = Math.abs(getGeneratedSpeed());
		if (!isRunning() || speed == 0) {
			lastCapacityProvided = 0;
			return 0;
		}
		float capacity = stressGenerated / speed;
		lastCapacityProvided = capacity;
		return capacity;
	}

	private boolean isRunning() {
		return burnTime > 0;
	}

	@Override
	protected Block getStressConfigKey() {
		return CFMBlocks.FUEL_MOTOR.get();
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		super.addToGoggleTooltip(tooltip, isPlayerSneaking);

		if (isRunning()) {
			CreateLang.translate("tooltip.create_fuel_motor.fuel_status_burn")
					.style(ChatFormatting.GRAY)
					.forGoggles(tooltip);

			CreateLang.translate("tooltip.create_fuel_motor.burn_time",
							StringFormattingTool.formatLong(burnTime / 20),
							StringFormattingTool.formatLong(maxBurnTime / 20))
					.style(ChatFormatting.AQUA)
					.forGoggles(tooltip, 1);

			CreateLang.translate("tooltip.create_fuel_motor.fuel_status_header")
					.style(ChatFormatting.GRAY)
					.forGoggles(tooltip);

			CreateLang.translate("tooltip.create_fuel_motor.fuel_status", "Yes")
					.style(ChatFormatting.AQUA)
					.forGoggles(tooltip);

			CreateLang.translate("tooltip.create_fuel_motor.fuel_name_header")
					.style(ChatFormatting.GRAY)
					.forGoggles(tooltip);

			for (int i = 0; i < inventory.getSlots(); i++) {
				ItemStack stack = inventory.getStackInSlot(i);
				if (!stack.isEmpty()) {
					CreateLang.translate("tooltip.create_fuel_motor.fuel_name", stack.getHoverName().getString())
							.style(ChatFormatting.AQUA)
							.forGoggles(tooltip);
				}
			}
		} else {
			CreateLang.translate("tooltip.create_fuel_motor.no_fuel")
					.style(ChatFormatting.WHITE)
					.forGoggles(tooltip);

			CreateLang.translate("tooltip.create_fuel_motor.fuel_status", "No fuel detected")
					.style(ChatFormatting.RED)
					.forGoggles(tooltip);
		}

		return true;
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		burnTime = tag.getInt("BurnTime");
		maxBurnTime = tag.getInt("MaxBurnTime");
		stressGenerated = tag.getFloat("StressGenerated");
		if (tag.contains("Inventory")) inventory.deserializeNBT(registries, tag.getCompound("inventory"));
	}

	@Override
	public void writeSafe(CompoundTag tag, HolderLookup.Provider registries) {
		super.writeSafe(tag, registries);
		tag.putInt("BurnTime", burnTime);
		tag.putInt("MaxBurnTime", maxBurnTime);
		tag.putFloat("StressGenerated", stressGenerated);
		tag.put("Inventory", inventory.serializeNBT(registries));
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
			if (facing.getAxis() == Axis.Y || getSide() != Direction.UP) return;
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
