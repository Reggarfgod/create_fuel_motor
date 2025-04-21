package com.reggarf.mods.create_fuel_motor.registry;


import com.reggarf.mods.create_fuel_motor.motor.FuelMotorBlockEntity;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class CFMCapabilities {
    public static void register(RegisterCapabilitiesEvent event) {

        FuelMotorBlockEntity.registerCapabilities(event);
    }
}
