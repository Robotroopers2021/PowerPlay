package teamcode.v1.opmodes

import com.asiankoala.koawalib.command.KOpMode
import com.asiankoala.koawalib.command.commands.*
import com.asiankoala.koawalib.command.group.ParallelGroup
import com.asiankoala.koawalib.command.group.SequentialGroup
import com.asiankoala.koawalib.control.profile.disp.Constraints
import com.asiankoala.koawalib.logger.Logger
import com.asiankoala.koawalib.logger.LoggerConfig
import com.asiankoala.koawalib.math.Pose
import com.asiankoala.koawalib.math.radians
import com.asiankoala.koawalib.path.*
import com.asiankoala.koawalib.path.gvf.BetterMotionProfileGVFController
import com.asiankoala.koawalib.util.OpModeState
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import teamcode.v1.auto.AutoRobot
import teamcode.v1.commands.sequences.AutoDepositSequence
import teamcode.v1.commands.sequences.AutoHomeSequence
import teamcode.v1.commands.subsystems.ClawCmds
import teamcode.v1.constants.*
import teamcode.v1.vision.Enums

@Autonomous(preselectTeleOp = "KTeleOp")
class MidLeft : KOpMode() {
    private val robot by lazy { AutoRobot(startPose) }

    private val startPose = Pose(-66.0, 40.0, 180.0.radians)

    private lateinit var mainCommand: Cmd

    private val path1 = HermitePath(
        TangentHeadingController().flip(),
        Pose(startPose.x, startPose.y, 0.0),
        Pose(-11.0, 32.0, 310.0.radians)
    )

    private val intakePath1 = HermitePath(
        TangentHeadingController(),
        Pose(-11.5, 32.5, 120.0.radians),
        Pose(-13.5, 60.5, 90.0.radians)
    )

    private val intakePath2 = HermitePath(
        TangentHeadingController(),
        Pose(-15.0, 31.5, 90.0.radians),
        Pose(-12.5, 60.5, 90.0.radians)
    )

    private val intakePath3 = HermitePath(
        TangentHeadingController(),
        Pose(-15.0, 31.5, 90.0.radians),
        Pose(-11.5, 60.5, 90.0.radians)
    )

    private val depositPath = HermitePath(
        TangentHeadingController().flip(),
        Pose(-14.0, 59.0, 270.0.radians),
        Pose(-14.0, 53.0, 270.0.radians),
        Pose(-24.5, 34.0, 210.0.radians)
    )

    private val leftPath = HermitePath(
        TangentHeadingController(),
        Pose(-15.0, 31.5, 90.0.radians),
        Pose(-12.5, 58.0, 90.0.radians)
    )

    private val middlePath = HermitePath(
        TangentHeadingController().flip(),
        Pose(-12.5, 58.5, 270.0.radians),
        Pose(-14.0, 37.0, 270.0.radians),
    )

    private val rightPath = HermitePath(
        TangentHeadingController().flip(),
        Pose(-12.5, 58.5, 270.0.radians),
        Pose(-14.0, 14.5, 270.0.radians)

    )

