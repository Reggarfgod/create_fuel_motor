package com.reggarf.mods.create_fuel_motor.datagen.provider;

import com.reggarf.mods.create_fuel_motor.datagen.builder.MotorFuelRecipeBuilder;
import com.simibubi.create.AllItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

public class MotorFuelRecipeProvider extends RecipeProvider {

    public MotorFuelRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        // Core fuels
        MotorFuelRecipeBuilder.motorFuel(Ingredient.of(Items.COAL), 900, 1350f)
                .save(consumer, new ResourceLocation("create_fuel_motor", "motor_fuel/coal"));

        MotorFuelRecipeBuilder.motorFuel(Ingredient.of(Items.CHARCOAL), 850, 1250f)
                .save(consumer, new ResourceLocation("create_fuel_motor", "motor_fuel/charcoal"));

        MotorFuelRecipeBuilder.motorFuel(Ingredient.of(Items.COAL_BLOCK), 8100, 1350f)
                .save(consumer, new ResourceLocation("create_fuel_motor", "motor_fuel/coal_block"));

        MotorFuelRecipeBuilder.motorFuel(Ingredient.of(Items.BLAZE_ROD), 400, 800f)
                .save(consumer, new ResourceLocation("create_fuel_motor", "motor_fuel/blaze_rod"));

        MotorFuelRecipeBuilder.motorFuel(Ingredient.of(Items.BLAZE_POWDER), 200, 850f)
                .save(consumer, new ResourceLocation("create_fuel_motor", "motor_fuel/blaze_powder"));

        MotorFuelRecipeBuilder.motorFuel(Ingredient.of(Items.DRIED_KELP_BLOCK), 450, 1180f)
                .save(consumer, new ResourceLocation("create_fuel_motor", "motor_fuel/dried_kelp_block"));

        // Generic wooden logs (all types)
        MotorFuelRecipeBuilder.motorFuel(
                Ingredient.of(TagKey.create(Registries.ITEM, new ResourceLocation("minecraft", "logs"))),
                400, 250f
        ).save(consumer, new ResourceLocation("create_fuel_motor", "motor_fuel/logs"));

        // Generic wooden planks
        MotorFuelRecipeBuilder.motorFuel(
                Ingredient.of(TagKey.create(Registries.ITEM, new ResourceLocation("minecraft", "planks"))),
                100, 230f
        ).save(consumer, new ResourceLocation("create_fuel_motor", "motor_fuel/planks"));

        // Sticks
        MotorFuelRecipeBuilder.motorFuel(Ingredient.of(Items.STICK), 100, 81f)
                .save(consumer, new ResourceLocation("create_fuel_motor", "motor_fuel/stick"));

        // Bamboo
        MotorFuelRecipeBuilder.motorFuel(Ingredient.of(Items.BAMBOO), 100, 150f)
                .save(consumer, new ResourceLocation("create_fuel_motor", "motor_fuel/bamboo"));

        // Scaffolding
        MotorFuelRecipeBuilder.motorFuel(
                Ingredient.of(Items.SCAFFOLDING),
                        200,
                        250f)
                .save(consumer, new ResourceLocation("create_fuel_motor", "motor_fuel/scaffolding"));

        // Hay Block
        MotorFuelRecipeBuilder.motorFuel(Ingredient.of(Items.HAY_BLOCK), 100, 120f)
                .save(consumer, new ResourceLocation("create_fuel_motor", "motor_fuel/hay_block"));

        MotorFuelRecipeBuilder.motorFuel(Ingredient.of(AllItems.BLAZE_CAKE), 2000, 16549f)
                .save(consumer, new ResourceLocation("create_fuel_motor", "motor_fuel/blaze_cake"));
    }
}
