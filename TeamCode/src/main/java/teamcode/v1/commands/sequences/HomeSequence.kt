package teamcode.v1.commands.sequences

import com.asiankoala.koawalib.command.commands.InstantCmd
import com.asiankoala.koawalib.command.commands.WaitCmd
import com.asiankoala.koawalib.command.group.ParallelGroup
import com.asiankoala.koawalib.command.group.SequentialGroup
import teamcode.v1.Robot
import teamcode.v1.commands.subsystems.ClawCmds
import teamcode.v1.constants.GuideConstants

class HomeSequence(
    robot : Robot,
    firstArmAngle : Double,
    secondArmAngle : Double,
    liftHeight : Double,
    GripPos : Double
) : ParallelGroup(
    SequentialGroup(
        InstantCmd({robot.arm.setPos(firstArmAngle)}),
        WaitCmd(1.0),
        ClawCmds.ClawOpenCmd(robot.claw, robot.guide, GuideConstants.telePos),
        InstantCmd({robot.arm.setPos(secondArmAngle)})),
        InstantCmd({robot.guide.setPos(GripPos)}),
    InstantCmd({robot.lift.setPos(liftHeight)}),
    InstantCmd(robot.lift::startAttemptingZero),
    ClawCmds.ClawCloseCmd(robot.claw),
    )

