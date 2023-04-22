package teamcode.v1.subsystems

import com.asiankoala.koawalib.command.commands.InstantCmd
import com.asiankoala.koawalib.command.commands.WaitCmd
import com.asiankoala.koawalib.command.group.SequentialGroup
import com.asiankoala.koawalib.control.controller.PIDGains
import com.asiankoala.koawalib.control.motor.FFGains
import com.asiankoala.koawalib.control.profile.MotionConstraints
import com.asiankoala.koawalib.hardware.motor.EncoderFactory
import com.asiankoala.koawalib.hardware.motor.MotorFactory
import com.asiankoala.koawalib.hardware.sensor.KLimitSwitch
import com.asiankoala.koawalib.logger.Logger
import com.asiankoala.koawalib.subsystem.KSubsystem
import teamcode.v1.constants.LiftConstants

class Lift() : KSubsystem() {

    val liftLeadMotor = MotorFactory("liftLead")
        .float
        .reverse
        .createEncoder(
            EncoderFactory(LiftConstants.ticksPerUnit)
            .zero(LiftConstants.homePos)
            .reverse
        )
        .withMotionProfileControl(
            PIDGains(LiftConstants.kP, LiftConstants.kI, LiftConstants.kD),
            FFGains(kS = LiftConstants.kS, kV = LiftConstants.kV, kA = LiftConstants.kA, kG = LiftConstants.kG),
            MotionConstraints(LiftConstants.maxVel, LiftConstants.maxAccel),
            allowedPositionError = LiftConstants.allowedPositionError,
            disabledPosition = LiftConstants.disabledPosition
        )
        .build()

    private val liftSecondMotor = MotorFactory("lift2")
        .float
        .build()

    val limit = KLimitSwitch("limit")

    fun setPos(pos: Double) {
        liftLeadMotor.setProfileTarget(pos)
    }

    var isAttemptingZero = false

    fun startAttemptingZero() {
        isAttemptingZero = true
    }

    override fun periodic() {
        liftSecondMotor.power = liftLeadMotor.power
        if(isAttemptingZero && limit.invoke()) {
            liftLeadMotor.zero()
            isAttemptingZero = false
            setPos(LiftConstants.groundPos)
            Logger.logInfo("zeroed")
        }
    }
}