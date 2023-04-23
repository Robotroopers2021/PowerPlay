package teamcode.v1.commands.subsystems

import com.asiankoala.koawalib.command.commands.Cmd
import com.asiankoala.koawalib.gamepad.KGamepad
import com.asiankoala.koawalib.math.Pose
import com.asiankoala.koawalib.subsystem.drive.KMecanumDrive

class DriveCmd(drive: KMecanumDrive, driver: KGamepad) : Cmd() {
    private val drive: KMecanumDrive
    private val driver: KGamepad

    init {
        addRequirements(drive)
        this.drive = drive
        this.driver = driver
    }

    override fun execute() {
        val xScalar: Double
        val yScalar: Double
        val rScalar: Double
        if (driver.a.isPressed) {
            xScalar = 0.4
            yScalar = 0.4
            rScalar = 0.4
        } else {
            xScalar = 1.0
            yScalar = 1.0
            rScalar = 0.75
        }
        val drivePowers = Pose(
            driver.leftStick.xAxis * xScalar,
            driver.leftStick.yInverted.yAxis * yScalar,
            driver.rightStick.xInverted.xAxis * rScalar
        )
        drive.setPowers(drivePowers)
    }

    override val isFinished: Boolean
        get() = false
}