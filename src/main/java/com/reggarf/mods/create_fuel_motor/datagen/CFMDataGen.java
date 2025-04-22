package com.reggarf.mods.create_fuel_motor.datagen;


import com.reggarf.mods.create_fuel_motor.Create_fuel_motor;
import com.reggarf.mods.create_fuel_motor.datagen.provider.MotorFuelRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Create_fuel_motor.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class CFMDataGen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        System.out.println("gatherData HERE");
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeServer(), new MotorFuelRecipeProvider(output,lookupProvider));


    }
}
