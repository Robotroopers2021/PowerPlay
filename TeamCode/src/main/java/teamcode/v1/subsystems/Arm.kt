package teamcode.v1.subsystems

import com.asiankoala.koawalib.hardware.motor.KMotor
import com.asiankoala.koawalib.subsystem.KSubsystem

class Arm(val motor: KMotor, val switch : KLimitSwitch) : KSubsystem(){

    val detect get() = switch.invokeBoolean()

    fun setPos(pos: Double) {
        motor.setProfileTarget(pos)
    }
}