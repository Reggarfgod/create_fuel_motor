package com.reggarf.mods.create_fuel_motor.armpoint;//package com.reggarf.mods.create_fuel_motor.armpoint;
//
//
//
//import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
//import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.state.BlockState;
//import com.reggarf.mods.create_fuel_motor.Register.CFMBlocks;
//
//public class FuelMotorArmInteractionPointType extends ArmInteractionPointType {
//    @Override
//    public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
//        return CFMBlocks.FUEL_MOTOR.has(state);
//    }
//
//    @Override
//    public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
//        return new FuelMotorArmInteractionPoint(this, level, pos, state);
//    }
//}
