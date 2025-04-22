package com.reggarf.mods.create_fuel_motor.datagen.provider;



import com.reggarf.mods.create_fuel_motor.registry.CFMRecipeTypes;

import com.simibubi.create.foundation.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.reggarf.mods.create_fuel_motor.datagen.builder.MotorFuelRecipeBuilder.motorFuel;


public class MotorFuelRecipeProvider extends ProcessingRecipeGen {

    public static final IRecipeTypeInfo recipeType = new IRecipeTypeInfo() {
        @Override
        public ResourceLocation getId() {
            return CFMRecipeTypes.MOTOR_FUEL_TYPE.getId();
        }

        @Override
        public <T extends RecipeSerializer<?>> T getSerializer() {
            return (T) CFMRecipeTypes.MOTOR_FUEL_TYPE.get();
        }

        @Override
        public <V extends RecipeInput, R extends Recipe<V>> RecipeType<R> getType() {
            return (RecipeType<R>) CFMRecipeTypes.MOTOR_FUEL_TYPE.get();
        }
    };

    private final HolderLookup.Provider provider;

    public MotorFuelRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
        try {
            this.provider = registries.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        motorFuel(900, 1350f)
                .require(Ingredient.of(Items.COAL))
                .save(output, "coal");

        motorFuel(900, 1250f)
                .require(Ingredient.of(Items.CHARCOAL))
                .save(output, "charcoal");

        motorFuel(100, 100f)
                .require(Ingredient.of(Items.STICK))
                .save(output, "stick");

        motorFuel(200, 150f)
                .require(Ingredient.of(Items.BAMBOO))
                .save(output, "bamboo");

        motorFuel(1600, 1200f)
                .require(Ingredient.of(Items.BLAZE_ROD))
                .save(output, "blaze_rod");

        motorFuel(300, 500f)
                .require(Ingredient.of(Items.DRIED_KELP_BLOCK))
                .save(output, "dried_kelp_block");
    }


    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return recipeType;
    }
}
