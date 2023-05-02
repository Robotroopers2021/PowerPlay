package teamcode.v1.opmodes

import com.asiankoala.koawalib.command.KOpMode
import com.asiankoala.koawalib.command.commands.InstantCmd
import com.asiankoala.koawalib.logger.Logger
import com.asiankoala.koawalib.logger.LoggerConfig
import com.asiankoala.koawalib.math.Pose
import com.asiankoala.koawalib.math.radians
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.opencv.core.Mat
import teamcode.v1.Robot
import teamcode.v1.commands.sequences.DepositSequence
import teamcode.v1.commands.sequences.HomeSequence
import teamcode.v1.commands.subsystems.ClawCmds
import teamcode.v1.commands.subsystems.DriveCmd
import teamcode.v1.constants.*
import teamcode.v1.subsystems.Lights


@TeleOp
open class KTeleOp : KOpMode(photonEnabled = false) {
    private val robot by lazy { Robot(Pose(-66.0, 40.0, 180.0.radians)) }

    var tvec = Mat()

    override fun mInit() {
//        robot.lights.setPattern(Lights.BlinkinPattern.RAINBOW_FOREST_PALETTE)
        Logger.config = LoggerConfig.DASHBOARD_CONFIG
        scheduleDrive()
        scheduleCycling()
//        scheduleTest()
    }

    private fun scheduleDrive() {
        robot.drive.defaultCommand = DriveCmd(robot.drive, driver)
            }

    private fun scheduleCycling() {
        driver.rightBumper.onPress(HomeSequence(robot, ArmConstants.homePos, -20.0, GuideConstants.telePos))
        driver.leftBumper.onPress(DepositSequence(robot, ArmConstants.highPos, LiftConstants.highPos, GuideConstants.depositPos))
        driver.leftTrigger.onPress(ClawCmds.ClawCloseCmd(robot.claw))
        driver.dpadUp.onPress(DepositSequence(robot, ArmConstants.midPos, LiftConstants.midPos, GuideConstants.depositPos))
        driver.y.onPress(DepositSequence(robot, ArmConstants.lowPos, LiftConstants.lowPos, GuideConstants.lowPos))
        driver.x.onPress(DepositSequence(robot, 200.0, LiftConstants.lowPos, 0.3))
        driver.rightTrigger.onPress(ClawCmds.ClawOpenCmd(robot.claw, robot.guide, GuideConstants.telePos))


        gunner.leftTrigger.onPress(InstantCmd({robot.lift.setPos(-15.5)}))
        gunner.rightTrigger.onPress(InstantCmd({robot.arm.setPos(-270.0)}))
        gunner.leftBumper.onPress(InstantCmd({robot.lift.setPos(11.0)}))
        gunner.rightBumper.onPress(InstantCmd({robot.lift.setPos(0.0)}))
        gunner.b.onPress(InstantCmd({ robot.lift.setPos(2.25)}))
        gunner.a.onPress(InstantCmd({ robot.lift.setPos(3.25)}))
        gunner.x.onPress(InstantCmd({ robot.lift.setPos(4.5)}))
        gunner.y.onPress(InstantCmd({ robot.lift.setPos(5.75)}))
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
        tvec = robot.vision.pose.tvec
        Logger.put("arm pos", robot.hardware.armMotor.pos)
        Logger.put("lift pos", robot.lift.liftLeadMotor.pos)
        Logger.put("arm power", robot.arm.motor.power)
        Logger.put("lift power", robot.lift.liftLeadMotor.power)
        Logger.put("drive powers", driver.leftStick.xAxis)
        Logger.put("switch", robot.lift.limit.invoke())
        Logger.put("dSensor", robot.guide.lastRead)
        Logger.put("claw pos", robot.hardware.clawServo.position)
        if(tvec.empty() == false){
            Logger.put("pole pose", tvec[0, 0][0])
        }

    }
}