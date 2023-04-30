package teamcode.v1.vision

import org.opencv.core.Mat

data class Pose(var rvec: Mat = Mat(), var tvec: Mat = Mat())