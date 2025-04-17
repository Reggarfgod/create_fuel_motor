package com.reggarf.mods.create_fuel_motor;



import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class CFMClient {

    public static void onInitializeClient(final FMLClientSetupEvent event) {
        ModContainer modContainer = ModList.get()
                .getModContainerById(Create_fuel_motor.MOD_ID)
                .orElseThrow(() -> new IllegalStateException("What the..."));

        modContainer.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (mc, previousScreen) -> new BaseConfigScreen(previousScreen, Create_fuel_motor.MOD_ID)));
    }

}


