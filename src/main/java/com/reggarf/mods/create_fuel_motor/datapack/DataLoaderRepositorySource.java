package com.reggarf.mods.create_fuel_motor.datapack;

import com.reggarf.mods.create_fuel_motor.Create_fuel_motor;
import com.reggarf.mods.create_fuel_motor.config.CommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.neoforged.fml.loading.FMLPaths;

import java.io.File;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class DataLoaderRepositorySource implements RepositorySource {

    private final RepoType type;
    private final List<File> directories;
    private final PackSource sourceInfo;

    public DataLoaderRepositorySource(RepoType type, Path configDir) {
        this.type = type;

        this.sourceInfo = PackSource.create(
                name -> Component.translatable("pack.nameAndSource", name,
                        Component.translatable("pack.source.dataloader")).withStyle(ChatFormatting.GREEN), true);

        Path gameDir = FMLPaths.GAMEDIR.get();
        Path customFolder = gameDir.resolve("create_fuel_motor").resolve(type.getPath());
        this.directories = List.of(customFolder.toFile());

        for (File dir : directories) {
            if (!dir.exists() && dir.mkdirs()) {
                Create_fuel_motor.LOG.info("Created {} folder at {}", type.displayName, dir);
            } else if (!dir.isDirectory()) {
                throw new IllegalStateException("Invalid " + type.displayName + " folder: " + dir);
            }

            // Copy example zip
            File exampleFile = new File(dir, "example_data.zip");
            if (!exampleFile.exists()) {
                try (InputStream in = getClass().getClassLoader().getResourceAsStream("example_data.zip")) {
                    if (in != null) {
                        Files.copy(in, exampleFile.toPath());
                        Create_fuel_motor.LOG.info("Copied example_data.zip to {}", exampleFile.getAbsolutePath());
                    } else {
                        Create_fuel_motor.LOG.warn("example_data.zip not found in resources!");
                    }
                } catch (Exception e) {
                    Create_fuel_motor.LOG.error("Failed to copy example_data.zip", e);
                }
            }

            // Create README.txt
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
                    Files.writeString(readme.toPath(), content);
                    Create_fuel_motor.LOG.info("Generated README.txt at {}", readme.getAbsolutePath());
                } catch (Exception e) {
                    Create_fuel_motor.LOG.error("Failed to generate README.txt", e);
                }
            }
        }
    }

    @Override
    public void loadPacks(Consumer<Pack> consumer) {
        if (!CommonConfig.data_Enabled.get()) {
            Create_fuel_motor.LOG.info("Skipping {}. Disabled by config.", type.displayName);
            return;
        }

        Create_fuel_motor.LOG.info("Preparing {} injection...", type.displayName);

        for (File dir : directories) {
            File[] files = Optional.ofNullable(dir.listFiles()).orElse(new File[0]);
            int loaded = 0, failed = 0;

            for (File file : files) {
                Path path = file.toPath();

                if (!isValidDataPack(path)) {
                    logInvalid(file);
                    failed++;
                    continue;
                }

                Pack.ResourcesSupplier supplier = file.isDirectory()
                        ? new PathPackResources.PathResourcesSupplier(path)
                        : createFilePack(path);

                if (supplier == null) {
                    logInvalid(file);
                    failed++;
                    continue;
                }

                Pack pack = Pack.readMetaAndCreate(
                        new PackLocationInfo(
                                file.getName(),
                                Component.literal(file.getName()).withStyle(ChatFormatting.GREEN),
                                sourceInfo,
                                Optional.empty()
                        ),
                        supplier,
                        type.getPackType(),
                        new PackSelectionConfig(false, Pack.Position.TOP, true)
                );

                if (pack != null) {
                    consumer.accept(pack);
                    loaded++;
                    Create_fuel_motor.LOG.info("Loaded {} pack: {}", type.getName(), file.getName());
                } else {
                    logInvalid(file);
                    failed++;
                }
            }

            Create_fuel_motor.LOG.info("Injected {}/{} packs from {}", loaded, loaded + failed, dir);
        }
    }

    private Pack.ResourcesSupplier createFilePack(Path path) {
        try {
            FileSystem fs = FileSystems.newFileSystem(path);
            if (!Files.isDirectory(fs.getPath("data")) || !Files.isRegularFile(fs.getPath("pack.mcmeta"))) {
                return null;
            }
            return new FilePackResources.FileResourcesSupplier(path);
        } catch (Exception e) {
            Create_fuel_motor.LOG.warn("Failed to open pack file system: {}", path, e);
            return null;
        }
    }

    private boolean isValidDataPack(Path pack) {
        if (Files.isSymbolicLink(pack)) return false;

        try {
            if (Files.isRegularFile(pack) && pack.toString().endsWith(".zip")) {
                try (FileSystem fs = FileSystems.newFileSystem(pack)) {
                    return Files.isDirectory(fs.getPath("data")) && Files.isRegularFile(fs.getPath("pack.mcmeta"));
                }
            } else {
                return Files.isDirectory(pack.resolve("data")) && Files.isRegularFile(pack.resolve("pack.mcmeta"));
            }
        } catch (Exception e) {
            return false;
        }
    }

    private void logInvalid(File file) {
        Create_fuel_motor.LOG.warn("Skipped invalid {} pack: {}", type.getName(), file.getName());
    }
}
