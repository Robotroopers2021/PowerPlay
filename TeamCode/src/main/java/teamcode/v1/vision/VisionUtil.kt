package teamcode.v1.vision

import org.opencv.calib3d.Calib3d
import org.opencv.core.Mat
import org.opencv.core.MatOfDouble
import org.opencv.core.MatOfPoint2f
import org.opencv.core.MatOfPoint3f
import org.opencv.core.Point
import org.opencv.core.Point3
import org.opencv.imgproc.Imgproc

object VisionUtil {

    @JvmStatic
    fun poseFromTrapezoid(
        points: Array<Point?>,
        cameraMatrix: Mat?,
        tagsizeX: Double,
        tagsizeY: Double
    ): Pose {
        // The actual 2d points of the tag detected in the image
        val points2d = MatOfPoint2f(*points)

        // The 3d points of the tag in an 'ideal projection'
        val arrayPoints3d = arrayOfNulls<Point3>(4)
        arrayPoints3d[0] = Point3(-tagsizeX / 2, tagsizeY / 2, 0.0)
        arrayPoints3d[1] = Point3(tagsizeX / 2, tagsizeY / 2, 0.0)
        arrayPoints3d[2] = Point3(tagsizeX / 2, -tagsizeY / 2, 0.0)
        arrayPoints3d[3] = Point3(-tagsizeX / 2, -tagsizeY / 2, 0.0)
        val points3d = MatOfPoint3f(*arrayPoints3d)

        // Using this information, actually solve for pose
        val pose = Pose()
        Calib3d.solvePnP(
            points3d,
            points2d,
            cameraMatrix,
            MatOfDouble(),
            pose.rvec,
            pose.tvec,
            false
        )


        return pose
    }

    /**
     * Draw a 3D axis marker on a detection. (Similar to what Vuforia does)
     *
     * @param buf the RGB buffer on which to draw the marker
     * @param length the length of each of the marker 'poles'
     * @param rvec the rotation vector of the detection
     * @param tvec the translation vector of the detection
     * @param cameraMatrix the camera matrix used when finding the detection
     */
    @JvmStatic
    fun drawAxisMarker(
        buf: Mat?,
        length: Double,
        thickness: Int,
        rvec: Mat?,
        tvec: Mat?,
        cameraMatrix: Mat?
    ) {
        // The points in 3D space we wish to project onto the 2D image plane.
        // The origin of the coordinate space is assumed to be in the center of the detection.
        val axis = MatOfPoint3f(
            Point3(0.0, 0.0, 0.0),
            Point3(length, 0.0, 0.0),
            Point3(0.0, length, 0.0),
            Point3(0.0, 0.0, -length)
        )

        // Project those points
        val matProjectedPoints = MatOfPoint2f()
        Calib3d.projectPoints(axis, rvec, tvec, cameraMatrix, MatOfDouble(), matProjectedPoints)
        val projectedPoints = matProjectedPoints.toArray()

        // Draw the marker!
        Imgproc.line(
            buf,
            projectedPoints[0], projectedPoints[1], PolePipeline.red, thickness
        )
        Imgproc.line(
            buf,
            projectedPoints[0], projectedPoints[2], PolePipeline.green, thickness
        )
        Imgproc.line(
            buf,
            projectedPoints[0], projectedPoints[3], PolePipeline.blue, thickness
        )
        Imgproc.circle(buf, projectedPoints[0], thickness, PolePipeline.white, -1)
    }


    @JvmStatic
    fun draw3dCubeMarker(
        buf: Mat?,
        length: Double,
        tagWidth: Double,
        tagHeight: Double,
        thickness: Int,
        rvec: Mat?,
        tvec: Mat?,
        cameraMatrix: Mat?
    ) {
        //axis = np.float32([[0,0,0], [0,3,0], [3,3,0], [3,0,0],
        //       [0,0,-3],[0,3,-3],[3,3,-3],[3,0,-3] ])

        // The points in 3D space we wish to project onto the 2D image plane.
        // The origin of the coordinate space is assumed to be in the center of the detection.
        val axis = MatOfPoint3f(
            Point3(-tagWidth / 2, tagHeight / 2, 0.0),
            Point3(tagWidth / 2, tagHeight / 2, 0.0),
            Point3(tagWidth / 2, -tagHeight / 2, 0.0),
            Point3(-tagWidth / 2, -tagHeight / 2, 0.0),
            Point3(-tagWidth / 2, tagHeight / 2, -length),
            Point3(tagWidth / 2, tagHeight / 2, -length),
            Point3(tagWidth / 2, -tagHeight / 2, -length),
            Point3(-tagWidth / 2, -tagHeight / 2, -length)
        )

        // Project those points
        val matProjectedPoints = MatOfPoint2f()
        Calib3d.projectPoints(axis, rvec, tvec, cameraMatrix, MatOfDouble(), matProjectedPoints)
        val projectedPoints = matProjectedPoints.toArray()

        // Pillars
        for (i in 0..3) {
            Imgproc.line(
                buf,
                projectedPoints[i],
                projectedPoints[i + 4],
                PolePipeline.blue,
                thickness
            )
        }

        // Base lines
        //Imgproc.line(buf, projectedPoints[0], projectedPoints[1], blue, thickness);
        //Imgproc.line(buf, projectedPoints[1], projectedPoints[2], blue, thickness);
        //Imgproc.line(buf, projectedPoints[2], projectedPoints[3], blue, thickness);
        //Imgproc.line(buf, projectedPoints[3], projectedPoints[0], blue, thickness);

        // Top lines
        Imgproc.line(
            buf,
            projectedPoints[4], projectedPoints[5], PolePipeline.green, thickness
        )
        Imgproc.line(
            buf,
            projectedPoints[5], projectedPoints[6], PolePipeline.green, thickness
        )
        Imgproc.line(
            buf,
            projectedPoints[6], projectedPoints[7], PolePipeline.green, thickness
        )
        Imgproc.line(
            buf,
            projectedPoints[4], projectedPoints[7], PolePipeline.green, thickness
        )
    }
}