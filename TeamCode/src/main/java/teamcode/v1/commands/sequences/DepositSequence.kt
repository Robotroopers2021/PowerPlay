package teamcode.v1.commands.sequences

import com.asiankoala.koawalib.command.commands.ChooseCmd
import com.asiankoala.koawalib.command.commands.InstantCmd
import com.asiankoala.koawalib.command.commands.WaitCmd
import com.asiankoala.koawalib.command.commands.WaitUntilCmd
import com.asiankoala.koawalib.command.group.ParallelGroup
import com.asiankoala.koawalib.command.group.SequentialGroup
import org.apache.commons.lang3.ObjectUtils.Null
import teamcode.v1.Robot
import teamcode.v1.commands.subsystems.ClawCmds
import teamcode.v1.vision.PoleVision

class DepositSequence(
    robot : Robot,
    armAngle : Double,
    LiftHeight : Double,
    GripPos : Double,
) : SequentialGroup(
    ParallelGroup(
    InstantCmd({robot.claw.countUp()}),
    ClawCmds.ClawCloseCmd(robot.claw),
    ),
    InstantCmd({robot.arm.setPos(armAngle)}),
    WaitCmd(0.3),
    InstantCmd({robot.lift.setPos(LiftHeight)}),
    WaitCmd(0.1),
    InstantCmd({robot.guide.setPos(GripPos)}),
    InstantCmd(robot.vision::start),
    WaitUntilCmd{ !robot.vision.pose.tvec.empty() && (robot.vision.pose.tvec[0, 0][0] < 3.0 && robot.vision.pose.tvec[0, 0][0] > 0) },
    InstantCmd({robot.lift.setPos(LiftHeight-5)}),
    InstantCmd(robot.vision::stop)
)