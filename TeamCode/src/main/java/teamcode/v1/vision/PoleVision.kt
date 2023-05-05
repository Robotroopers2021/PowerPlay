package teamcode.v1.vision

import com.acmerobotics.dashboard.FtcDashboard
import com.asiankoala.koawalib.subsystem.KSubsystem
import com.asiankoala.koawalib.subsystem.vision.KWebcam
import org.opencv.core.Mat
import org.openftc.easyopencv.OpenCvCameraRotation

class PoleVision : KSubsystem(){
    private val pipeline = PolePipeline2()
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
        FtcDashboard.getInstance().startCameraStream(webcam.camera, 30.0)
    }

    fun stop() {
        webcam.stopStreaming()
        pose = Pose()
        pipeline.pose.tvec.release()
        pipeline.pose.rvec.release()
    }
}