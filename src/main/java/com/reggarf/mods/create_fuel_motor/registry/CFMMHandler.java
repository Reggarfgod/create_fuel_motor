package com.reggarf.mods.create_fuel_motor.registry;



import com.reggarf.mods.create_fuel_motor.config.CommonConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;


import static net.minecraft.network.chat.TextColor.fromRgb;


@EventBusSubscriber
public class CFMMHandler {

    public static String titleColor = "DDA0FF";
    public static String zapColor = "00FFFF";
    public static String discordColor = "5599FF";
    public static String hostingColor = "00FFAA";
    public static String disableColor = "00FF66";
    public static String githubColor = "A9A9A9";

//    @SubscribeEvent
//    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
//        if (!(event.getEntity() instanceof ServerPlayer player) || !enabled) return;
//
//        CompoundTag persistentData = player.getPersistentData();
//        CompoundTag forgeData = persistentData.getCompound(ServerPlayer.PERSISTED_NBT_TAG);
//
//
//        if (!forgeData.getBoolean("create_hard_mod_hasJoinedBefore")) {
//            sendStyledMessages(player); // Send welcome + links
//            forgeData.putBoolean("create_hard_mod_hasJoinedBefore", true);
//            persistentData.put(ServerPlayer.PERSISTED_NBT_TAG, forgeData);
//        }
//    }
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        // Move config check here to ensure it's accessed after config is loaded
        if (!CommonConfig.message_Enabled.get()) return;

        CompoundTag persistentData = player.getPersistentData();
        CompoundTag forgeData = persistentData.getCompound(ServerPlayer.PERSISTED_NBT_TAG);

        if (!forgeData.getBoolean("create_hard_mod_hasJoinedBefore")) {
            sendStyledMessages(player); // Send welcome + links
            forgeData.putBoolean("create_hard_mod_hasJoinedBefore", true);
            persistentData.put(ServerPlayer.PERSISTED_NBT_TAG, forgeData);
        }
    }


    private static void sendStyledMessages(ServerPlayer player) {

        Component title = Component.literal("Hello, thank you for downloading ")
                .append(Component.literal("Create: Fuel Motor")
                        .setStyle(Style.EMPTY.withColor(parseTextColor(titleColor))))
                .append(Component.literal(". The mod is still in beta, if you find any bugs, please report them on "))
                .append(Component.literal("GitHub.")
                        .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x3498db)).withUnderlined(true)));

        Component blankLine = Component.literal("");


        Component discord = Component.literal(" - ")
                .append(Component.literal("Join our Discord ")
                        .setStyle(Style.EMPTY
                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.com/invite/CN962KMpJk"))
                                .withColor(parseTextColor(discordColor))
                                .withUnderlined(true)))
                .append(Component.literal(" (support, updates)"));


        Component zap = Component.literal(" - ")
                .append(Component.literal("ZAP-Hosting ")
                        .setStyle(Style.EMPTY
                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://zap-hosting.com/reggarf"))
                                .withColor(parseTextColor(zapColor))
                                .withUnderlined(true)))
                .append(Component.literal(" (20% off with code Reggarf-1047)"));



        Component disable = Component.literal(" - ")
                .append(Component.literal("Disable this message")
                        .setStyle(Style.EMPTY
                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://curseforge.com/minecraft/mc-mods/create-fuel-motor"))
                                .withColor(parseTextColor(disableColor))
                                .withUnderlined(true)))
                .append(Component.literal(" (Mod config)"));

        Component issueTracker = Component.literal(" - ")
                .append(Component.literal("Issue Tracker")
                        .setStyle(Style.EMPTY
                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/Reggarfgod/create_fuel_motor/issues"))
                                .withColor(parseTextColor(githubColor))
                                .withUnderlined(true)))
                .append(Component.literal(" (github/wiki)"));


        player.sendSystemMessage(title);
        player.sendSystemMessage(blankLine);
        player.sendSystemMessage(discord);
        player.sendSystemMessage(zap);
        player.sendSystemMessage(issueTracker);
        player.sendSystemMessage(disable);

    }

    private static TextColor parseTextColor(String hex) {
        try {
            if (hex.startsWith("#")) hex = hex.substring(1);
            int rgb = Integer.parseInt(hex, 16);
            return fromRgb(rgb);
        } catch (NumberFormatException e) {
            System.err.println("Invalid color format: " + hex);
            return fromRgb(0xFFFFFF); // Default to white
        }
    }
}
