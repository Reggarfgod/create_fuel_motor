package com.reggarf.mods.create_fuel_motor.datapack;

import com.reggarf.mods.create_fuel_motor.Create_fuel_motor;
import com.reggarf.mods.create_fuel_motor.config.DataConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public class DataLoaderRepositorySource implements RepositorySource {

    private final RepoType type;
    private final List<File> directories;
    private final DataConfig.PackConfig config;
    private final PackSource sourceInfo;

    public DataLoaderRepositorySource(RepoType type, DataConfig globalConfig, DataConfig.PackConfig config, Path configDir) {
        this.type = type;
        this.config = config;
        this.sourceInfo = PackSource.create(
               // name -> globalConfig.displaySourceName
                name -> true
                        ? Component.translatable("pack.nameAndSource", name, Component.translatable("pack.source.openloader")).withStyle(ChatFormatting.GREEN)
                        : name, true);

        //this.directories = List.of(configDir.resolve(type.getPath()).toFile());
        Path gameDir = net.minecraftforge.fml.loading.FMLPaths.GAMEDIR.get(); // Minecraft root directory
        Path customFolder = gameDir.resolve("create_fuel_motor").resolve(type.getPath());
        this.directories = List.of(customFolder.toFile());

//        directories.forEach(dir -> {
//            if (!dir.exists() && dir.mkdirs()) {
//                Create_fuel_motor.LOG.info("Created {} folder at {}", type.displayName, dir);
//            } else if (!dir.isDirectory()) {
//                throw new IllegalStateException("Invalid " + type.displayName + " folder: " + dir);
//            }
//        });
        directories.forEach(dir -> {
            if (!dir.exists() && dir.mkdirs()) {
                Create_fuel_motor.LOG.info("Created {} folder at {}", type.displayName, dir);
            } else if (!dir.isDirectory()) {
                throw new IllegalStateException("Invalid " + type.displayName + " folder: " + dir);
            }

            // === Copy example_data.zip if it doesn't exist ===
            File exampleFile = new File(dir, "example_data.zip");
            if (!exampleFile.exists()) {
                try (var in = getClass().getClassLoader().getResourceAsStream("example_data.zip")) {
                    if (in == null) {
                        Create_fuel_motor.LOG.error("example_data.zip not found in resources!");
                    } else {
                        java.nio.file.Files.copy(in, exampleFile.toPath());
                        Create_fuel_motor.LOG.info("Copied example_data.zip to {}", exampleFile.getAbsolutePath());
                    }
                } catch (Exception e) {
                    Create_fuel_motor.LOG.error("Failed to copy example_data.zip", e);
                }
            }

            // === Create README.txt with instructions ===
            File readme = new File(dir, "README.txt");
            if (!readme.exists()) {
                try {
                    String content = """
                This folder is used by the Create Fuel Motor mod to load custom %s packs.

                📦 You can use the provided 'example_data.zip' file as a reference to create your own custom recipes.

                ✅ How to use:
                - Extract or place your own folder or .zip/.jar file here.
                - Ensure your pack contains a valid 'pack.mcmeta' file.
                - Supported formats: folders, .zip, .jar.

                ⚠️ Do NOT delete the 'example_data.zip' file.
                ➤ Create your own data by taking help from the contents of 'example_data.zip'.

                For recipe structure and format examples, refer to the contents inside 'example_data.zip'.

                Mod by Reggarf.
                """.formatted(type.displayName.toLowerCase());
                    java.nio.file.Files.writeString(readme.toPath(), content);
                    Create_fuel_motor.LOG.info("Generated README.txt at {}", readme.getAbsolutePath());
                } catch (Exception e) {
                    Create_fuel_motor.LOG.error("Failed to generate README.txt", e);
                }
            }


        });


    }

    @Override
    public void loadPacks(Consumer<Pack> consumer) {
        if (!config.enabled) {
            Create_fuel_motor.LOG.info("Skipping {}. Disabled by config.", type.displayName);
            return;
        }

        Create_fuel_motor.LOG.info("Preparing {} injection.", type.displayName);

        for (File dir : directories) {
            File[] files = Optional.ofNullable(dir.listFiles()).orElse(new File[0]);
            int[] stats = new int[2]; // [loaded, failed]

            for (File candidate : files) {
                String kind = getPackKind(candidate);
                if (!kind.equals("invalid")) {
                    String packName = type.getPath() + "/" + candidate.getName();
                    Pack pack = Pack.readMetaAndCreate(packName, Component.literal(packName), true,
                            createPackSupplier(candidate), type.getPackType(), Pack.Position.TOP, sourceInfo);
                    if (pack != null) {
                        consumer.accept(pack);
                        stats[0]++;
                        Create_fuel_motor.LOG.info("Loaded {} {} from {}", kind, type.getName(), candidate);
                    }
                } else {
                    logInvalidPack(candidate);
                    stats[1]++;
                }
            }

            Create_fuel_motor.LOG.info("Injected {}/{} packs from {}", stats[0], stats[0] + stats[1], dir);
        }
    }

    private String getPackKind(File file) {
        if (isArchivePack(file, false)) return "archive";
        if (isFolderPack(file, false)) return "folder";
        return "invalid";
    }

    private Pack.ResourcesSupplier createPackSupplier(File file) {
        return name -> file.isDirectory()
                ? new PathPackResources(name, file.toPath(), false)
                : new FilePackResources(name, file, false);
    }

    private void logInvalidPack(File file) {
        Create_fuel_motor.LOG.error("Skipping {}. Invalid {}!", file.getAbsolutePath(), type.getName());
        isArchivePack(file, true);
        isFolderPack(file, true);
    }

    private boolean isArchivePack(File file, boolean log) {
        if (file.isFile()) {
            boolean valid = file.getName().endsWith(".zip") || file.getName().endsWith(".jar");
            if (!valid && log) Create_fuel_motor.LOG.warn("Invalid archive: {}", file);
            return valid;
        }
        if (log) Create_fuel_motor.LOG.warn("Archive not a file: {}", file);
        return false;
    }

    private boolean isFolderPack(File file, boolean log) {
        if (file.isDirectory()) {
            boolean hasMeta = new File(file, "pack.mcmeta").isFile();
            if (!hasMeta && log) Create_fuel_motor.LOG.warn("Missing pack.mcmeta in folder: {}", file);
            return hasMeta;
        }
        if (log) Create_fuel_motor.LOG.warn("Folder not a directory: {}", file);
        return false;
    }
}