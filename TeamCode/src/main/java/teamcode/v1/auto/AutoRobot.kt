package teamcode.v1.auto

import com.asiankoala.koawalib.math.Pose
import com.asiankoala.koawalib.subsystem.drive.KMecanumOdoDrive
import teamcode.v1.constants.ArmConstants
import teamcode.v1.constants.LiftConstants
import teamcode.v1.subsystems.*
import teamcode.v1.vision.SleeveVision

class AutoRobot(startPose: Pose) {
    private val hardware = AutoHardware(startPose)

    val drive = KMecanumOdoDrive(
        hardware.fl,
        hardware.bl,
        hardware.br,
        hardware.fr,
        hardware.odometry,
        true
    )

    val arm = Arm(hardware.armMotor)
    val claw = Claw(hardware.clawServo, hardware.distanceSensor)
    val guide = Guide(hardware.guideServo)
    val vision = SleeveVision()
    val lift = Lift()

    init {
        arm.setPos(ArmConstants.autoHomePos)
        lift.setPos(LiftConstants.homePos)
    }
}