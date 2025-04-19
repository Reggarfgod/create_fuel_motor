package com.reggarf.mods.create_fuel_motor.registry;


import com.reggarf.mods.create_fuel_motor.recipe.MotorFuelRecipe;
import com.reggarf.mods.create_fuel_motor.recipe.MotorFuelRecipeSerializer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CFMRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "create_fuel_motor");

    public static final RegistryObject<RecipeSerializer<MotorFuelRecipe>> MOTOR_FUEL_SERIALIZER =
            SERIALIZERS.register("motor_fuel", () -> MotorFuelRecipeSerializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
