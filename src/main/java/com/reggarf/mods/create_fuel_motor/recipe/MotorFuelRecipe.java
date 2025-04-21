package com.reggarf.mods.create_fuel_motor.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import static com.reggarf.mods.create_fuel_motor.recipe.MotorFuelRecipeType.ID;

public class MotorFuelRecipe implements Recipe<Container> {
    public static final Codec<MotorFuelRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(r -> r.ingredient),
            Codec.INT.fieldOf("burn_time").forGetter(r -> r.burnTime),
            Codec.INT.fieldOf("stress").forGetter(r -> r.stress)
    ).apply(instance, MotorFuelRecipe::new));

    private final Ingredient ingredient;
    private final int burnTime;
    private final int stress;

    public MotorFuelRecipe(Ingredient ingredient, int burnTime, int stress) {
        this.ingredient = ingredient;
        this.burnTime = burnTime;
        this.stress = stress;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public int getStress() {
        return stress;
    }

    @Override
    public boolean matches(Container inv, Level world) {
        return ingredient.test(inv.getItem(0));
    }

    @Override
    public ItemStack assemble(Container inv, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.MOTOR_FUEL.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.MOTOR_FUEL.get();
    }
}