    override fun mInit() {
        robot.vision.start()
        robot.claw.setPos(ClawConstants.closePos)
        Logger.config = LoggerConfig(
            isLogging = true,
            false,
            isDashboardEnabled = true,
            isTelemetryEnabled = true
        )

        mainCommand = SequentialGroup(
            WaitUntilCmd {opModeState == OpModeState.PLAY},
            InstantCmd({robot.lift.setPos(7.0)}),
            GVFCmd(
                robot.drive,
                BetterMotionProfileGVFController(
                    path1,
                    robot.drive,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    Constraints(0.0,0.0),
                    0.0,
                    0.0,
                    0.0),
                ProjQuery(
                    AutoDepositSequence(
                        robot,
                        137.0,
                        LiftConstants.highPos,
                        GuideConstants.depositPos),
                        0.5)
            ),
            ClawCmds.ClawOpenCmd(robot.claw, robot.guide, GuideConstants.telePos),
            WaitCmd(0.1),
            ParallelGroup(
                AutoHomeSequence(
                    robot,
                    ArmConstants.groundPos,
                    4.5,
                    GuideConstants.telePos),
                GVFCmd(
                    robot.drive,
                    BetterMotionProfileGVFController(
                        intakePath1,
                        robot.drive,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        Constraints(0.0,0.0),
                        0.0,
                        0.0,
                        0.0)
                )
            ),
            WaitCmd(0.1),
            ClawCmds.ClawCloseCmd(robot.claw),
            WaitCmd(0.1),
            InstantCmd({robot.lift.setPos(11.0)}),
            WaitCmd(0.1),
            GVFCmd(
                robot.drive,
                BetterMotionProfileGVFController(
                    depositPath,
                    robot.drive,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    Constraints(0.0,0.0),
                    0.0,
                    0.0,
                    0.0),
                ProjQuery(
                    AutoDepositSequence(
                        robot,
                        145.0,
                        LiftConstants.midPos,
                        GuideConstants.depositPos),
                    0.5)
            ),
            WaitCmd(0.1),
            ClawCmds.ClawOpenCmd(robot.claw, robot.guide, GuideConstants.telePos),
            WaitCmd(0.1),
            ParallelGroup(
                AutoHomeSequence(
                    robot,
                    ArmConstants.groundPos,
                    3.0,
                    GuideConstants.telePos),
                GVFCmd(
                    robot.drive,
                    BetterMotionProfileGVFController(
                        intakePath2,
                        robot.drive,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        Constraints(0.0,0.0),
                        0.0,
                        0.0,
                        0.0)
                )
            ),
            WaitCmd(0.1),
            ClawCmds.ClawCloseCmd(robot.claw),
            WaitCmd(0.1),
            InstantCmd({robot.lift.setPos(9.5)}),
            WaitCmd(0.1),
            GVFCmd(
                robot.drive,
                BetterMotionProfileGVFController(
                    depositPath,
                    robot.drive,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    Constraints(0.0,0.0),
                    0.0,
                    0.0,
                    0.0),
                ProjQuery(
                    AutoDepositSequence(
                        robot,
                        145.0,
                        LiftConstants.midPos,
                        GuideConstants.depositPos),
                    0.5)
            ),
            WaitCmd(0.1),
            ClawCmds.ClawOpenCmd(robot.claw, robot.guide, GuideConstants.telePos),
            WaitCmd(0.1),
            ParallelGroup(
                AutoHomeSequence(
                    robot,
                    ArmConstants.groundPos,
                    1.5,
                    GuideConstants.telePos),
                GVFCmd(
                    robot.drive,
                    BetterMotionProfileGVFController(
                        intakePath2,
                        robot.drive,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        Constraints(0.0,0.0),
                        0.0,
                        0.0,
                        0.0)
                )
            ),
            WaitCmd(0.1),
            ClawCmds.ClawCloseCmd(robot.claw),
            WaitCmd(0.1),
            InstantCmd({robot.lift.setPos(9.0)}),
            WaitCmd(0.1),
            GVFCmd(
                robot.drive,
                BetterMotionProfileGVFController(
                    depositPath,
                    robot.drive,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    Constraints(0.0,0.0),
                    0.0,
                    0.0,
                    0.0),
                ProjQuery(
                    AutoDepositSequence(
                        robot,
                        145.0,
                        LiftConstants.midPos,
                        GuideConstants.depositPos),
                    0.5)
            ),
            WaitCmd(0.1),
            ClawCmds.ClawOpenCmd(robot.claw, robot.guide, GuideConstants.telePos),
            WaitCmd(0.1),
            ParallelGroup(
                AutoHomeSequence(
                    robot,
                    ArmConstants.groundPos,
                    0.5,
                    GuideConstants.telePos),
                GVFCmd(
                    robot.drive,
                    BetterMotionProfileGVFController(
                        intakePath3,
                        robot.drive,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        Constraints(0.0,0.0),
                        0.0,
                        0.0,
                        0.0)
                )
            ),
            WaitCmd(0.1),
            ClawCmds.ClawCloseCmd(robot.claw),
            WaitCmd(0.1),
            InstantCmd({robot.lift.setPos(8.5)}),
            WaitCmd(0.1),
            GVFCmd(
                robot.drive,
                BetterMotionProfileGVFController(
                    depositPath,
                    robot.drive,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    Constraints(0.0,0.0),
                    0.0,
                    0.0,
                    0.0),
                ProjQuery(
                    AutoDepositSequence(
                        robot,
                        145.0,
                        LiftConstants.midPos,
                        GuideConstants.depositPos),
                    0.5)
            ),
            WaitCmd(0.1),
            ClawCmds.ClawOpenCmd(robot.claw, robot.guide, GuideConstants.telePos),
            WaitCmd(0.1),
            ParallelGroup(
                AutoHomeSequence(
                    robot,
                    ArmConstants.groundPos,
                    0.0,
                    GuideConstants.telePos),
                GVFCmd(
                    robot.drive,
                    BetterMotionProfileGVFController(
                        intakePath3,
                        robot.drive,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        Constraints(0.0,0.0),
                        0.0,
                        0.0,
                        0.0)
                )
            ),
            WaitCmd(0.1),
            ClawCmds.ClawCloseCmd(robot.claw),
            WaitCmd(0.1),
            InstantCmd({robot.lift.setPos(8.0)}),
            WaitCmd(0.1),
            GVFCmd(
                robot.drive,
                BetterMotionProfileGVFController(
                    depositPath,
                    robot.drive,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    Constraints(0.0,0.0),
                    0.0,
                    0.0,
                    0.0),
                ProjQuery(
                    AutoDepositSequence(
                        robot,
                        145.0,
                        LiftConstants.midPos,
                        GuideConstants.depositPos),
                    0.5)
            ),
            WaitCmd(0.1),
            ClawCmds.ClawOpenCmd(robot.claw, robot.guide, GuideConstants.telePos),
            WaitCmd(0.1),
            ParallelGroup(
                AutoHomeSequence(
                    robot,
                    -80.0,
                    -1.0,
                    GuideConstants.telePos),
                GVFCmd(
                    robot.drive,
                    BetterMotionProfileGVFController(
                        leftPath,
                        robot.drive,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        Constraints(0.0,0.0),
                        0.0,
                        0.0,
                        0.0)
                )
            ),
            ChooseCmd(
                GVFCmd(
                    robot.drive,
                    BetterMotionProfileGVFController(
                        rightPath,
                        robot.drive,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        Constraints(0.0,0.0),
                        0.0,
                        0.0,
                        0.0)
                ),
                ChooseCmd(
                    GVFCmd(
                        robot.drive,
                        BetterMotionProfileGVFController(
                            middlePath,
                            robot.drive,
                            0.0,
                            0.0,
                            0.0,
                            0.0,
                            0.0,
                            Constraints(0.0,0.0),
                            0.0,
                            0.0,
                            0.0)
                    ),
                    WaitCmd(0.1),
                ) { robot.vision.zone == Enums.Zones.MIDDLE },
            ) { robot.vision.zone == Enums.Zones.RIGHT }
        )
        mainCommand.schedule()
    }

    override fun mInitLoop() {
        Logger.put("zone", robot.vision.zone)
        Logger.put("arm pos", robot.arm.motor.pos)
    }

    override fun mStart() {
        robot.vision.stop()
        robot.vision.unregister()
    }

    override fun mLoop() {
        Logger.put("arm pos", robot.arm.motor.pos)
    }
}