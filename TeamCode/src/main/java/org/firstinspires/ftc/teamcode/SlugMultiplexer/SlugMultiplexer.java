package org.firstinspires.ftc.teamcode.SlugMultiplexer;

import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice;
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties;
import com.qualcomm.robotcore.hardware.configuration.annotations.I2cDeviceType;

@I2cDeviceType
@DeviceProperties(name = "Slug I2C Mux", xmlTag = "TCA9548A")
public class SlugMultiplexer extends I2cDeviceSynchDevice<I2cDeviceSynch> {

    // Default mux address. A0/A1/A2 pins shift it across 0x70..0x77.
    private static final I2cAddr ADDRESS = I2cAddr.create7bit(0x70);

    private int currentChannel = -1; // remembered so we can skip redundant selects

    public SlugMultiplexer(I2cDeviceSynch deviceClient) {
        super(deviceClient, true);
        this.deviceClient.setI2cAddress(ADDRESS);
        super.registerArmingStateCallback(false);
        this.deviceClient.engage();
    }

    /**
     * Connect exactly one downstream channel (0..7) to the hub, disconnect the rest.
     * The control register is just a bitmask: bit N = channel N.
     */
    public void selectChannel(int channel) {
        if (channel == currentChannel) return;   // already selected, skip the write
        deviceClient.write8(0x00, 1 << channel);  // last byte written becomes the control reg
        currentChannel = channel;
    }

    /** Disconnect all channels (rarely needed, but handy for a clean state). */
    public void disableAll() {
        deviceClient.write8(0x00, 0x00);
        currentChannel = -1;
    }

    @Override protected boolean doInitialize() { return true; }
    @Override public Manufacturer getManufacturer() { return Manufacturer.Other; }
    @Override public String getDeviceName() { return "TCA9548A I2C Multiplexer"; }
}