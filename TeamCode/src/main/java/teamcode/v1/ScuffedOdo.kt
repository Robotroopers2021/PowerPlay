package teamcode.v1

import com.asiankoala.koawalib.hardware.motor.KEncoder
import com.asiankoala.koawalib.logger.Logger
import com.asiankoala.koawalib.math.Pose
import com.asiankoala.koawalib.subsystem.odometry.Odometry
import teamcode.v1.constants.OdoConstants
import kotlin.math.absoluteValue

class ScuffedOdo(
    private val leftEnc: KEncoder,
    private val rightEnc: KEncoder,
    private val perpEnc: KEncoder,
    private val LEFT_OFFSET: Double,
    private val RIGHT_OFFSET: Double,
    private val PERP_OFFSET: Double,
    startPose: Pose
) : Odometry(startPose) {
    /**
     * Secondary constructor. Uses [TRACK_WIDTH] to calculate [LEFT_OFFSET] and [RIGHT_OFFSET].
     * Assumes that the parallel pods are dispersed evenly.
     * @param[TRACK_WIDTH] Distance between the parallel odo pods.
     */
    constructor(
        leftEnc: KEncoder,
        rightEnc: KEncoder,
        perpEnc: KEncoder,
        TRACK_WIDTH: Double,
        PERP_OFFSET: Double,
        startPose: Pose
    ) : this(
        leftEnc,
        rightEnc,
        perpEnc,
        TRACK_WIDTH / 2.0,
        -TRACK_WIDTH / 2.0,
        PERP_OFFSET,
        startPose
    )

    private var encoders = listOf(leftEnc, rightEnc, perpEnc)
    private val radius2 = LEFT_OFFSET - RIGHT_OFFSET
    private var accumulatedLeftTheta = 0.0
    private var accumulatedRightTheta = 0.0
    private var accumulatedAuxDelta = 0.0

    override fun updateTelemetry() {
        Logger.addTelemetryData("start pose", startPose)
        Logger.addTelemetryData("curr pose", pose)
        Logger.addTelemetryData("left encoder", leftEnc.pos * OdoConstants.offset_multiplier)
        Logger.addTelemetryData("right encoder", rightEnc.pos)
        Logger.addTelemetryData("perp encoder", perpEnc.pos)
        Logger.addTelemetryData("delta tracker", accumulatedAuxDelta)
        Logger.addTelemetryData("accumulated left theta", accumulatedLeftTheta)
        Logger.addTelemetryData("accumulated right theta", accumulatedRightTheta)
    }

    override fun reset(p: Pose) {
        encoders.forEach(KEncoder::zero)
        pose = p
        startPose = p
    }

    override fun periodic() {
        encoders.forEach(KEncoder::update)
        val ldt = leftEnc.delta
        val rdt = rightEnc.delta
        val dtheta = (ldt * OdoConstants.offset_multiplier - rdt) / radius2
        val dy = (LEFT_OFFSET * rdt - RIGHT_OFFSET * ldt) / radius2
        val dx = perpEnc.delta - dtheta * PERP_OFFSET
        pose = exp(pose, Pose(dx, dy, dtheta))

        accumulatedLeftTheta += ldt
        accumulatedRightTheta += rdt
        accumulatedAuxDelta += dx.absoluteValue
    }
}
