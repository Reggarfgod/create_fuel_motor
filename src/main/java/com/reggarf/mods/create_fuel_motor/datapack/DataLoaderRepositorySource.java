package com.reggarf.mods.create_fuel_motor.datapack;

import com.reggarf.mods.create_fuel_motor.Create_fuel_motor;
import com.reggarf.mods.create_fuel_motor.config.CommonConfig;
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
    private final PackSource sourceInfo;

    public DataLoaderRepositorySource(RepoType type, Path configDir) {
        this.type = type;
        this.sourceInfo = PackSource.create(
                name -> Component.translatable("pack.nameAndSource", name,
                        Component.translatable("pack.source.dataloader")).withStyle(ChatFormatting.GREEN), true);


        Path gameDir = net.minecraftforge.fml.loading.FMLPaths.GAMEDIR.get();
        Path customFolder = gameDir.resolve("create_fuel_motor").resolve(type.getPath());
        this.directories = List.of(customFolder.toFile());

        directories.forEach(dir -> {
            if (!dir.exists() && dir.mkdirs()) {
                Create_fuel_motor.LOG.info("Created {} folder at {}", type.displayName, dir);
            } else if (!dir.isDirectory()) {
                throw new IllegalStateException("Invalid " + type.displayName + " folder: " + dir);
            }

//            // === Copy example_data.zip if it doesn't exist ===
//            File exampleFile = new File(dir, "example_data.zip");
//            if (!exampleFile.exists()) {
//                try (var in = getClass().getClassLoader().getResourceAsStream("example_data.zip")) {
//                    if (in == null) {
//                        Create_fuel_motor.LOG.error("example_data.zip not found in resources!");
//                    } else {
//                        java.nio.file.Files.copy(in, exampleFile.toPath());
//                        Create_fuel_motor.LOG.info("Copied example_data.zip to {}", exampleFile.getAbsolutePath());
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


        });


    }

    @Override
    public void loadPacks(Consumer<Pack> consumer) {
        if (!CommonConfig.data_Enabled.get()) {
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