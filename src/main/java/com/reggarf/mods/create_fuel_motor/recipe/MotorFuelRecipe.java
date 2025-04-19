package com.reggarf.mods.create_fuel_motor.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class MotorFuelRecipe implements Recipe<SimpleContainer> {
    private final Ingredient ingredient;
    private final int burnTime;
    private final float stressGenerated;
    private final ResourceLocation id;

    public MotorFuelRecipe(Ingredient ingredient, int burnTime, float stressGenerated, ResourceLocation id) {
        this.ingredient = ingredient;
        this.burnTime = burnTime;
        this.stressGenerated = stressGenerated;
        this.id = id;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public float getStressGenerated() {
        return stressGenerated;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        return ingredient.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MotorFuelRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return MotorFuelRecipeType.INSTANCE;
    }
}
