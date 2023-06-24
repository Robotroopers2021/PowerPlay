package teamcode.v1.commands.subsystems

import com.asiankoala.koawalib.command.commands.InstantCmd
import com.asiankoala.koawalib.command.commands.WaitCmd
import com.asiankoala.koawalib.command.commands.WaitUntilCmd
import com.asiankoala.koawalib.command.group.SequentialGroup
import teamcode.v1.Robot
import teamcode.v1.constants.ClawConstants
import teamcode.v1.subsystems.Arm
import teamcode.v1.subsystems.Claw
import teamcode.v1.subsystems.Guide
import teamcode.v1.subsystems.Lift

class ClawCmds {
    open class ClawCmd(claw: Claw, pos: Double) : InstantCmd({ claw.setPos(pos) }, claw)

    class ClawCloseCmd(claw: Claw) : ClawCmd(claw, ClawConstants.closePos)

    class ClawOpenCmd(claw: Claw, guide : Guide, GripPos: Double) : SequentialGroup(
        ClawCmd(claw, ClawConstants.openPos),
        InstantCmd({guide.setPos(GripPos)})
    )

    class ClawSmartCmd(claw: Claw, lift: Lift, arm: Arm) : SequentialGroup(
        InstantCmd({claw.startReading()}),
        WaitUntilCmd {claw.lastRead < ClawConstants.sensorPos },
        InstantCmd({lift.setPos(0.0)}),
        WaitCmd(0.4),
        ClawCloseCmd(claw),
        WaitCmd(0.4),
        InstantCmd({arm.setPos(-63.0)}),
        WaitCmd(0.2),
        InstantCmd({lift.setPos(1.0)}),
        InstantCmd({claw.stopReading()})
    )
}