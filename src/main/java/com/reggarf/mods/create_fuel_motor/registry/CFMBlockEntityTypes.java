package com.reggarf.mods.create_fuel_motor.registry;



import com.reggarf.mods.create_fuel_motor.motor.FuelMotorBlockEntity;
import com.reggarf.mods.create_fuel_motor.motor.FuelMotorRenderer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.reggarf.mods.create_fuel_motor.Create_fuel_motor.REGISTRATE;


public class CFMBlockEntityTypes {

    public static final BlockEntityEntry<FuelMotorBlockEntity> FUEL_MOTOR = REGISTRATE
            .blockEntity("fuel_motor", FuelMotorBlockEntity::new)
            .visual(() -> OrientedRotatingVisual.of(AllPartialModels.SHAFT_HALF), false)
            .validBlocks(CFMBlocks.FUEL_MOTOR)
            .renderer(() -> FuelMotorRenderer::new)
            .register();

    public static void load() {  }
}
