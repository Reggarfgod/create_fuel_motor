package com.reggarf.mods.create_fuel_motor.recipe;


import com.mojang.serialization.Codec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class MotorFuelRecipeSerializer implements RecipeSerializer<MotorFuelRecipe> {
    @Override
    public Codec<MotorFuelRecipe> codec() {
        return MotorFuelRecipe.CODEC;
    }
}
