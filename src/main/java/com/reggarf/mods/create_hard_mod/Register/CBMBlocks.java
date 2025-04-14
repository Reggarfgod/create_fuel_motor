package com.reggarf.mods.create_hard_mod.Register;


import com.reggarf.mods.create_hard_mod.Create_hard_mod;
import com.reggarf.mods.create_hard_mod.motor.CoalMotorBlock;
import com.reggarf.mods.create_hard_mod.motor.CoalMotorGenerator;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.MapColor;

import static com.reggarf.mods.create_hard_mod.Create_hard_mod.REGISTRATE;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;


public class CBMBlocks {
    static {
        REGISTRATE.defaultCreativeTab(Create_hard_mod.CREATIVE_TAB_KEY);
    }
    public static final BlockEntry<CoalMotorBlock> CREATIVE_MOTOR =
            REGISTRATE.block("creative_motor", CoalMotorBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.mapColor(MapColor.COLOR_PURPLE)
                            .forceSolidOn())
                    .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
                    .transform(pickaxeOnly())
                    .blockstate(new CoalMotorGenerator()::generate)
                    .transform(CBMStress.setCapacity(16384.0))
                    .onRegister(BlockStressValues.setGeneratorSpeed(256, true))
                    .item()
                    .properties(p -> p.rarity(Rarity.EPIC))
                    .transform(customItemModel())
                    .register();

        public static void load() {  }
}
