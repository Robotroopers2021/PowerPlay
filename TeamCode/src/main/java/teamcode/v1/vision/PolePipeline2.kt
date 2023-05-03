package teamcode.v1.vision

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.openftc.easyopencv.OpenCvPipeline

//for dashboard
/*@Config*/
class PolePipeline2 : OpenCvPipeline() {
    //backlog of frames to average out to reduce noise
    var frameList: ArrayList<DoubleArray> = ArrayList()

    override fun processFrame(input: Mat): Mat {
        val mat = Mat()

        //mat turns into HSV value
        Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2HSV)
        if (mat.empty()) {
            return input
        }

        // lenient bounds will filter out near yellow, this should filter out all near yellow things(tune this if needed)
        val lowHSV = Scalar(20.0, 70.0, 80.0) // lenient lower bound HSV for yellow
        val highHSV = Scalar(32.0, 255.0, 255.0) // lenient higher bound HSV for yellow
        val thresh = Mat()

        // Get a black and white image of yellow objects
        Core.inRange(mat, lowHSV, highHSV, thresh)
        val masked = Mat()
        //color the white portion of thresh in with HSV from mat
        //output into masked
        Core.bitwise_and(mat, mat, masked, thresh)
        //calculate average HSV values of the white thresh values
        val average = Core.mean(masked, thresh)
        val scaledMask = Mat()
        //scale the average saturation to 150
        masked.convertTo(scaledMask, -1, 150 / average.`val`[1], 0.0)
        val scaledThresh = Mat()
        //you probably want to tune this
        val strictLowHSV = Scalar(0.0, strictLowS, 0.0) //strict lower bound HSV for yellow
        val strictHighHSV = Scalar(255.0, strictHighS, 255.0) //strict higher bound HSV for yellow
        //apply strict HSV filter onto scaledMask to get rid of any yellow other than pole
        Core.inRange(scaledMask, strictLowHSV, strictHighHSV, scaledThresh)
        val finalMask = Mat()
        //color in scaledThresh with HSV, output into finalMask(only useful for showing result)(you can delete)
        Core.bitwise_and(mat, mat, finalMask, scaledThresh)
        val edges = Mat()
        //detect edges(only useful for showing result)(you can delete)
        Imgproc.Canny(scaledThresh, edges, 100.0, 200.0)

        //contours, apply post processing to information
        val contours: List<MatOfPoint> = ArrayList()
        val hierarchy = Mat()
        //find contours, input scaledThresh because it has hard edges
        Imgproc.findContours(
            scaledThresh,
            contours,
            hierarchy,
            Imgproc.RETR_TREE,
            Imgproc.CHAIN_APPROX_SIMPLE
        )

        //list of frames to reduce inconsistency, not too many so that it is still real-time, change the number from 5 if you want
        if (frameList.size > 5) {
            frameList.removeAt(0)
        }


        //release all the data
        input.release()
        scaledThresh.copyTo(input)
        scaledThresh.release()
        scaledMask.release()
        mat.release()
        masked.release()
        edges.release()
        thresh.release()
        finalMask.release()
        //change the return to whatever mat you want
        //for example, if I want to look at the lenient thresh:
        // return thresh;
        // note that you must not do thresh.release() if you want to return thresh
        // you also need to release the input if you return thresh(release as much as possible)
        return input
    }

    companion object {
        //these are public static to be tuned in dashboard
        var strictLowS = 140.0
        var strictHighS = 255.0
    }
}