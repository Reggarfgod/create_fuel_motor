package com.reggarf.mods.create_fuel_motor.recipe;


import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, "create_fuel_motor");

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MotorFuelRecipe>> MOTOR_FUEL =
            SERIALIZERS.register("motor_fuel", MotorFuelRecipeSerializer::new);
    public static void register() {  }
}


