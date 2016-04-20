#include "ADNS.h"

ADNS::ADNS() {
    field_of_view = ADNS_08_FOV;
}
uint8_t ADNS::read_register(uint8_t address) {
    AP_HAL::Semaphore *spi_sem;

    // check that we have an spi bus
    if (_spi == NULL) {
        return 0;
    }

    // get spi bus semaphore
    spi_sem = _spi->get_semaphore();

    // try to get control of the spi bus
    if (spi_sem == NULL || !spi_sem->take_nonblocking()) {
        return 0;
    }

    _spi->cs_assert();
    // send the device the register you want to read:
    _spi->transfer(address);
    hal.scheduler->delay_microseconds(50);
    // send a value of 0 to read the first byte returned:
    uint8_t result = _spi->transfer(0x00);

    _spi->cs_release();

    // release the spi bus
    spi_sem->give();

    return result;
}
void ADNS::write_register(uint8_t address, uint8_t value) {
    AP_HAL::Semaphore *spi_sem;

    // check that we have an spi bus
    if (_spi == NULL) {
        return;
    }

    // get spi bus semaphore
    spi_sem = _spi->get_semaphore();

    // try to get control of the spi bus
    if (spi_sem == NULL || !spi_sem->take_nonblocking()) {
        return;
    }

    _spi->cs_assert();

    // send register address
    _spi->transfer(address | 0x80 );
    hal.scheduler->delay_microseconds(50);
    // send data
    _spi->transfer(value);

    _spi->cs_release();

    // release the spi bus
    spi_sem->give();
}
void ADNS::init() {
    int8_t retry = 0;
    _flags.healthy = false;

    // suspend timer while we set-up SPI communication
    hal.scheduler->suspend_timer_procs();

    // get pointer to the spi bus
    _spi = hal.spi->device(AP_HAL::SPIDevice_ADNS3080_SPI0);
    if (_spi != NULL) {
        // check 3 times for the sensor on standard SPI bus
        while (!_flags.healthy && retry < 3) {
            if (read_register(ADNS3080_PRODUCT_ID) == 0x17) {
                _flags.healthy = true;
            }
            retry++;
        }
    }

    // if not yet found, get pointer to the SPI3 bus
    if (!_flags.healthy) {
        _spi = hal.spi->device(AP_HAL::SPIDevice_ADNS3080_SPI3);
        if (_spi != NULL) {
            // check 3 times on SPI3
            retry = 0;
            while (!_flags.healthy && retry < 3) {
                if (read_register(ADNS3080_PRODUCT_ID) == 0x17) {
                    _flags.healthy = true;
                }
                retry++;
            }
        }
    }

    // configure the sensor
    if (_flags.healthy) {
        // set frame rate to manual
        uint8_t regVal = read_register(ADNS3080_EXTENDED_CONFIG);
        hal.scheduler->delay_microseconds(50);
        regVal = (regVal & ~0x01) | 0x01;
        write_register(ADNS3080_EXTENDED_CONFIG, regVal);
        hal.scheduler->delay_microseconds(50);

        // set frame period to 12000 (0x2EE0)
        write_register(ADNS3080_FRAME_PERIOD_MAX_BOUND_LOWER,0xE0);
        hal.scheduler->delay_microseconds(50);
        write_register(ADNS3080_FRAME_PERIOD_MAX_BOUND_UPPER,0x2E);
        hal.scheduler->delay_microseconds(50);

        // set 1600 resolution bit
        regVal = read_register(ADNS3080_CONFIGURATION_BITS);
        hal.scheduler->delay_microseconds(50);
        regVal |= 0x10;
        write_register(ADNS3080_CONFIGURATION_BITS, regVal);
        hal.scheduler->delay_microseconds(50);

        // update scalers
        update_conversion_factors();

        // register the global static read function to be called at 1khz
        hal.scheduler->register_timer_process(AP_HAL_MEMBERPROC(&ADNS::read));
    }else{
        // no connection available.
        _spi = NULL;
    }

    // resume timer
    hal.scheduler->resume_timer_procs();
}
void ADNS::update(void) {
    uint8_t motion_reg;
    int16_t  raw_dx, raw_dy;    // raw sensor change in x and y position (i.e. unrotated)
    surface_quality = read_register(ADNS3080_SQUAL);
    hal.scheduler->delay_microseconds(50);

    // check for movement, update x,y values
    motion_reg = read_register(ADNS3080_MOTION);
    if ((motion_reg & 0x80) != 0) {
        raw_dx = ((int8_t)read_register(ADNS3080_DELTA_X));
        hal.scheduler->delay_microseconds(50);
        raw_dy = ((int8_t)read_register(ADNS3080_DELTA_Y));
    }else{
        raw_dx = 0;
        raw_dy = 0;
    }

    last_update = hal.scheduler->millis();

    Vector3f rot_vector(raw_dx, raw_dy, 0);

    // rotate dx and dy
    rot_vector.rotate(_orientation);
    dx = rot_vector.x;
    dy = rot_vector.y;
}
// clear_motion - will cause the Delta_X, Delta_Y, and internal motion
// registers to be cleared
void ADNS::clear_motion() {
    // writing anything to this register will clear the sensor's motion
    // registers
    write_register(ADNS3080_MOTION_CLEAR,0xFF);
    x_cm = 0;
    y_cm = 0;
    dx = 0;
    dy = 0;
}
Track ADNS::get_motion() {

}
