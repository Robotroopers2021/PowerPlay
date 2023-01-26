package teamcode.v1.subsystems

import com.asiankoala.koawalib.hardware.servo.KServo
import com.asiankoala.koawalib.subsystem.KSubsystem

class Guide(private val servo : KServo) : KSubsystem() {
    fun setPos(pos: Double) {
        servo.position = pos
    }
}