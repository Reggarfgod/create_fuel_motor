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

//            // Copy example zip
//            File exampleFile = new File(dir, "example_data.zip");
//            if (!exampleFile.exists()) {
//                try (InputStream in = getClass().getClassLoader().getResourceAsStream("example_data.zip")) {
//                    if (in != null) {
//                        Files.copy(in, exampleFile.toPath());
//                        Create_fuel_motor.LOG.info("Copied example_data.zip to {}", exampleFile.getAbsolutePath());
//                    } else {
//                        Create_fuel_motor.LOG.warn("example_data.zip not found in resources!");
//                    }
//                } catch (Exception e) {
//                    Create_fuel_motor.LOG.error("Failed to copy example_data.zip", e);
//                }
//            }

            // === Copy example_data folder and its contents if it doesn't exist ===
            File targetFolder = new File(dir, "example_data");
            if (!targetFolder.exists()) {
                try {
                    var url = getClass().getClassLoader().getResource("example_data");
                    if (url == null) {
                        Create_fuel_motor.LOG.error("example_data folder not found in resources!");
                    } else if (url.getProtocol().equals("jar")) {
                        // From inside a JAR
                        String pathInJar = "example_data/";
                        String jarPath = url.getPath().substring(5, url.getPath().indexOf("!"));
                        try (java.util.jar.JarFile jar = new java.util.jar.JarFile(jarPath)) {
                            java.util.Enumeration<java.util.jar.JarEntry> entries = jar.entries();
                            while (entries.hasMoreElements()) {
                                java.util.jar.JarEntry entry = entries.nextElement();
                                String name = entry.getName();
                                if (name.startsWith(pathInJar)) {
                                    String relativePath = name.substring(pathInJar.length());
                                    File outFile = new File(targetFolder, relativePath);
                                    if (entry.isDirectory()) {
                                        outFile.mkdirs();
                                    } else {
                                        outFile.getParentFile().mkdirs();
                                        try (var in = jar.getInputStream(entry)) {
                                            java.nio.file.Files.copy(in, outFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                                        }
                                    }
                                }
                            }
                            Create_fuel_motor.LOG.info("Copied example_data folder to {}", targetFolder.getAbsolutePath());
                        }
                    } else {
                        // From filesystem (e.g. in dev environment)
                        java.nio.file.Path sourcePath = java.nio.file.Paths.get(url.toURI());
                        java.nio.file.Files.walk(sourcePath).forEach(source -> {
                            try {
                                java.nio.file.Path dest = java.nio.file.Paths.get(targetFolder.getAbsolutePath(), sourcePath.relativize(source).toString());
                                if (java.nio.file.Files.isDirectory(source)) {
                                    java.nio.file.Files.createDirectories(dest);
                                } else {
                                    java.nio.file.Files.copy(source, dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                                }
                            } catch (Exception e) {
                                Create_fuel_motor.LOG.error("Failed copying file from example_data: {}", source, e);
                            }
                        });
                        Create_fuel_motor.LOG.info("Copied example_data folder to {}", targetFolder.getAbsolutePath());
                    }
                } catch (Exception e) {
                    Create_fuel_motor.LOG.error("Failed to copy example_data folder", e);
                }
            }


            // === Create README.txt with instructions ===
            File readme = new File(dir, "README.txt");
            if (!readme.exists()) {
                try {
                    String content = """
                         
                                                                    This folder is used by the **Create Fuel Motor** mod to load custom `%s` data, such as motor fuel recipes. These data  allow you to extend and customize the mod's behavior without modifying its core files.
                            
                                                                    ================================================================================
                            
                                                                    📦 You can use the provided `example_data` folder as a reference to create your own custom data . It contains working examples of valid recipe formats and pack structures.
                            
                                                                    ================================================================================
                            
                                                                    ✅ How to Use:
                            
                                                                    1. **Create Your Own Data**
                            
                                                                       * Prepare a data  folder that includes your custom motor fuel recipes.
                                                                       * Follow the same folder and file structure as shown in the `example_data` folder.
                                                                       * Make sure to include a valid `pack.mcmeta` file at the root of your data. Without it, the game will not recognize your pack.
                            
                                                                    2. **Supported Formats**
                            
                                                                       * Raw folders (uncompressed)
                                                                       * Compressed `.zip` files
                                                                       * `.jar` files (structured like a data pack)
                            
                                                                    3. **Installation**
                            
                                                                       * Place your custom data  (folder, .zip, or .jar) inside this directory. The mod will automatically detect and load it when Minecraft starts or when data is reloaded.
                            
                                                                    ================================================================================
                            
                                                                    ⚠️ Important Notes:
                            
                                                                    * **Do NOT delete** the `example_data` folder. It is provided as a reference and fallback. Keeping it ensures that the mod always has a working example to fall back on.
                                                                    * Always validate your custom data packs before use to avoid crashes or loading errors.
                                                                    * If something goes wrong, double-check your file structure, JSON formatting, and the presence of a valid `pack.mcmeta` file.
                            
                                                                    ================================================================================
                            
                                                                    For more information on recipe structure and expected data formats, refer to the files inside the `example_data` folder. These examples demonstrate the correct way to define custom motor fuel recipes for the mod.
                            
                                                                    ================================================================================
                            
                                                                    🛠️ Mod developed by Reggarf
                                  
                """.formatted(type.displayName.toLowerCase());
                    java.nio.file.Files.writeString(readme.toPath(), content);
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
