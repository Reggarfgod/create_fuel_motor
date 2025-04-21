package com.reggarf.mods.create_fuel_motor.recipe;


import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.reggarf.mods.create_fuel_motor.recipe.MotorFuelRecipeType.ID;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, "create_fuel_motor");

    public static final DeferredHolder<RecipeType<?>, RecipeType<MotorFuelRecipe>> MOTOR_FUEL =
            TYPES.register("motor_fuel", () -> new RecipeType<MotorFuelRecipe>() {
                @Override
                public String toString() {
                    return ID.toString();
                }
            });
    public static void register() {  }
}
