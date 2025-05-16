package com.reggarf.mods.create_fuel_motor.datapack;


import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;


public class JoinDataName {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        Path datapackFolder = FMLPaths.GAMEDIR.get()
                .resolve("create_fuel_motor")
                .resolve("data");

        File folder = datapackFolder.toFile();

        if (!folder.exists() || !folder.isDirectory()) {
            return;
        }

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (!isValidDataPack(file.toPath())) continue;

            String name = file.getName();

            player.sendSystemMessage(
                    Component.literal("Loaded: ")
                            .withStyle(ChatFormatting.GOLD)
                            .append(Component.literal(name).withStyle(ChatFormatting.GREEN))
            );
        }
    }

    private static boolean isValidDataPack(Path path) {
        try {
            if (Files.isRegularFile(path) && path.toString().endsWith(".zip")) {
                try (var fs = FileSystems.newFileSystem(path)) {
                    return Files.isDirectory(fs.getPath("data")) && Files.isRegularFile(fs.getPath("pack.mcmeta"));
                }
            } else if (Files.isDirectory(path)) {
                return Files.isDirectory(path.resolve("data")) && Files.isRegularFile(path.resolve("pack.mcmeta"));
            }
        } catch (Exception ignored) {}
        return false;
    }
}
