package com.reggarf.mods.create_fuel_motor.armpoint;

import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import com.reggarf.mods.create_fuel_motor.content.motor.FuelMotorBlockEntity;
import com.reggarf.mods.create_fuel_motor.recipe.MotorFuelRecipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class FuelMotorArmInteractionPoint extends AllArmInteractionPointTypes.DepositOnlyArmInteractionPoint {
//TODO: arm feeding
    public FuelMotorArmInteractionPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Override
    public ItemStack insert(ItemStack stack, boolean simulate) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof FuelMotorBlockEntity motor)) {
            // Log or throw error if the block entity is not the expected type
            return stack;
        }

        // Don't insert if there's already fuel in the motor
        if (!motor.getFuel().isEmpty()) {
            return stack;
        }

        // Find a matching fuel recipe
        Optional<MotorFuelRecipe> recipeOpt = level.getRecipeManager()
                .getAllRecipesFor(MotorFuelRecipe.Type.INSTANCE)
                .stream()
                .filter(r -> r.getIngredient().test(stack))
                .findFirst();

        if (recipeOpt.isEmpty()) {
            return stack;
        }

        // Insert fuel if simulate is false
        if (!simulate) {
            // Extract one item from the stack
            ItemStack toInsert = stack.split(1);
            motor.setFuel(toInsert);  // Set the fuel in the motor

            // Set the active recipe and burn time
            MotorFuelRecipe recipe = recipeOpt.get();
            motor.setActiveRecipe(recipe);
            motor.setBurnTime(recipe.getBurnTime());
            motor.setMaxBurnTime(recipe.getBurnTime());

            // Mark motor as changed and sync data
            motor.setChanged();
            motor.sendData();
        }

        // Return the remaining stack (the original stack minus the inserted item)
        return stack;
    }

}
