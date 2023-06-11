package teamcode.v1.opmodes

import com.asiankoala.koawalib.command.KOpMode
import com.asiankoala.koawalib.logger.Logger
import com.asiankoala.koawalib.logger.LoggerConfig
import com.asiankoala.koawalib.math.Pose
import com.asiankoala.koawalib.math.radians
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.opencv.core.Mat
import teamcode.v1.Robot
import teamcode.v1.commands.subsystems.DriveCmd

@TeleOp
open class OutreachTeleOp : KOpMode(photonEnabled = false) {
    private val robot by lazy { Robot(Pose(-66.0, 40.0, 180.0.radians)) }

    var tvec = Mat()

    override fun mInit() {
//        robot.lights.setPattern(Lights.BlinkinPattern.RAINBOW_FOREST_PALETTE)
        Logger.config = LoggerConfig.DASHBOARD_CONFIG
        scheduleDrive()
    }

    private fun scheduleDrive() {
        robot.drive.defaultCommand = DriveCmd(robot.drive, driver)
    }

    override fun mLoop() {
        Logger.put("drive powers", driver.leftStick.xAxis)
    }
}