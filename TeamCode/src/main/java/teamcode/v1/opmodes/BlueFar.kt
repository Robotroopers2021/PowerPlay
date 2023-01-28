package teamcode.v1.opmodes

import com.asiankoala.koawalib.command.commands.*
import com.asiankoala.koawalib.command.group.SequentialGroup
import com.asiankoala.koawalib.logger.Logger
import com.asiankoala.koawalib.logger.LoggerConfig
import com.asiankoala.koawalib.math.Pose
import com.asiankoala.koawalib.math.angleWrap
import com.asiankoala.koawalib.math.radians
import com.asiankoala.koawalib.path.*
import com.asiankoala.koawalib.path.gvf.SimpleGVFController
import com.asiankoala.koawalib.util.OpModeState
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import teamcode.v1.auto.AutoRobot
import teamcode.v1.commands.sequences.DepositSequence
import teamcode.v1.commands.sequences.HomeSequence
import teamcode.v1.commands.subsystems.ClawCmds
import teamcode.v1.commands.subsystems.GuideCmds
import teamcode.v1.constants.ArmConstants
import teamcode.v1.constants.ClawConstants
import teamcode.v1.constants.GuideConstants
import teamcode.v1.constants.LiftConstants
import teamcode.v1.vision.AutoOpMode

@Autonomous(preselectTeleOp = "KTeleOp")
class BlueFar : AutoOpMode() {
    private val robot by lazy { AutoRobot(startPose) }

    private val startPose = Pose(-66.0, 40.0, 180.0.radians)

    private lateinit var mainCommand: Cmd

    private val path1 = HermitePath(
        DEFAULT_HEADING_CONTROLLER.flip(),
        Pose(startPose.x, startPose.y, 0.0),
        Pose(-45.0, 40.0, 0.0),
        Pose(-10.0, 31.5, 310.0.radians)
    )

    private val intakePath = HermitePath(
        DEFAULT_HEADING_CONTROLLER,
        Pose(-10.0, 33.5, 120.0.radians),
        Pose(-15.0, 65.0, 90.0.radians)
    )

    private val depositPath = HermitePath(
        DEFAULT_HEADING_CONTROLLER.flip(),
        Pose(-16.0, 64.0, 270.0.radians),
        Pose(-16.0, 56.0, 275.0.radians),
        Pose(-3.0, 34.0, 310.0.radians)
    )

    private val intakePath2 = HermitePath(
        DEFAULT_HEADING_CONTROLLER,
        Pose(-10.5, 34.5, 120.0.radians),
        Pose(-15.0, 64.0, 90.0.radians)
    )

    private val leftPath = HermitePath(
        {180.0.radians},
        Pose(-14.0, 33.5, 180.0.radians),
        Pose(-18.0, 60.0, 180.0.radians),
        Pose(-22.0, 60.0, 180.0.radians),
        Pose(-24.0, 60.0, 180.0.radians)
    )

    private val middlePath = HermitePath(
        {180.0.radians},
        Pose(-8.0, 33.5, 180.0.radians),
        Pose(-16.0, 39.0, 180.0.radians),
        Pose(-18.0, 39.0, 180.0.radians),
        Pose(-20.0, 39.0, 180.0.radians)
    )

    private val rightPath = HermitePath(
        {180.0.radians},
        Pose(-8.0, 33.5, 180.0.radians),
        Pose(-16.0, 15.0, 180.0.radians),
        Pose(-18.0, 15.0, 180.0.radians),
        Pose(-20.0, 15.0, 180.0.radians)
    )

