package org.firstinspires.ftc.teamcode.PandaEncoder;

import com.qualcomm.robotcore.hardware.HardwareDevice.Manufacturer;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice;
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties;
import com.qualcomm.robotcore.hardware.configuration.annotations.I2cDeviceType;

@I2cDeviceType
@DeviceProperties(name = "Panda Encoder", xmlTag = "PandaEncoder")
public class PandaEncoder extends I2cDeviceSynchDevice<I2cDeviceSynch> {

    public enum Mode { ABSOLUTE, RELATIVE }

    // ---- AS5600 chip constants ----
    private static final I2cAddr ADDRESS   = I2cAddr.create7bit(0x36);
    private static final int REG_STATUS    = 0x0B;
    private static final int REG_RAW_ANGLE = 0x0C;          // 0x0C high, 0x0D low
    private static final int TICKS_PER_REV = 4096;          // 12-bit; 1:1 gearing -> also per INPUT rev

    // ---- per-unit calibration ----
    private int baseTickOffset = 0;   // raw tick read when the input shaft is at its zero reference

    // ---- mode + relative state ----
    private Mode mode = Mode.ABSOLUTE;
    private int  lastRaw = -1;
    private long totalTicks = 0;

    public PandaEncoder(I2cDeviceSynch deviceClient) {
        super(deviceClient, true);
        this.deviceClient.setI2cAddress(ADDRESS);
        super.registerArmingStateCallback(false);
        this.deviceClient.engage();
    }

    // ===== configuration =====
    public void setMode(Mode m) {
        this.mode = m;
        if (m == Mode.RELATIVE) resetRelative();   // relative always starts from zero
    }
    public Mode getMode() { return mode; }

    public void setBaseTickOffset(int ticks) { this.baseTickOffset = ticks; }
    public int  getBaseTickOffset() { return baseTickOffset; }

    // ===== raw read =====
    /** Raw position within one turn, 0..4095. Stateless — reflects the physical shaft right now. */
    public int readRawTicks() {
        byte[] d = deviceClient.read(REG_RAW_ANGLE, 2);
        int high = d[0] & 0xFF;
        int low  = d[1] & 0xFF;
        return ((high << 8) | low) & 0x0FFF;
    }

    // ===== ABSOLUTE mode (survives resets) =====
    /** Absolute shaft angle 0..360, referenced to the stored zero. Unambiguous over the full circle now. */
    public double getAbsoluteDegrees() {
        int t = ((readRawTicks() - baseTickOffset) % TICKS_PER_REV + TICKS_PER_REV) % TICKS_PER_REV;
        return t * 360.0 / TICKS_PER_REV;
    }

    /** Same angle as -180..+180, which steering controllers usually want. */
    public double getAbsoluteDegreesSigned() {
        double a = getAbsoluteDegrees();
        return (a > 180.0) ? a - 360.0 : a;
    }

    // ===== RELATIVE mode (multi-turn) =====
    /** Call every loop in relative mode so wraps are tracked into a running total. */
    public void update() {
        int raw = readRawTicks();
        if (lastRaw < 0) lastRaw = raw;
        int delta = raw - lastRaw;
        if (delta >  TICKS_PER_REV / 2) delta -= TICKS_PER_REV;
        if (delta < -TICKS_PER_REV / 2) delta += TICKS_PER_REV;
        totalTicks += delta;
        lastRaw = raw;
    }

    public double getRelativeRevolutions() { return totalTicks / (double) TICKS_PER_REV; }
    public double getRelativeDegrees()     { return totalTicks * 360.0 / TICKS_PER_REV; }
    public long   getRelativeTicks()       { return totalTicks; }
    public void   resetRelative()          { totalTicks = 0; lastRaw = -1; }

    // ===== magnet health =====
    public int     readStatus()      { return deviceClient.read8(REG_STATUS) & 0xFF; }
    public boolean magnetDetected()  { return (readStatus() & 0x20) != 0; }
    public boolean magnetTooWeak()   { return (readStatus() & 0x10) != 0; }
    public boolean magnetTooStrong() { return (readStatus() & 0x08) != 0; }
    public boolean magnetOk()        { int s = readStatus(); return (s & 0x20) != 0 && (s & 0x18) == 0; }

    // ===== required overrides =====
    @Override protected boolean doInitialize() { return true; }
    @Override public Manufacturer getManufacturer() { return Manufacturer.Other; }
    @Override public String getDeviceName() { return "Lumen Encoder (AS5600-based)"; }
}