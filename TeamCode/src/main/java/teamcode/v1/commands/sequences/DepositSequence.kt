package teamcode.v1.commands.sequences

import com.asiankoala.koawalib.command.commands.ChooseCmd
import com.asiankoala.koawalib.command.commands.InstantCmd
import com.asiankoala.koawalib.command.commands.WaitCmd
import com.asiankoala.koawalib.command.group.SequentialGroup
import teamcode.v1.Robot
import teamcode.v1.commands.subsystems.ClawCmds
import teamcode.v1.vision.PoleVision

private var vision = PoleVision()
class DepositSequence(
    robot : Robot,
    armAngle : Double,
    LiftHeight : Double,
    GripPos : Double,
) : SequentialGroup(
    ClawCmds.ClawCloseCmd(robot.claw),
    InstantCmd({robot.arm.setPos(armAngle)}),
    WaitCmd(0.3),
    InstantCmd({robot.lift.setPos(LiftHeight)}),
    WaitCmd(0.1),
    InstantCmd({robot.guide.setPos(GripPos)}),
    InstantCmd({vision.start()}),
    ChooseCmd(InstantCmd({robot.lift.setPos(LiftHeight-2)}), WaitCmd(0.0)) { vision.pose.tvec[2, 0][0] < 3.0 },
    InstantCmd({vision.stop()})
)