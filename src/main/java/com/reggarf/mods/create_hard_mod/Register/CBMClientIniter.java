package com.reggarf.mods.create_hard_mod.Register;



import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class CBMClientIniter {
    public static void onInitializeClient(final FMLClientSetupEvent event) {
        CBMClient.onInitializeClient(event);
    }
}
