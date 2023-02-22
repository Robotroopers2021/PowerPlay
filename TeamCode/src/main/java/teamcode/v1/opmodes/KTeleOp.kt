package teamcode.v1.opmodes

import com.asiankoala.koawalib.command.KOpMode
import com.asiankoala.koawalib.command.commands.InstantCmd
import com.asiankoala.koawalib.command.commands.MecanumCmd
import com.asiankoala.koawalib.logger.Logger
import com.asiankoala.koawalib.logger.LoggerConfig
import com.asiankoala.koawalib.math.Pose
import com.asiankoala.koawalib.math.radians
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import teamcode.v1.Robot
import teamcode.v1.commands.sequences.DepositSequence
import teamcode.v1.commands.sequences.HomeSequence
import teamcode.v1.commands.subsystems.ClawCmds
import teamcode.v1.constants.*

@TeleOp
open class KTeleOp : KOpMode(photonEnabled = false) {
    private val robot by lazy { Robot(Pose(-66.0, 40.0, 180.0.radians)) }

    override fun mInit() {
        InstantCmd({robot.lift.setPos(1.0)})
        Logger.config = LoggerConfig.DASHBOARD_CONFIG
        scheduleDrive()
        scheduleCycling()
//        scheduleTest()
    }

    private fun scheduleDrive() {
        robot.drive.defaultCommand = MecanumCmd(
            robot.drive,
            driver.leftStick.yInverted,
            driver.rightStick.xInverted,
            Pose(1.0,1.0,0.75)
        )
            }

    private fun scheduleCycling() {
        driver.rightBumper.onPress(HomeSequence(robot, ArmConstants.intervalPos, ArmConstants.groundPos, 0.0, GuideConstants.telePos))
        driver.leftBumper.onPress(DepositSequence(robot, ArmConstants.highPos, LiftConstants.highPos, GuideConstants.depositPos))
        driver.leftTrigger.onPress(ClawCmds.ClawCloseCmd(robot.claw))
        driver.dpadUp.onPress(DepositSequence(robot, ArmConstants.midPos, LiftConstants.midPos, GuideConstants.depositPos))
        driver.y.onPress(DepositSequence(robot, ArmConstants.lowPos, LiftConstants.lowPos, GuideConstants.lowPos))
        driver.rightTrigger.onPress(ClawCmds.ClawOpenCmd(robot.claw, robot.guide, GuideConstants.telePos))
        driver.x.onPress(InstantCmd({robot.lift.setPos(3.0)}))
        driver.b.onPress(InstantCmd({robot.lift.setPos(5.0)}))

        gunner.leftTrigger.onPress(InstantCmd({robot.lift.setPos(-15.5)}))
        gunner.rightTrigger.onPress(InstantCmd({robot.arm.setPos(-270.0)}))
        gunner.leftBumper.onPress(InstantCmd({robot.lift.setPos(11.0)}))
        gunner.rightBumper.onPress(InstantCmd({robot.lift.setPos(0.0)}))
    }

    private fun scheduleTest() {
        driver.leftBumper.onPress(InstantCmd({robot.arm.setPos(ArmConstants.highPos)}, robot.arm))
        driver.rightBumper.onPress(InstantCmd({robot.lift.setPos(LiftConstants.highPos)}, robot.lift))
//        driver.leftBumper.onPress(InstantCmd({robot.claw.setPos(ClawConstants.openPos)}))
//        driver.rightBumper.onPress(InstantCmd({robot.claw.setPos(ClawConstants.closePos)}))
        driver.a.onPress(InstantCmd({robot.arm.setPos(-10.0)}, robot.arm))
        driver.b.onPress(InstantCmd({robot.lift.setPos(0.0)}, robot.lift))
    }

    override fun mLoop() {
        Logger.put("arm pos", robot.hardware.armMotor.pos)
        Logger.put("lift pos", robot.hardware.liftLeadMotor.pos)
        Logger.put("arm power", robot.arm.motor.power)
        Logger.put("lift power", robot.hardware.liftLeadMotor.power)
    }
}