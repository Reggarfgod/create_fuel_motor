package com.reggarf.mods.create_fuel_motor.datagen.builder;


import com.reggarf.mods.create_fuel_motor.recipe.MotorFuelRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Recipe builder for MotorFuelRecipe.
 */
public class MotorFuelRecipeBuilder {
    protected Ingredient ingredient;
    protected int burnTime;
    protected float stressGenerated;
    protected String group;

    public MotorFuelRecipeBuilder(int burnTime, float stressGenerated) {
        this.burnTime = burnTime;
        this.stressGenerated = stressGenerated;
        this.ingredient = Ingredient.EMPTY;
        this.group = "";
    }

    public static MotorFuelRecipeBuilder motorFuel(int burnTime, float stressGenerated) {
        return new MotorFuelRecipeBuilder(burnTime, stressGenerated);
    }

    public MotorFuelRecipeBuilder require(Ingredient ingredient) {
        this.ingredient = ingredient;
        return this;
    }

    public MotorFuelRecipeBuilder group(String group) {
        this.group = group;
        return this;
    }

    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        MotorFuelRecipe recipe = new MotorFuelRecipe(ingredient, burnTime, stressGenerated);
        recipeOutput.accept(id.withPrefix("motor_fuel/"), recipe, null);
    }

    public void save(RecipeOutput recipeOutput, String id) {
        save(recipeOutput, ResourceLocation.fromNamespaceAndPath("create_fuel_motor", id));
    }
}
