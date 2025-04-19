package com.reggarf.mods.create_fuel_motor.registry;

import com.reggarf.mods.create_fuel_motor.recipe.MotorFuelRecipe;
import com.reggarf.mods.create_fuel_motor.recipe.MotorFuelRecipeType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;


import static com.reggarf.mods.create_fuel_motor.Create_fuel_motor.MOD_ID;

public class CFMRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, MOD_ID);

    public static final DeferredRegister<RecipeType<MotorFuelRecipe>> MOTOR_FUEL_TYPE =
            RECIPE_TYPES.register("motor_fuel", () ->  MotorFuelRecipeType.INSTANCE);

    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
    }
}
