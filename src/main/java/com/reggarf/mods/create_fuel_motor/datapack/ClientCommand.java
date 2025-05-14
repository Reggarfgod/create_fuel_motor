package com.reggarf.mods.create_fuel_motor.datapack;

import java.util.List;

public interface ClientCommand {

    int execute();

    String id();

    default boolean isHidden() {
        return false;
    }

    default List<ClientCommand> commands() {
        return List.of();
    }
}
