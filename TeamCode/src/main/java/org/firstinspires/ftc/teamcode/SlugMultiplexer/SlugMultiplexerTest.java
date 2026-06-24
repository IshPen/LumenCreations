package org.firstinspires.ftc.teamcode.SlugMultiplexer;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.PandaEncoder.PandaEncoder;

@TeleOp(name = "Mux + PandaEncoder Test", group = "test")
public class SlugMultiplexerTest extends LinearOpMode {

    @Override
    public void runOpMode() {
        SlugMultiplexer mux = hardwareMap.get(SlugMultiplexer.class, "mux");

        // four PandaEncoders, all configured at 0x36, distinguished only by mux channel
        PandaEncoder[] enc = {
                hardwareMap.get(PandaEncoder.class, "Panda1"),
                hardwareMap.get(PandaEncoder.class, "Panda2"),
        };
        int[] channel = {0, 1}; // which mux channel each encoder is wired to

        // ---- bring-up: prove each channel responds ----
        telemetry.addLine("Checking each channel for a magnet...");
        for (int i = 0; i < enc.length; i++) {
            mux.selectChannel(channel[i]);          // connect this sub-bus FIRST
            telemetry.addData("ch " + channel[i] + " magnet", enc[i].magnetDetected());
        }
        telemetry.addLine();
        telemetry.addLine("Press START.");
        telemetry.update();
        waitForStart();

        ElapsedTime loopTimer = new ElapsedTime();

        while (opModeIsActive()) {
            loopTimer.reset();

            for (int i = 0; i < enc.length; i++) {
                mux.selectChannel(channel[i]);      // 1) select, then
                double heading = enc[i].getAbsoluteDegrees(); // 2) read — only ch[i] answers
                telemetry.addData("enc " + i + " (deg)", "%.1f", heading);
            }

            telemetry.addData("loop read time (ms)", "%.2f", loopTimer.milliseconds());
            telemetry.update();
        }
    }
}