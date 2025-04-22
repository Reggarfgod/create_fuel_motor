package com.reggarf.mods.create_fuel_motor.datagen.builder;

import com.google.gson.JsonObject;

import com.reggarf.mods.create_fuel_motor.registry.CFMRecipeSerializers;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

/**
 * Recipe builder for MotorFuelRecipe.
 */
public class MotorFuelRecipeBuilder {
    protected final Ingredient ingredient;
    protected final int burnTime;
    protected final float stressGenerated;
    protected final String group;

    public MotorFuelRecipeBuilder(Ingredient ingredient, int burnTime, float stressGenerated, String group) {
        this.ingredient = ingredient;
        this.burnTime = burnTime;
        this.stressGenerated = stressGenerated;
        this.group = group;
    }

    public static MotorFuelRecipeBuilder motorFuel(Ingredient ingredient, int burnTime, float stressGenerated) {
        return new MotorFuelRecipeBuilder(ingredient, burnTime, stressGenerated, "");
    }

    public static MotorFuelRecipeBuilder motorFuel(Ingredient ingredient, int burnTime, float stressGenerated, String group) {
        return new MotorFuelRecipeBuilder(ingredient, burnTime, stressGenerated, group);
    }

    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        consumer.accept(new Result(id));
    }

    private class Result implements FinishedRecipe {
        private final ResourceLocation id;

        public Result(ResourceLocation id) {
            this.id = id;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            if (!group.isEmpty()) {
                json.addProperty("group", group);
            }
            json.add("ingredient", ingredient.toJson());
            json.addProperty("burn_time", burnTime);
            json.addProperty("stress", stressGenerated);
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public net.minecraft.world.item.crafting.RecipeSerializer<?> getType() {
            return CFMRecipeSerializers.MOTOR_FUEL_SERIALIZER.get();
        }

        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
