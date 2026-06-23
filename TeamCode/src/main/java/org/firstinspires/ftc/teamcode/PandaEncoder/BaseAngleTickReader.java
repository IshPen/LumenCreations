package org.firstinspires.ftc.teamcode.PandaEncoder;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Lumen: Find Base Offset", group = "Panda")
public class BaseAngleTickReader extends LinearOpMode {
    @Override
    public void runOpMode() {
        PandaEncoder enc = hardwareMap.get(PandaEncoder.class, "panda");

        int captured = -1;
        boolean prevA = false;

        telemetry.addLine("1) Hold the INPUT shaft at its physical zero reference.");
        telemetry.addLine("2) Press START, then press A to capture.");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            int raw = enc.readRawTicks();

            boolean a = gamepad1.a;
            if (a && !prevA) captured = raw;   // capture on the button's rising edge
            prevA = a;

            telemetry.addData("Magnet OK", enc.magnetOk());
            telemetry.addData("Live raw ticks (0-4095)", raw);
            telemetry.addLine();
            telemetry.addData("CAPTURED base offset",
                    captured < 0 ? "hold at zero, press A" : captured);
            telemetry.addLine(">> Record this number for THIS encoder <<");
            telemetry.update();
        }
    }
}