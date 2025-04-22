package com.reggarf.mods.create_fuel_motor.datagen;


import com.reggarf.mods.create_fuel_motor.Create_fuel_motor;
import com.reggarf.mods.create_fuel_motor.datagen.provider.MotorFuelRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = Create_fuel_motor.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CFMDataGen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        System.out.println("gatherData HERE");
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        event.getGenerator().addProvider(true, new MotorFuelRecipeProvider(event.getGenerator().getPackOutput()));



    }
}
