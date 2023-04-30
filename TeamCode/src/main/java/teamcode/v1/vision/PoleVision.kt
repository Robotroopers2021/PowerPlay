package teamcode.v1.vision

import com.asiankoala.koawalib.subsystem.KSubsystem
import com.asiankoala.koawalib.subsystem.vision.KWebcam
import org.openftc.easyopencv.OpenCvCameraRotation

class PoleVision : KSubsystem(){
    private val pipeline = PolePipeline()
    private val webcam = KWebcam(
        "PoleWebcam",
        pipeline, 800, 448,
        OpenCvCameraRotation.UPRIGHT
    )


    var pose = Pose()

    override fun periodic() {
        pose = pipeline.pose
    }

    fun start() {
        webcam.startStreaming()
    }

    fun stop() {
        webcam.stopStreaming()
//        pipeline.release()
    }
}