package com.reggarf.mods.create_fuel_motor.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

public class MotorFuelRecipe implements Recipe<RecipeWrapper> {
    protected final Ingredient ingredient;
    protected final int burnTime;
    protected final float stressGenerated;

    public MotorFuelRecipe(Ingredient ingredient, int burnTime, float stressGenerated) {
        this.ingredient = ingredient;
        this.burnTime = burnTime;
        this.stressGenerated = stressGenerated;
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
    public boolean matches(RecipeWrapper inv, Level level) {
        if (inv.isEmpty()) return false;
        return ingredient.test(inv.getItem(0));
    }

    @Override
    public ItemStack assemble(RecipeWrapper inv, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    // Removed getId()

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return MotorFuelRecipeType.INSTANCE;
    }

    public static class Serializer implements RecipeSerializer<MotorFuelRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("create_fuel_motor", "motor_fuel");

        public static final MapCodec<MotorFuelRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(r -> r.ingredient),
                Codec.INT.fieldOf("burn_time").forGetter(r -> r.burnTime),
                Codec.FLOAT.fieldOf("stress").forGetter(r -> r.stressGenerated)
        ).apply(instance, MotorFuelRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, MotorFuelRecipe> STREAM_CODEC = StreamCodec.of(
                MotorFuelRecipe.Serializer::toNetwork, MotorFuelRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<MotorFuelRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MotorFuelRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static MotorFuelRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            int burnTime = buffer.readInt();
            float stress = buffer.readFloat();
            return new MotorFuelRecipe(input, burnTime, stress);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, MotorFuelRecipe recipe) {
            recipe.ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.ingredient);
            buffer.writeInt(recipe.burnTime);
            buffer.writeFloat(recipe.stressGenerated);
        }
    }
}
