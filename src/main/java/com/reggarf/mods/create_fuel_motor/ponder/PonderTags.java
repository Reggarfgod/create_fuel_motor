package com.reggarf.mods.create_fuel_motor.ponder;



import com.reggarf.mods.create_fuel_motor.Create_fuel_motor;
import com.reggarf.mods.create_fuel_motor.registry.CFMBlocks;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;



public class PonderTags {


    public static final ResourceLocation FUEL_MOTOR = Create_fuel_motor.asResource("fuel");

    private static ResourceLocation loc(String id) {
        return Create_fuel_motor.asResource(id);
    }


    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<RegistryEntry<?,?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);
        PonderTagRegistrationHelper<ItemLike> itemHelper = helper.withKeyFunction(
                RegisteredObjectsHelper::getKeyOrThrow);

/// //////////////////////////////////////////////////////////////

        HELPER.registerTag(FUEL_MOTOR)
                .addToIndex()
                .item(CFMBlocks.FUEL_MOTOR.get(), true, false)
                .title("Fuel Blocks")
                .description("Components which use electricity")
                .register();
/// //////////////////////////////////////////////////////////////////

        HELPER.addToTag(AllCreatePonderTags.KINETIC_SOURCES)
                .add(CFMBlocks.FUEL_MOTOR);

     ///////////////////////////////////////////////////////////////
        HELPER.addToTag(FUEL_MOTOR)
                .add(CFMBlocks.FUEL_MOTOR);;

        }

    }

