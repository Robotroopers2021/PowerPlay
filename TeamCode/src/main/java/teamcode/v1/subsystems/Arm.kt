package teamcode.v1.subsystems

import com.asiankoala.koawalib.hardware.motor.KMotor
import com.asiankoala.koawalib.subsystem.KSubsystem

class Arm(val motor: KMotor) : KSubsystem(){

    fun setPos(pos: Double) {
        motor.setProfileTarget(pos)
    }
}