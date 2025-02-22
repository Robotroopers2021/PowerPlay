package teamcode.v1.vision

import com.asiankoala.koawalib.subsystem.KSubsystem
import com.asiankoala.koawalib.subsystem.vision.KWebcam
import org.openftc.easyopencv.OpenCvCameraRotation

class SleeveVision: KSubsystem() {
    private val pipeline = SleevePipeline()
    private val webcam = KWebcam(
        "webcam",
        pipeline,
        800,
        448,
        OpenCvCameraRotation.UPSIDE_DOWN
    )
    var zone = Enums.Zones.WTF
        private set

    override fun periodic() {
        zone = pipeline.zone
    }

    fun start() {
        webcam.startStreaming()
    }

    fun stop() {
        webcam.stopStreaming()
        pipeline.release()
    }
}