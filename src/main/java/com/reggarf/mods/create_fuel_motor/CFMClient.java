package com.reggarf.mods.create_fuel_motor;

import com.reggarf.mods.create_fuel_motor.ponder.PonderPlugin;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;


@EventBusSubscriber(modid = Create_fuel_motor.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CFMClient {

    @SubscribeEvent
    public static void onInitializeClient(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // Register Ponder plugin
            PonderIndex.addPlugin(new PonderPlugin());

            // Register config screen if using catnip
            ModLoadingContext.get().registerExtensionPoint(
                   IConfigScreenFactory.class,
                    () -> (mc, previous) -> new BaseConfigScreen(previous, Create_fuel_motor.MOD_ID)
            );
        });
    }
}
