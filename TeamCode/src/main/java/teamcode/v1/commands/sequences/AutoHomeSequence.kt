package teamcode.v1.commands.sequences

import com.asiankoala.koawalib.command.commands.InstantCmd
import com.asiankoala.koawalib.command.commands.WaitCmd
import com.asiankoala.koawalib.command.group.ParallelGroup
import teamcode.v1.auto.AutoRobot
import teamcode.v1.commands.subsystems.ClawCmds
import teamcode.v1.constants.GuideConstants

class AutoHomeSequence(
    robot : AutoRobot,
    firstArmAngle : Double,
    liftHeight : Double,
    GripPos : Double
) : ParallelGroup(
    ParallelGroup(
        InstantCmd({robot.arm.setPos(firstArmAngle)}),
        ClawCmds.ClawCloseCmd(robot.claw),
        InstantCmd({robot.guide.setPos(GripPos)}),
    ),
    InstantCmd({robot.lift.setPos(liftHeight)}),
    WaitCmd(0.5),
    ClawCmds.ClawOpenCmd(robot.claw, robot.guide, GuideConstants.telePos)
)

