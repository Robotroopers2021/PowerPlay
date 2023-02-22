package teamcode.v1.commands.sequences

import com.asiankoala.koawalib.command.commands.InstantCmd
import com.asiankoala.koawalib.command.commands.WaitCmd
import com.asiankoala.koawalib.command.group.SequentialGroup
import teamcode.v1.auto.AutoRobot
import teamcode.v1.commands.subsystems.ClawCmds

class AutoDepositSequence(
        robot : AutoRobot,
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
)