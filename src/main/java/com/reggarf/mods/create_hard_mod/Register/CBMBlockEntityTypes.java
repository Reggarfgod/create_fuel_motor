package com.reggarf.mods.create_hard_mod.Register;


import com.reggarf.mods.create_hard_mod.motor.CoalMotorBlockEntity;
import com.reggarf.mods.create_hard_mod.motor.CoalMotorRenderer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.reggarf.mods.create_hard_mod.Create_hard_mod.REGISTRATE;

public class CBMBlockEntityTypes {

    public static final BlockEntityEntry<CoalMotorBlockEntity> MOTOR = REGISTRATE
            .blockEntity("motor", CoalMotorBlockEntity::new)
            .visual(() -> OrientedRotatingVisual.of(AllPartialModels.SHAFT_HALF), false)
            .validBlocks(CBMBlocks.CREATIVE_MOTOR)
            .renderer(() -> CoalMotorRenderer::new)
            .register();

    public static void load() {  }
}
