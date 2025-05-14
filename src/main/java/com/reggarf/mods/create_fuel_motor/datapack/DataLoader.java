package com.reggarf.mods.create_fuel_motor.datapack;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import com.reggarf.mods.create_fuel_motor.Create_fuel_motor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.reggarf.mods.create_fuel_motor.Create_fuel_motor.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DataLoader {

    @SubscribeEvent
    public static void onClientCommand(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(createCommand(new DataLoaderClient(Create_fuel_motor.configDir, Create_fuel_motor.config)));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createCommand(ClientCommand command) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(command.id());
        if (!command.isHidden()) {
            builder = builder.executes(context -> command.execute());
        }
        for (ClientCommand subCommand : command.commands()) {
            if (subCommand.isHidden()) continue;
            builder = builder.then(createCommand(subCommand));
        }
        return builder;
    }
}
