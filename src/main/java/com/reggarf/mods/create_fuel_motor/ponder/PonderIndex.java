package com.reggarf.mods.create_fuel_motor.ponder;





import com.reggarf.mods.create_fuel_motor.registry.CFMBlocks;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;




public class PonderIndex {

    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemProviderEntry<?,?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        //Motor
        HELPER.forComponents(CFMBlocks.FUEL_MOTOR)
                .addStoryBoard("motor", PonderScenes::fuelMotor, AllCreatePonderTags.KINETIC_SOURCES, PonderTags.FUEL_MOTOR)
                .addStoryBoard("motor_in", PonderScenes::inMotor, AllCreatePonderTags.KINETIC_SOURCES, PonderTags.FUEL_MOTOR);

    }
}
