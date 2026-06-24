package org.firstinspires.ftc.teamcode.PandaEncoder;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Lumen: Absolute Test", group = "Lumen")
public class PandaEncoderAbsolute extends LinearOpMode {

    // Paste the value from "Find Base Offset" for THIS encoder:
    static final int BASE_TICK_OFFSET = 2535;   // <-- replace per encoder

    @Override
    public void runOpMode() {
        PandaEncoder enc = hardwareMap.get(PandaEncoder.class, "panda");

        enc.setMode(PandaEncoder.Mode.ABSOLUTE);
        enc.setBaseTickOffset(BASE_TICK_OFFSET);

        telemetry.addLine("Absolute mode — reports true position immediately,");
        telemetry.addLine("and the same value after any stop / re-init.");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("Magnet OK", enc.magnetOk());
            telemetry.addData("Absolute (0-360)",     "%.1f", enc.getAbsoluteDegrees());
            telemetry.addData("Absolute (-180..180)", "%.1f", enc.getAbsoluteDegreesSigned());
            telemetry.update();
        }
    }
}