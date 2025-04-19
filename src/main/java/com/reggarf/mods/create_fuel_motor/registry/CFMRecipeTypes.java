package com.reggarf.mods.create_fuel_motor.registry;


import com.reggarf.mods.create_fuel_motor.recipe.MotorFuelRecipe;
import com.reggarf.mods.create_fuel_motor.recipe.MotorFuelRecipeType;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CFMRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, "create_fuel_motor");

    public static final RegistryObject<RecipeType<MotorFuelRecipe>> MOTOR_FUEL_TYPE =
            RECIPE_TYPES.register("motor_fuel", () -> MotorFuelRecipeType.INSTANCE);

    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
    }
}
