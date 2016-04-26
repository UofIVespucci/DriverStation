#pragma once
// timer process runs at 1khz.  50 iterations = 20hz
#define ADNS_NUM_CALLS_FOR_20HZ          50

// orientations for ADNS3080 sensor
#define ADNS_PINS_FORWARD ROTATION_YAW_180
#define ADNS_PINS_FORWARD_RIGHT ROTATION_YAW_135
#define ADNS_PINS_RIGHT ROTATION_YAW_90
#define ADNS_PINS_BACK_RIGHT ROTATION_YAW_45
#define ADNS_PINS_BACK ROTATION_NONE
#define ADNS_PINS_BACK_LEFT ROTATION_YAW_315
#define ADNS_PINS_LEFT ROTATION_YAW_270
#define ADNS_PINS_FORWARD_LEFT ROTATION_YAW_225

// field of view of ADNS3080 sensor lenses
#define ADNS_08_FOV 0.202458f        // 11.6 degrees

// scaler - value returned when sensor is moved equivalent of 1 pixel
#define ADNS_SCALER_400   1.1f       // when resolution set to 400
#define ADNS_SCALER_1600  4.4f       // when resolution set to 1600

// ADNS3080 hardware config
#define ADNS3080_PIXELS_X               30
#define ADNS3080_PIXELS_Y               30
#define ADNS3080_CLOCK_SPEED            24000000

// Register Map for the ADNS3080 Optical OpticalFlow Sensor
#define ADNS3080_PRODUCT_ID            0x00
#define ADNS3080_REVISION_ID           0x01
#define ADNS3080_MOTION                0x02
#define ADNS3080_DELTA_X               0x03
#define ADNS3080_DELTA_Y               0x04
#define ADNS3080_SQUAL                 0x05
#define ADNS3080_PIXEL_SUM             0x06
#define ADNS3080_MAXIMUM_PIXEL         0x07
#define ADNS3080_CONFIGURATION_BITS    0x0a
#define ADNS3080_EXTENDED_CONFIG       0x0b
#define ADNS3080_DATA_OUT_LOWER        0x0c
#define ADNS3080_DATA_OUT_UPPER        0x0d
#define ADNS3080_SHUTTER_LOWER         0x0e
#define ADNS3080_SHUTTER_UPPER         0x0f
#define ADNS3080_FRAME_PERIOD_LOWER    0x10
#define ADNS3080_FRAME_PERIOD_UPPER    0x11
#define ADNS3080_MOTION_CLEAR          0x12
#define ADNS3080_FRAME_CAPTURE         0x13
#define ADNS3080_SROM_ENABLE           0x14
#define ADNS3080_FRAME_PERIOD_MAX_BOUND_LOWER      0x19
#define ADNS3080_FRAME_PERIOD_MAX_BOUND_UPPER      0x1a
#define ADNS3080_FRAME_PERIOD_MIN_BOUND_LOWER      0x1b
#define ADNS3080_FRAME_PERIOD_MIN_BOUND_UPPER      0x1c
#define ADNS3080_SHUTTER_MAX_BOUND_LOWER           0x1e
#define ADNS3080_SHUTTER_MAX_BOUND_UPPER           0x1e
#define ADNS3080_SROM_ID               0x1f
#define ADNS3080_OBSERVATION           0x3d
#define ADNS3080_INVERSE_PRODUCT_ID    0x3f
#define ADNS3080_PIXEL_BURST           0x40
#define ADNS3080_MOTION_BURST          0x50
#define ADNS3080_SROM_LOAD             0x60

// Extended Configuration bits
#define ADNS3080_SERIALNPU_OFF  0x02

#define CHIP_SELECTED LOW

class Track{ public: int x,y; };

class ADNS {
private:
    uint8_t read_register(uint8_t address);
    void    write_register(uint8_t address, uint8_t value);
    Track track;
    const uint8_t CS_PIN;
public:
    ADNS(uint8_t cs);
    void init();
    // read latest values from sensor and fill in x,y and totals,
    // returns true on successful read
    void update(void);
    // will cause the x,y, dx, dy, and the sensor's motion registers to
    // be cleared
    void clear_motion();
    Track get_motion();
};
