package com.reggarf.mods.create_fuel_motor;


import com.reggarf.mods.create_fuel_motor.Register.CFMBlockEntityTypes;
import com.reggarf.mods.create_fuel_motor.Register.CFMBlocks;
import com.reggarf.mods.create_fuel_motor.Register.CFMItems;
import com.reggarf.mods.create_fuel_motor.Register.CFMRecipes;

import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.mojang.text2speech.Narrator.LOGGER;

@Mod(Create_fuel_motor.MOD_ID)
public class Create_fuel_motor {

    public static final String MOD_ID = "create_fuel_motor";
    public static final CreateRegistrate BASE_REGISTRATE = CreateRegistrate.create(MOD_ID);

    private static DeferredRegister<CreativeModeTab> TAB_REGISTRAR = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    public static final RegistryObject<CreativeModeTab> tab = TAB_REGISTRAR.register("create_fuel_motor_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("item_group." + MOD_ID + ".tab"))
                    .icon(CFMBlocks.FUEL_MOTOR::asStack)
                    .build()
    );

    public static final CreateRegistrate REGISTRATE = BASE_REGISTRATE.setCreativeTab(tab);


    public static final ResourceKey<CreativeModeTab> CREATIVE_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB,
            new ResourceLocation(MOD_ID, "create_fuel_motor_tab"));



    public Create_fuel_motor() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        LOGGER.info("Hello 1.20.1 Create!");

        BASE_REGISTRATE.registerEventListeners(modBus);
        TAB_REGISTRAR.register(modBus);
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        CFMBlocks.load();
        CFMBlockEntityTypes.load();
        CFMItems.load();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CFMClientIniter::onInitializeClient);
        CFMRecipes.register(modEventBus);

    }
    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
    public static void init() {

    }
}
