package com.reggarf.mods.create_fuel_motor.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class MotorFuelRecipeSerializer implements RecipeSerializer<MotorFuelRecipe> {
    public static final MotorFuelRecipeSerializer INSTANCE = new MotorFuelRecipeSerializer();
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("create_fuel_motor", "motor_fuel");

    @Override
    public MotorFuelRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
        int burnTime = GsonHelper.getAsInt(json, "burn_time");
        float stress = GsonHelper.getAsFloat(json, "stress");
        return new MotorFuelRecipe(ingredient, burnTime, stress, recipeId);
    }

    @Override
    public MotorFuelRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        Ingredient ingredient = Ingredient.fromNetwork(buffer);
        int burnTime = buffer.readInt();
        float stress = buffer.readFloat();
        return new MotorFuelRecipe(ingredient, burnTime, stress, recipeId);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, MotorFuelRecipe recipe) {
        recipe.getIngredient().toNetwork(buffer);
        buffer.writeInt(recipe.getBurnTime());
        buffer.writeFloat(recipe.getStressGenerated());
    }
}
