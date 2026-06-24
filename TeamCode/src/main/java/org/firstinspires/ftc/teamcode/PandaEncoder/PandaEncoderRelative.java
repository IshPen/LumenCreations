package org.firstinspires.ftc.teamcode.PandaEncoder;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.SlugMultiplexer.SlugMultiplexer;

@TeleOp(name = "Lumen: Relative Test", group = "Lumen")
public class PandaEncoderRelative extends LinearOpMode {
    @Override
    public void runOpMode() {
        SlugMultiplexer mux = hardwareMap.get(SlugMultiplexer.class, "mux");

        PandaEncoder[] encoders = {
                hardwareMap.get(PandaEncoder.class, "panda1")
        };
        mux.selectChannel(0);

        PandaEncoder enc = encoders[0];
        enc.setMode(PandaEncoder.Mode.RELATIVE);   // resets ticks to zero on init

        telemetry.addLine("Relative mode — ticks zeroed on init.");
        telemetry.addLine("Press START, turn the shaft. (B re-zeroes.)");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            enc.update();   // tracks across wraps

            telemetry.addData("Magnet OK", enc.magnetOk());
            telemetry.addData("Revolutions", "%.3f", enc.getRelativeRevolutions());
            telemetry.addData("Degrees", "%.1f", enc.getRelativeDegrees());
            telemetry.addData("(raw tick total)", enc.getRelativeTicks());

            if (gamepad1.b) enc.resetRelative();
            telemetry.update();
        }
    }
}