    override fun mInit() {
        super.mInit()
        robot.claw.setPos(ClawConstants.closePos)
        Logger.config = LoggerConfig(
            isLogging = true,
            false,
            isDashboardEnabled = true,
            isTelemetryEnabled = true
        )

        mainCommand = SequentialGroup(
            WaitUntilCmd {opModeState == OpModeState.START},
            InstantCmd({robot.lift.setPos(7.0)}),
            GVFCmd(
                robot.drive,
                SimpleGVFController(path1, robot.drive, 0.6, 20.0, 10.0, 0.5, 3.0, 5.0),
                ProjQuery(
                    DepositSequence(robot.lift, robot.arm, robot.claw, robot.guide, 145.0, LiftConstants.highPos, GuideConstants.depositPos),
                    0.5
                )
            ),
            ClawCmds.ClawOpenCmd(robot.claw, robot.guide, GuideConstants.telePos),
            WaitCmd(0.5),
            HomeSequence(robot.lift, robot.claw, robot.arm, robot.guide, ArmConstants.intervalPos, ArmConstants.groundPos, 5.0, GuideConstants.telePos),
            GVFCmd(
                robot.drive,
                SimpleGVFController(intakePath, robot.drive, 0.6, 20.0, 8.0, 0.4, 3.0, 5.0)
            ),
            WaitCmd(0.25),
            ClawCmds.ClawCloseCmd(robot.claw),
            WaitCmd(0.5),
            InstantCmd({robot.lift.setPos(11.0)}),
            GuideCmds.GuideDepositCmd(robot.guide),
            WaitCmd(0.25),
            GVFCmd(
                robot.drive,
                SimpleGVFController(depositPath, robot.drive, 0.6, 20.0, 15.0, 0.5, 3.0, 5.0),
                ProjQuery(
                    DepositSequence(robot.lift, robot.arm, robot.claw, robot.guide, 155.0, LiftConstants.highPos, GuideConstants.depositPos),
                    0.5
                )
            ),
            WaitCmd(0.25),
            ClawCmds.ClawOpenCmd(robot.claw, robot.guide, GuideConstants.telePos),
            WaitCmd(0.25),
            HomeSequence(robot.lift, robot.claw, robot.arm, robot.guide, ArmConstants.intervalPos, ArmConstants.groundPos, 4.0, GuideConstants.telePos),
            GVFCmd(
                robot.drive,
                SimpleGVFController(intakePath2, robot.drive, 0.6, 25.0, 8.0, 0.4, 3.0, 5.0)
            ),
            WaitCmd(0.25),
            ClawCmds.ClawCloseCmd(robot.claw),
            WaitCmd(0.5),
            InstantCmd({robot.lift.setPos(11.0)}),
            GuideCmds.GuideDepositCmd(robot.guide),
            WaitCmd(0.25),
            GVFCmd(
                robot.drive,
                SimpleGVFController(depositPath, robot.drive, 0.6, 20.0, 15.0, 0.5, 3.0, 5.0),
                ProjQuery(
                    DepositSequence(robot.lift, robot.arm, robot.claw, robot.guide, 155.0, LiftConstants.highPos, GuideConstants.depositPos),
                    0.5
                )
            ),
            WaitCmd(0.25),
            ClawCmds.ClawOpenCmd(robot.claw, robot.guide, GuideConstants.telePos),
            WaitCmd(0.25),
            HomeSequence(robot.lift, robot.claw, robot.arm, robot.guide, ArmConstants.intervalPos, ArmConstants.groundPos, 3.0, GuideConstants.telePos),
            GVFCmd(
                robot.drive,
                SimpleGVFController(intakePath2, robot.drive, 0.6, 25.0, 8.0, 0.4, 3.0, 5.0)
            ),
            WaitCmd(0.25),
            ClawCmds.ClawCloseCmd(robot.claw),
            WaitCmd(0.5),
            InstantCmd({robot.lift.setPos(11.0)}),
            GuideCmds.GuideDepositCmd(robot.guide),
            WaitCmd(0.25),
            GVFCmd(
                robot.drive,
                SimpleGVFController(depositPath, robot.drive, 0.6, 20.0, 15.0, 0.5, 3.0, 5.0),
                ProjQuery(
                    DepositSequence(robot.lift, robot.arm, robot.claw, robot.guide, 155.0, LiftConstants.highPos, GuideConstants.depositPos),
                    0.5
                )
            ),
            WaitCmd(0.25),
            ClawCmds.ClawOpenCmd(robot.claw, robot.guide, GuideConstants.telePos),
            WaitCmd(0.25),
            HomeSequence(robot.lift, robot.claw, robot.arm, robot.guide, ArmConstants.intervalPos, ArmConstants.groundPos, 1.0, GuideConstants.telePos),
            GVFCmd(
                robot.drive,
                SimpleGVFController(intakePath2, robot.drive, 0.6, 25.0, 8.0, 0.4, 3.0, 5.0)
            ),
            WaitCmd(0.25),
            ClawCmds.ClawCloseCmd(robot.claw),
            WaitCmd(0.5),
            InstantCmd({robot.lift.setPos(11.0)}),
            GuideCmds.GuideDepositCmd(robot.guide),
            WaitCmd(0.25),
            GVFCmd(
                robot.drive,
                SimpleGVFController(depositPath, robot.drive, 0.6, 20.0, 15.0, 0.5, 3.0, 5.0),
                ProjQuery(
                    DepositSequence(robot.lift, robot.arm, robot.claw, robot.guide, 155.0, LiftConstants.highPos, GuideConstants.depositPos),
                    0.5
                )
            ),
            WaitCmd(0.25),
            ClawCmds.ClawOpenCmd(robot.claw, robot.guide, GuideConstants.telePos),
            WaitCmd(0.25),
            HomeSequence(robot.lift, robot.claw, robot.arm, robot.guide, ArmConstants.intervalPos, ArmConstants.groundPos, 0.0, GuideConstants.telePos),
            ChooseCmd(
                GVFCmd(robot.drive,
                    SimpleGVFController(rightPath, robot.drive, 0.5, 30.0, 6.0, 0.6, 3.0, 5.0)),
                ChooseCmd(
                    GVFCmd(robot.drive,
                        SimpleGVFController(middlePath, robot.drive, 0.5, 30.0, 6.0, 0.6, 3.0, 5.0)),
                    GVFCmd(robot.drive, SimpleGVFController(leftPath, robot.drive, 0.5, 30.0, 6.0, 0.6, 3.0, 5.0)),
                ) { tagOfInterest!!.id == MIDDLE },
            ) { tagOfInterest!!.id == RIGHT }
        )
        mainCommand.schedule()
    }

    override fun mLoop() {
        super.mLoop()
        Logger.addTelemetryData("arm pos", robot.arm.motor.pos)
    }

    override fun mStop() {
        super.mStop()
        ClawCmds.ClawCloseCmd(robot.claw)
    }
}