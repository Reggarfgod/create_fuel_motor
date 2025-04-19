package com.reggarf.mods.create_fuel_motor.registry;


import com.reggarf.mods.create_fuel_motor.Create_fuel_motor;
import com.reggarf.mods.create_fuel_motor.config.CFMStress;
import com.reggarf.mods.create_fuel_motor.content.motor.FuelMotorBlock;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.kinetics.motor.CreativeMotorGenerator;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.MapColor;

import static com.reggarf.mods.create_fuel_motor.Create_fuel_motor.REGISTRATE;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;


public class CFMBlocks {
    static {
        REGISTRATE.defaultCreativeTab(Create_fuel_motor.CREATIVE_TAB_KEY);
    }
    public static final BlockEntry<FuelMotorBlock> FUEL_MOTOR =
            REGISTRATE.block("fuel_motor", FuelMotorBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.mapColor(MapColor.COLOR_PURPLE)
                            .forceSolidOn())
                    .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
                    .transform(pickaxeOnly())
                    .blockstate(new CreativeMotorGenerator()::generate)
                    .transform(CFMStress.setCapacity(16384.0))
                    .onRegister(BlockStressValues.setGeneratorSpeed(256, true))
                    .item()
                    .properties(p -> p.rarity(Rarity.COMMON))
                    .transform(customItemModel())
                    .register();

        public static void load() {  }
}
