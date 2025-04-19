package com.reggarf.mods.create_fuel_motor.recipe;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;

public class MotorFuelRecipeType implements RecipeType<MotorFuelRecipe> {
    public static final MotorFuelRecipeType INSTANCE = new MotorFuelRecipeType();
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("create_fuel_motor", "motor_fuel");

    @Override
    public String toString() {
        return ID.toString();
    }
}
