package com.reggarf.mods.create_fuel_motor.Register;


import com.reggarf.mods.create_fuel_motor.recipe.MotorFuelRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.reggarf.mods.create_fuel_motor.Create_fuel_motor.MOD_ID;

public class CFMRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MOD_ID);

    public static final RegistryObject<RecipeSerializer<MotorFuelRecipe>> COAL_MOTOR_FUEL_SERIALIZER =
            SERIALIZERS.register("motor_fuel", () -> MotorFuelRecipe.Serializer.INSTANCE);


    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
