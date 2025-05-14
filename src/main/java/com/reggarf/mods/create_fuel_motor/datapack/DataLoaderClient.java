package com.reggarf.mods.create_fuel_motor.datapack;

import com.reggarf.mods.create_fuel_motor.config.DataConfig;
import net.minecraft.Util;
import java.nio.file.Path;
import java.util.List;
import static com.reggarf.mods.create_fuel_motor.Create_fuel_motor.MOD_ID;

public record DataLoaderClient(Path configDir, DataConfig config) implements ClientCommand {

    @Override public int execute() { return 0; }
    @Override public String id() { return MOD_ID; }
    @Override public List<ClientCommand> commands() {
        return List.of(new RepoCommand(configDir, config));
    }

    private record RepoCommand(Path configDir, DataConfig config) implements ClientCommand {
        @Override public int execute() { Util.getPlatform().openFile(configDir.toFile()); return 1; }
        @Override public String id() { return "folder"; }
        @Override public List<ClientCommand> commands() {
            return List.of(new SubRepoCommand(configDir, RepoType.DATA, config.dataPacks));
        }
    }

    private record SubRepoCommand(Path configDir, RepoType type, DataConfig.PackConfig config) implements ClientCommand {
        @Override public int execute() { Util.getPlatform().openFile(configDir.resolve(type.getPath()).toFile()); return 1; }
        @Override public String id() { return type.getPath(); }
        @Override public boolean isHidden() { return !config.enabled; }
    }
}
