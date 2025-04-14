package com.reggarf.mods.create_hard_mod.recipes;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;


public interface RecipeType<T extends Recipe<?>> {

    net.minecraft.world.item.crafting.RecipeType<SmeltingRecipe> SMELTING = register("smelting");


    static <T extends Recipe<?>> net.minecraft.world.item.crafting.RecipeType<T> register(final String p_44120_) {
        return Registry.register(BuiltInRegistries.RECIPE_TYPE, new ResourceLocation(p_44120_), new net.minecraft.world.item.crafting.RecipeType<T>() {
            public String toString() {
                return p_44120_;
            }
        });
    }

    public static <T extends Recipe<?>> net.minecraft.world.item.crafting.RecipeType<T> simple(final ResourceLocation name) {
        final String toString = name.toString();
        return new net.minecraft.world.item.crafting.RecipeType<T>() {
            @Override
            public String toString() {
                return toString;
            }
        };
    }
}
