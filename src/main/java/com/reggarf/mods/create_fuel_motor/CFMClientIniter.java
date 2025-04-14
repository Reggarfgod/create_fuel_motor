package com.reggarf.mods.create_fuel_motor;



import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class CFMClientIniter {
    public static void onInitializeClient(final FMLClientSetupEvent event) {
        CFMClient.onInitializeClient(event);
    }
}
