package com.reggarf.mods.create_fuel_motor.datagen.provider;



import com.reggarf.mods.create_fuel_motor.registry.CFMRecipeTypes;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
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

        // Core fuels
        motorFuel(900, 1350f)
                .require(Ingredient.of(Items.COAL))
                .save(output, "coal");

        motorFuel(850, 1250f)
                .require(Ingredient.of(Items.CHARCOAL))
                .save(output, "charcoal");

        motorFuel(8100, 1350f)
                .require(Ingredient.of(Items.COAL_BLOCK))
                .save(output, "coal_block");


        motorFuel(400, 800f)
                .require(Ingredient.of(Items.BLAZE_ROD))
                .save(output, "blaze_rod");
        motorFuel(200, 850f)
                .require(Ingredient.of(Items.BLAZE_POWDER))
                .save(output, "blaze_powder");

        motorFuel(450, 1180f)
                .require(Ingredient.of(Items.DRIED_KELP_BLOCK))
                .save(output, "dried_kelp_block");

        // Generic wooden logs (all types)
        motorFuel(400, 250f)
                .require(Ingredient.of(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("minecraft", "logs"))))
                .save(output, "logs");

        // Generic wooden planks
        motorFuel(100, 230f)
                .require(Ingredient.of(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("minecraft", "planks"))))
                .save(output, "planks");

        // Sticks
        motorFuel(100, 81f)
                .require(Ingredient.of(Items.STICK))
                .save(output, "stick");

        // Bamboo
        motorFuel(100, 150f)
                .require(Ingredient.of(Items.BAMBOO))
                .save(output, "bamboo");

        // Scaffolding (made from bamboo + string)
        motorFuel(200, 250f)
                .require(Ingredient.of(Items.SCAFFOLDING))
                .save(output, "scaffolding");

        // Hay Block - decent fuel
        motorFuel(100, 120f)
                .require(Ingredient.of(Items.HAY_BLOCK))
                .save(output, "hay_block");
        motorFuel(2000, 16549f)
                .require(Ingredient.of(AllItems.BLAZE_CAKE))
                .save(output, "blaze_cake");

    }


    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return recipeType;
    }
}
