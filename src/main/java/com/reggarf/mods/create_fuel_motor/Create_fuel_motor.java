package com.reggarf.mods.create_fuel_motor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.reggarf.mods.create_fuel_motor.config.CommonConfig;
import com.reggarf.mods.create_fuel_motor.datapack.DataLoaderRepositorySource;
import com.reggarf.mods.create_fuel_motor.datapack.JoinDataName;
import com.reggarf.mods.create_fuel_motor.datapack.RepoType;
import com.reggarf.mods.create_fuel_motor.registry.*;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Create_fuel_motor.MOD_ID)
public class Create_fuel_motor {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "create_fuel_motor";
    // Directly reference a slf4j logger
    //private static final Logger LOGGER = LogUtils.getLogger();
    public static final Logger LOG = LogManager.getLogger(MOD_ID);
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();

    public static Path configDir;


    public static final CreateRegistrate BASE_REGISTRATE = CreateRegistrate.create(MOD_ID);

    private static DeferredRegister<CreativeModeTab> TAB_REGISTRAR = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> tab = TAB_REGISTRAR.register("create_better_motors_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("item_group." + MOD_ID + ".tab"))
                    .icon(CFMBlocks.FUEL_MOTOR::asStack)
                    .build()
    );

    public static final CreateRegistrate REGISTRATE = BASE_REGISTRATE.setCreativeTab(tab);


    public static final ResourceKey<CreativeModeTab> CREATIVE_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "create_better_motors_tab"));



    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public Create_fuel_motor(IEventBus modEventBus, ModContainer modContainer) {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();

        LOG.info("Hello 1.20.1 Create!");
        BASE_REGISTRATE.registerEventListeners(modEventBus);
        TAB_REGISTRAR.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(CFMClientIniter::onInitializeClient);
        modEventBus.addListener(RegisterCapabilitiesEvent.class, CFMCapabilities::register);

        // Register content
        CFMBlocks.load();
        CFMBlockEntityTypes.load();
        CFMItems.load();
        CFMRecipeSerializers.register(modEventBus);
        CFMRecipeTypes.register(modEventBus);


        // Load config
        modContainer.registerConfig(
                ModConfig.Type.COMMON,
                CommonConfig.COMMON_CONFIG,
                "Create Fuel Motor/create_fuel_motor-common.toml"
        );

        // Register client initializer and general setup
        modEventBus.addListener(this::generalSetup);


        // Register Forge event handlers
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(CFMMHandler.class);
        NeoForge.EVENT_BUS.register(JoinDataName.class);
        configDir = FMLPaths.CONFIGDIR.get().resolve("Create Fuel Motor");
        modEventBus.addListener(this::PackRepositories);

    }
    private void PackRepositories(AddPackFindersEvent event) {

        switch (event.getPackType()) {

            case SERVER_DATA -> {

                event.addRepositorySource(new DataLoaderRepositorySource(RepoType.DATA,configDir));
            }

            default -> Create_fuel_motor.LOG.warn("Encountered unknown pack type {}. Nothing will be loaded for this type.", event.getPackType().name());
        }
    }
    private void generalSetup(final FMLCommonSetupEvent event) {
    }
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }

}
