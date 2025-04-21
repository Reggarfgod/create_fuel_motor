package com.reggarf.mods.create_fuel_motor;



import com.reggarf.mods.create_fuel_motor.ponder.PonderPlugin;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;


public class CFMClient {

    public static void onInitializeClient(final FMLClientSetupEvent event) {

        PonderIndex.addPlugin(new PonderPlugin());


//        ModContainer modContainer = ModList.get()
//                .getModContainerById(Create_fuel_motor.MOD_ID)
//                .orElseThrow(() -> new IllegalStateException("What the..."));
//
//        modContainer.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
//                () -> new ConfigScreenHandler.ConfigScreenFactory(
//                        (mc, previousScreen) -> new BaseConfigScreen(previousScreen, Create_fuel_motor.MOD_ID)));
//

    }

}


