package com.reggarf.mods.create_fuel_motor.ponder;




import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

import static com.reggarf.mods.create_fuel_motor.Create_fuel_motor.MOD_ID;

public class PonderPlugin implements net.createmod.ponder.api.registration.PonderPlugin {
    @Override
    public String getModId() {
        return MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderIndex.register(helper);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTags.register(helper);
    }
}
