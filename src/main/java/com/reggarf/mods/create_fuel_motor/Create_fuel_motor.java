package com.reggarf.mods.create_fuel_motor;

import com.reggarf.mods.create_fuel_motor.config.CFMConfig;
import com.reggarf.mods.create_fuel_motor.registry.*;
import com.simibubi.create.foundation.data.CreateRegistrate;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Create_fuel_motor.MOD_ID)
public class Create_fuel_motor {

    public static final String MOD_ID = "create_fuel_motor";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final CreateRegistrate BASE_REGISTRATE = CreateRegistrate.create(MOD_ID);

    // Creative tab
    private static final DeferredRegister<CreativeModeTab> TAB_REGISTRAR =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final RegistryObject<CreativeModeTab> TAB = TAB_REGISTRAR.register("create_fuel_motor_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("item_group." + MOD_ID + ".tab"))
                    .icon(CFMBlocks.FUEL_MOTOR::asStack)
                    .build());

    public static final CreateRegistrate REGISTRATE = BASE_REGISTRATE.setCreativeTab(TAB);

    public static final ResourceKey<CreativeModeTab> CREATIVE_TAB_KEY =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(MOD_ID, "create_fuel_motor_tab"));

    public Create_fuel_motor() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        LOGGER.info("Initializing Create: Fuel Motor mod.");

        // Register creative tab
        TAB_REGISTRAR.register(modEventBus);

        // Register Create's registrate
        BASE_REGISTRATE.registerEventListeners(modEventBus);
        REGISTRATE.setCreativeTab(TAB);

        // Register content
        CFMBlocks.load();
        CFMBlockEntityTypes.load();
        CFMItems.load();
        CFMRecipeSerializers.register(modEventBus);
        CFMRecipeTypes.register(modEventBus);

        // Load config
        CFMConfig.getCommon();

        // Register client initializer and general setup
        modEventBus.addListener(CFMClientIniter::onInitializeClient);
        modEventBus.addListener(this::generalSetup);

        // Register Forge event handlers
        forgeEventBus.register(CFMMHandler.class);
    }

    private void generalSetup(final FMLCommonSetupEvent event) {
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
