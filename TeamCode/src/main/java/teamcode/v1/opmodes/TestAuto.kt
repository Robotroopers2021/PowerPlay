package teamcode.v1.opmodes

import com.asiankoala.koawalib.command.KOpMode
import com.asiankoala.koawalib.command.commands.Cmd
import com.asiankoala.koawalib.command.commands.MecanumCmd
import com.asiankoala.koawalib.logger.Logger
import com.asiankoala.koawalib.logger.LoggerConfig
import com.asiankoala.koawalib.math.Pose
import com.asiankoala.koawalib.math.radians
import com.asiankoala.koawalib.path.ConstantHeadingPath
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import teamcode.v1.auto.AutoRobot
import teamcode.v1.commands.subsystems.DriveCmd

@TeleOp
class TestAuto : KOpMode() {
    private val robot by lazy { AutoRobot(startPose) }

    private val startPose = Pose(0.0, 0.0, 0.0.radians)

    private val path1 = ConstantHeadingPath(
        0.0.radians,
        Pose(0.0,0.0,0.0.radians),
        Pose(70.0,0.0,0.0.radians)
    )

    private val path2 = ConstantHeadingPath(
        180.0.radians,
        Pose(70.0,0.0,0.0.radians),
        Pose(0.0,0.0,0.0.radians)
    )

    private fun scheduleDrive() {
        robot.drive.defaultCommand = DriveCmd(robot.drive, driver)
    }

    override fun mInit() {
        scheduleDrive()

        Logger.config = LoggerConfig.DASHBOARD_CONFIG
    }
    }