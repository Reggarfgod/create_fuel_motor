package com.reggarf.mods.create_fuel_motor.registry;


import com.reggarf.mods.create_fuel_motor.recipe.MotorFuelRecipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CFMRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, "create_fuel_motor");

     public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MotorFuelRecipe>> MOTOR_FUEL_SERIALIZER =
            SERIALIZERS.register("motor_fuel", () -> MotorFuelRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}


