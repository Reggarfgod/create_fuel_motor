package com.reggarf.mods.create_fuel_motor.content.motor;

import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;

import com.reggarf.mods.create_fuel_motor.Register.CFMBlocks;
import com.reggarf.mods.create_fuel_motor.config.Config;
import com.reggarf.mods.create_fuel_motor.recipe.MotorFuelRecipe;
import com.reggarf.mods.create_fuel_motor.util.StringFormattingTool;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.motor.CreativeMotorBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

public class FuelMotorBlockEntity extends GeneratingKineticBlockEntity {
	private ItemStack fuel = ItemStack.EMPTY;
	public static final int DEFAULT_SPEED = 32;
	private MotorFuelRecipe activeFuelRecipe = null;
	protected ScrollValueBehaviour generatedSpeed;
	private int burnTime;
	private int maxBurnTime;

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

	private final LazyOptional<ItemStackHandler> inventoryCap = LazyOptional.of(() -> inventory);

	public FuelMotorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public <T> LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER)
			return inventoryCap.cast();
		return super.getCapability(cap, side);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		generatedSpeed = new KineticScrollValueBehaviour(CreateLang.translateDirect("kinetics.creative_motor.rotation_speed"),
				this, new MotorValueBox());
		generatedSpeed.between(-Config.FUEL_MOTOR_RPM_RANGE.get(), Config.FUEL_MOTOR_RPM_RANGE.get());
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

		if (level == null || level.isClientSide)
			return;

		if (burnTime <= 0) {
			tryConsumeFuel();
		}

		if (burnTime > 0) {
			float speedFactor = Math.max(1, Math.abs(getGeneratedSpeed()) / 16f);
			int burnConsumption = Math.max(1, Math.round(speedFactor));
			burnTime -= burnConsumption;

			if (burnTime <= 0) {
				burnTime = 0;
				activeFuelRecipe = null;
				fuel = ItemStack.EMPTY;
				tryConsumeFuel();
			}
		}

		updateGeneratedRotation();
		sendData();
	}


	private void tryConsumeFuel() {
		if (fuel.isEmpty()) {
			ItemStack stackInSlot = inventory.getStackInSlot(0);
			if (!stackInSlot.isEmpty()) {
				Optional<MotorFuelRecipe> recipe = level.getRecipeManager()
						.getAllRecipesFor(MotorFuelRecipe.Type.INSTANCE)
						.stream()
						.filter(r -> r.getIngredient().test(stackInSlot))
						.findFirst();

				if (recipe.isPresent()) {
					MotorFuelRecipe r = recipe.get();
					burnTime = r.getBurnTime();
					maxBurnTime = burnTime;
					fuel = stackInSlot.split(1);
					activeFuelRecipe = r;
					inventory.setStackInSlot(0, stackInSlot);
					return;
				}
			}
		}

		if (fuel.isEmpty()) {
			AABB searchBox = new AABB(worldPosition).inflate(3);
			for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, searchBox)) {
				ItemStack stack = entity.getItem();
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
					activeFuelRecipe = r;

					if (stack.isEmpty()) entity.discard();
					else entity.setItem(stack);

					if (level instanceof ServerLevel serverLevel) {
						serverLevel.sendParticles(ParticleTypes.FLAME,
								worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
								5, 0.3, 0.3, 0.3, 0.01);
					}

					break;
				}
			}
		}
	}

//	@Override
//	public float calculateAddedStressCapacity() {
//		float capacity = 0f;
//		if (activeFuelRecipe != null && burnTime > 0) {
//			capacity = activeFuelRecipe.getStressGenerated() / 256f;
//		}
//		this.lastCapacityProvided = capacity;
//		return capacity;
//	}
@Override
public float calculateAddedStressCapacity() {
	if (activeFuelRecipe == null || getGeneratedSpeed() == 0)
		return 0f;

	float speed = Math.abs(getGeneratedSpeed());
	float desiredStress = activeFuelRecipe.getStressGenerated();
	float Stress = desiredStress / speed;
	     // Stress = Math.min(Stress, desiredStress);

	this.lastStressApplied = Stress;
	return Stress;
}
	@Override
	public void write(CompoundTag tag, boolean clientPacket) {
		super.write(tag, clientPacket);
		tag.putInt("BurnTime", burnTime);
		tag.putInt("MaxBurnTime", maxBurnTime);
		tag.put("Inventory", inventory.serializeNBT());
		if (activeFuelRecipe != null) {
			tag.putFloat("StoredStress", activeFuelRecipe.getStressGenerated());
		}
	}

	@Override
	public void read(CompoundTag tag, boolean clientPacket) {
		super.read(tag, clientPacket);
		burnTime = tag.getInt("BurnTime");
		maxBurnTime = tag.getInt("MaxBurnTime");
		if (tag.contains("Inventory"))
			inventory.deserializeNBT(tag.getCompound("Inventory"));

		if (tag.contains("StoredStress")) {
			activeFuelRecipe = new MotorFuelRecipe(
					Ingredient.EMPTY, burnTime, tag.getFloat("StoredStress"), new ResourceLocation("stored:virtual")
			);
		}
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		if (activeFuelRecipe != null) {
			CreateLang.translate("tooltip.create_fuel_motor.fuel_info")
					.style(ChatFormatting.WHITE)
					.forGoggles(tooltip);

			CreateLang.translate("tooltip.create_fuel_motor.fuel_item", fuel.getDisplayName().getString())
					.style(ChatFormatting.GRAY)
					.forGoggles(tooltip, 1);

			CreateLang.translate("tooltip.create_fuel_motor.burn_time",
							burnTime / 20, maxBurnTime / 20)
					.style(ChatFormatting.GRAY)
					.forGoggles(tooltip, 1);

		} else {
			CreateLang.translate("tooltip.create_fuel_motor.no_fuel")
					.style(ChatFormatting.DARK_GRAY)
					.forGoggles(tooltip);
		}

		return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
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
			if (facing.getAxis() == Axis.Y) return;
			if (getSide() != Direction.UP) return;
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

	public ItemStack getFuel() { return fuel; }
	public void setFuel(ItemStack stack) { this.fuel = stack; }
	public void setActiveRecipe(MotorFuelRecipe recipe) { this.activeFuelRecipe = recipe; }
	public void setBurnTime(int time) { this.burnTime = time; }
	public void setMaxBurnTime(int time) { this.maxBurnTime = time; }
}
