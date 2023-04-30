package teamcode.v1.vision

import org.firstinspires.ftc.robotcore.external.Telemetry
import teamcode.v1.vision.VisionUtil.draw3dCubeMarker
import teamcode.v1.vision.VisionUtil.drawAxisMarker
import teamcode.v1.vision.VisionUtil.poseFromTrapezoid
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.RotatedRect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.openftc.easyopencv.OpenCvPipeline

class PolePipeline(
    private val telemetry: Telemetry? = null,
) : OpenCvPipeline() {

    companion object{
        // STATIC CONSTANTS
        @JvmField
        val blue = Scalar(7.0, 197.0, 235.0, 255.0)
        val red = Scalar(255.0, 0.0, 0.0, 255.0)
        val green = Scalar(0.0, 255.0, 0.0, 255.0)
        val white = Scalar(255.0, 255.0, 255.0, 255.0)
        val lowerYellow = Scalar(9.9, 200.0, 86.4)
        val upperYellow = Scalar(79.3, 255.0, 255.0)

        val fx = 2.840787980870358e+03
        val fy = 2.817438613994958e+03
        val cx = 1.528711012730565e+03
        val cy = 5.165550661468130e+02

        val tagX = 1.052*0.0254
        val tagY = 10*0.0254 //TODO: FIX THIS
    }

    private var mat: Mat
    private var ret: Mat


    var pose = Pose()


    init {
        ret = Mat()
        mat = Mat()
    }


    override fun processFrame(input: Mat?): Mat {
        ret.release() // releasing mat to release backing buffer
        // must release at the start of function since this is the variable being returned

        ret = Mat() // resetting pointer held in ret
        try { // try catch in order for opMode to not crash and force a restart
            /**converting from RGB color space to HSV color space**/
            Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2HSV)

            /**checking if any pixel is within the color bounds to make a black and white mask**/
            val mask = Mat(mat.rows(), mat.cols(), CvType.CV_8UC1) // variable to store mask in
            Core.inRange(mat, lowerYellow, upperYellow, mask)

            /**applying to input and putting it on ret in black or yellow**/
            Core.bitwise_and(input, input, ret, mask)

            /**applying GaussianBlur to reduce noise when finding contours**/
            Imgproc.GaussianBlur(mask, mask, Size(5.0, 15.0), 5.00)

            /**finding contours on mask**/
            val contours: List<MatOfPoint> = ArrayList()
            val hierarchy = Mat()
            Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE)

            Imgproc.drawContours(ret, contours, -1, green, 2)
            //finding the largest(closest) contour
            var maxWidth = 0.0
            var maxRect = RotatedRect()
            for (c: MatOfPoint in contours) {
                val copy = MatOfPoint2f(*c.toArray())
                val rect = Imgproc.minAreaRect(copy)

                if (rect.boundingRect().width > maxWidth) {
                    maxWidth = rect.boundingRect().width.toDouble()
                    maxRect = rect
                }


                c.release() // releasing the buffer of the contour, since after use, it is no longer needed
                copy.release() // releasing the buffer of the copy of the contour, since after use, it is no longer needed
            }

            //Imgproc.rectangle(ret, maxRect.boundingRect().tl(), maxRect.boundingRect().br(), red, 2)

            //Find corner points of contour

            val pointsMat = Mat()
            Imgproc.boxPoints(maxRect, pointsMat)

            val points: Array<Point?> = arrayOf(
                Point(pointsMat[0,0][0], pointsMat[0,1][0]),
                Point(pointsMat[1,0][0], pointsMat[1,1][0]),
                Point(pointsMat[2,0][0], pointsMat[2,1][0]),
                Point(pointsMat[3,0][0], pointsMat[3,1][0]))

            for (p in points) {
                Imgproc.circle(ret, p, 2, blue, 2)
            }



            //     Construct the camera matrix.
            //
            //      --         --
            //     | fx   0   cx |
            //     | 0    fy  cy |
            //     | 0    0   1  |
            //      --         --
            //
            val cameraMatrix = Mat(3, 3, CvType.CV_32FC1)

            cameraMatrix.put(0, 0, fx)
            cameraMatrix.put(0, 1, 0.0)
            cameraMatrix.put(0, 2, cx)

            cameraMatrix.put(1, 0, 0.0)
            cameraMatrix.put(1, 1, fy)
            cameraMatrix.put(1, 2, cy)

            cameraMatrix.put(2, 0, 0.0)
            cameraMatrix.put(2, 1, 0.0)
            cameraMatrix.put(2, 2, 1.0)

            //Calculate pose
            pose = poseFromTrapezoid(points, cameraMatrix, tagX, tagY)

            //Draw cube and axis
            drawAxisMarker(ret, 10*0.0254/2.0, 1, pose.rvec, pose.tvec, cameraMatrix)
            draw3dCubeMarker(ret, 1.052*0.0254, tagX, tagY, 1, pose.rvec, pose.tvec, cameraMatrix)

            println("(${pose.tvec[0,0][0]}, ${pose.tvec[1,0][0]}, ${pose.tvec[2,0][0]}")

            mat.release()
            mask.release()
            hierarchy.release()

        } catch (e: Exception) {
            /**error handling, prints stack trace for specific debug**/
            telemetry?.addData("[ERROR]", e)
            e.stackTrace.toList().stream().forEach { x -> telemetry?.addLine(x.toString()) }
        }
        telemetry?.update()

        /**returns the black and yellow mask with contours drawn to see logic in action**/
        return ret
    }
}