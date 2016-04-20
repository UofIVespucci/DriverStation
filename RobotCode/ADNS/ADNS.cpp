#include <SPI.h>
#include "ADNS.h"

ADNS::ADNS(uint8_t cs): CS_PIN(cs) {
}
uint8_t ADNS::read_register(uint8_t address) {
    digitalWrite(CS_PIN, CHIP_SELECTED);
    // send the device the register you want to read:
    SPI.transfer(address);
    delayMicroseconds(50);
    // send a value of 0 to read the first byte returned:
    uint8_t result = SPI.transfer(0);
    digitalWrite(CS_PIN, ~CHIP_SELECTED);
    return result;
}
void ADNS::write_register(uint8_t address, uint8_t value) {
    digitalWrite(CS_PIN, CHIP_SELECTED);
    SPI.transfer(address);
    delayMicroseconds(50);
    SPI.transfer(value);
    digitalWrite(CS_PIN, ~CHIP_SELECTED);
}
void ADNS::init() {
    int8_t retry = 0;
    boolean healthy = false;

    // get pointer to the spi bus
    while (!healthy && retry < 3) {
        if (read_register(ADNS3080_PRODUCT_ID) == 0x17) {
            healthy = true;
        }
        retry++;
    }

    if(!healthy) return;

    // set frame rate to manual
    uint8_t regVal = read_register(ADNS3080_EXTENDED_CONFIG);
    delayMicroseconds(50);
    regVal = (regVal & ~0x01) | 0x01;
    write_register(ADNS3080_EXTENDED_CONFIG, regVal);
    delayMicroseconds(50);

    // set frame period to 12000 (0x2EE0)
    write_register(ADNS3080_FRAME_PERIOD_MAX_BOUND_LOWER,0xE0);
    delayMicroseconds(50);
    write_register(ADNS3080_FRAME_PERIOD_MAX_BOUND_UPPER,0x2E);
    delayMicroseconds(50);

    // set 1600 resolution bit
    regVal = read_register(ADNS3080_CONFIGURATION_BITS);
    delayMicroseconds(50);
    regVal |= 0x10;
    write_register(ADNS3080_CONFIGURATION_BITS, regVal);
    delayMicroseconds(50);

}
void ADNS::update(void) {
    uint8_t motion_reg;
    int16_t raw_dx, raw_dy;

    // check for movement, update x,y values
    motion_reg = read_register(ADNS3080_MOTION);
    if ((motion_reg & 0x80) != 0) {
        raw_dx = ((int8_t)read_register(ADNS3080_DELTA_X));
        delayMicroseconds(50);
        raw_dy = ((int8_t)read_register(ADNS3080_DELTA_Y));
    }else{
        raw_dx = 0;
        raw_dy = 0;
    }

    track.x += raw_dx;
    track.y += raw_dy;
}
void ADNS::clear_motion() {
    track.x = 0;
    track.y = 0;
}
Track ADNS::get_motion() {
    return track;
}
