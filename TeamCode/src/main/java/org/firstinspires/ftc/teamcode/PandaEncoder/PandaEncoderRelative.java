package org.firstinspires.ftc.teamcode.PandaEncoder;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Lumen: Relative Test", group = "Lumen")
public class PandaEncoderRelative extends LinearOpMode {
    @Override
    public void runOpMode() {
        PandaEncoder enc = hardwareMap.get(PandaEncoder.class, "panda");

        enc.setMode(PandaEncoder.Mode.RELATIVE);   // resets ticks to zero on init

        telemetry.addLine("Relative mode — ticks zeroed on init.");
        telemetry.addLine("Press START, turn the shaft. (B re-zeroes.)");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            enc.update();   // tracks across wraps

            telemetry.addData("Magnet OK", enc.magnetOk());
            telemetry.addData("Revolutions", "%.3f", enc.getRelativeRevolutions());
            telemetry.addData("Degrees",     "%.1f", enc.getRelativeDegrees());
            telemetry.addData("(raw tick total)", enc.getRelativeTicks());

            if (gamepad1.b) enc.resetRelative();
            telemetry.update();
        }
    }
}