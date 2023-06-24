package teamcode.v1.auto

import com.acmerobotics.dashboard.config.Config
import com.asiankoala.koawalib.control.controller.PIDGains
import com.asiankoala.koawalib.control.motor.FFGains
import com.asiankoala.koawalib.control.profile.MotionConstraints
import com.asiankoala.koawalib.hardware.motor.EncoderFactory
import com.asiankoala.koawalib.hardware.motor.MotorFactory
import com.asiankoala.koawalib.hardware.sensor.KDistanceSensor
import com.asiankoala.koawalib.hardware.servo.KServo
import com.asiankoala.koawalib.math.Pose
import com.asiankoala.koawalib.subsystem.odometry.KThreeWheelOdometry
import teamcode.v1.constants.*

class AutoHardware(startPose: Pose) {
    val fl = MotorFactory("fl")
        .reverse
        .brake
        .build()

    val bl = MotorFactory("bl")
        .reverse
        .brake
        .build()

    val br = MotorFactory("br")
        .forward
        .brake
        .build()

    val fr = MotorFactory("fr")
        .forward
        .brake
        .build()

    val armMotor = MotorFactory("Arm")
        .reverse
        .float
        .createEncoder(
            EncoderFactory(ArmConstants.ticksPerUnit)
            .reverse
            .zero(ArmConstants.autoHomePos)
        )
        .withMotionProfileControl(
            PIDGains(ArmConstants.kP, ArmConstants.kI, ArmConstants.kD),
            FFGains(kS = ArmConstants.kS, kV = ArmConstants.kV, kA = ArmConstants.kA, kCos = ArmConstants.kCos),
            MotionConstraints(ArmConstants.maxVel, ArmConstants.maxAccel),
            allowedPositionError = ArmConstants.allowedPositionError
        )
        .build()

    val distanceSensor = KDistanceSensor("dSensor")

    val clawServo = KServo("Claw")
        .startAt(ClawConstants.closePos)

    val guideServo = KServo("Guide")
            .startAt(GuideConstants.homePos)

    private val leftEncoder = EncoderFactory(ticksPerUnit)
        .revEncoder
        .build(fl)
    private val rightEncoder = EncoderFactory(ticksPerUnit)
        .revEncoder
        .build(bl)
    private val auxEncoder = EncoderFactory(ticksPerUnit)
        .reverse
        .revEncoder
        .build(fr)

    val odometry = KThreeWheelOdometry(
        leftEncoder,
        rightEncoder,
        auxEncoder,
        (OdoConstants.TRACK_WIDTH / 25.4)/ 2.0,
        -(OdoConstants.TRACK_WIDTH / 25.4)/ 2.0,
        OdoConstants.PERP_TRACKER,
        startPose
    )

    @Config
    companion object {
        private const val ticksPerUnit = 1892.3724
    }
}