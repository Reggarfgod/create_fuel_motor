package com.reggarf.mods.create_hard_mod.recipe;



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
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public static class Type implements RecipeType<MotorFuelRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "motor_fuel";
    }

    public static class Serializer implements RecipeSerializer<MotorFuelRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation("create_hard_mod", "motor_fuel");

        @Override
        public MotorFuelRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));
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
            recipe.ingredient.toNetwork(buffer);
            buffer.writeInt(recipe.getBurnTime());
            buffer.writeFloat(recipe.getStressGenerated());
        }
    }
}
