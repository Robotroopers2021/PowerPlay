package teamcode.v1.subsystems

import com.asiankoala.koawalib.hardware.sensor.KDistanceSensor
import com.asiankoala.koawalib.hardware.servo.KServo
import com.asiankoala.koawalib.subsystem.KSubsystem

class Claw(val servo: KServo, val sensor : KDistanceSensor) : KSubsystem() {

    var count = 0
    private var isReading = false
    val lastRead get() = sensor.lastRead

    fun startReading() {
        isReading = true
    }

    fun countUp() {
        count++
    }

    fun stopReading() {
        isReading = false
    }

    fun setPos(pos: Double) {
        servo.position = pos
    }

    override fun periodic() {
        if(isReading) {
            sensor.periodic()
        }
    }
}