package teamcode.mule.subsystems

import com.asiankoala.koawalib.hardware.motor.KMotor
import com.asiankoala.koawalib.subsystem.KSubsystem

class MuleArm(val motor: KMotor) : KSubsystem() {
    fun setPos(pos: Double) {
        motor.setProfileTarget(pos)
    }
}