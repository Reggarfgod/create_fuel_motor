package com.reggarf.mods.create_fuel_motor.config;

import com.google.gson.annotations.Expose;
import com.reggarf.mods.create_fuel_motor.Create_fuel_motor;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class DataConfig {

    @Expose
    public PackConfig dataPacks = new PackConfig();


    public static class PackConfig {
        @Expose
        public boolean enabled = true;

    }

    public static DataConfig load(Path configDir) {

        // RepoType.DATA.createDirectory(configDir);

        final File configFile = configDir.resolve("create_fuel_motor-data.toml").toFile();
        final DataConfig defaultConfig = new DataConfig();
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                return Create_fuel_motor.GSON.fromJson(reader, DataConfig.class);
            }
            catch (IOException e) {
                Create_fuel_motor.LOG.error("Could not read config file {}. Defaults will be used.", configFile.getAbsolutePath());
                Create_fuel_motor.LOG.catching(e);
            }
        } else {
            Create_fuel_motor.LOG.info("Creating a new config file at {}.", configFile.getAbsolutePath());
            configFile.getParentFile().mkdirs();
        }
        try (FileWriter writer = new FileWriter(configFile)) {
            Create_fuel_motor.GSON.toJson(defaultConfig, writer);
            Create_fuel_motor.LOG.info("Default configuration file generated at {}.", configFile.getAbsolutePath());
        }
        catch (IOException e) {
            Create_fuel_motor.LOG.error("Could not write config file '{}'!", configFile.getAbsolutePath());
            Create_fuel_motor.LOG.catching(e);
        }

        return defaultConfig;
    }
}
