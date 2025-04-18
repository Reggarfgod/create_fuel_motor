package com.reggarf.mods.create_fuel_motor.ponder;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;  // Smoke particles import
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class PonderScenes {
	public static void fuelMotor(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("fuel_motor", "create_fuel_motor.ponder.fuel_motor.title");
		scene.configureBasePlate(0, 0, 5);
		scene.world().showSection(util.select().layer(0), Direction.UP);

		BlockPos motor = util.grid().at(3, 1, 2);

		for (int i = 0; i < 3; i++) {
			scene.idle(5);
			scene.world().showSection(util.select().position(1 + i, 1, 2), Direction.DOWN);
		}

		scene.idle(10);
		scene.effects().rotationDirectionIndicator(motor);
		scene.overlay().showText(60)
				.text("The Fuel Motor generates Rotational Force by consuming burnable fuel")
				.placeNearTarget()
				.pointAt(util.vector().topOf(motor));
		scene.idle(60);

		scene.rotateCameraY(90);
		scene.idle(20);

		scene.addKeyframe();
		scene.overlay().showText(60)
				.text("Just drop a fuel item on top to power the motor!")
				.placeNearTarget()
				.pointAt(util.vector().blockSurface(motor, Direction.UP));
		scene.idle(30);

		// Drop coal on the motor
		scene.world().createItemEntity(
				util.vector().topOf(motor).add(0, 0.25, 0),
				new Vec3(0, -0.1, 0),
				Items.COAL.getDefaultInstance()
		);
		scene.idle(30);
		scene.effects().rotationSpeedIndicator(motor);
		scene.world().modifyKineticSpeed(util.select().fromTo(1, 1, 2, 3, 1, 2), f -> 4 * f);
		scene.idle(60);

		scene.overlay().showText(80)
				.text("Each fuel item powers the motor for a duration based on its burn time")
				.placeNearTarget()
				.pointAt(util.vector().topOf(motor));
		scene.idle(60);

		scene.markAsFinished();
		scene.rotateCameraY(-90);
	}
	public static void inMotor(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("fuel_motor_1", "Powering the Fuel Motor with Fuel");
		scene.configureBasePlate(0, 0, 5);
		scene.world().showSection(util.select().layer(0), Direction.UP);

		BlockPos motor = util.grid().at(3, 1, 2);

		for (int dx = -1; dx <= 1; dx++) {
			for (int dz = -1; dz <= 1; dz++) {
				BlockPos pos = util.grid().at(2 + dx, 1, 2 + dz);
				scene.world().showSection(util.select().position(pos), Direction.DOWN);
				scene.idle(2);
			}
		}


		scene.idle(10);
		scene.effects().rotationDirectionIndicator(motor);
		scene.overlay().showText(60)
				.text("The Fuel Motor generates Rotational Force by consuming burnable fuel")
				.placeNearTarget()
				.pointAt(util.vector().topOf(motor));
		scene.idle(60);

		scene.rotateCameraY(90);
		scene.idle(20);

		scene.addKeyframe();
		scene.overlay().showText(60)
				.text("You can also feed fuel using a funnel!")
				.placeNearTarget()
				.pointAt(util.vector().blockSurface(motor, Direction.UP));
		scene.idle(30);

		// Simulate kinetic energy output
		scene.effects().rotationSpeedIndicator(motor);
		scene.world().modifyKineticSpeed(util.select().fromTo(1, 1, 2, 3, 1, 2), f -> 4 * f);
		scene.idle(60);

		scene.overlay().showText(80)
				.text("Each fuel item powers the motor for a duration based on its burn time")
				.placeNearTarget()
				.pointAt(util.vector().topOf(motor));
		scene.idle(60);

		scene.markAsFinished();
		scene.rotateCameraY(-90);
	}
}
