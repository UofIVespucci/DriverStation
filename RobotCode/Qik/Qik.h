#ifndef QIK_H
#define QIK_H

#include "Stream.h"


/**
 * Communicates with a Qix 2s9v1 pololu motor controller using the compact
 * serial protocol
 * https://www.pololu.com/docs/0J25
 */


enum DriveMode { _32KHZ = 0, _16KHS = 1, _8KHS = 2, _4KHS = 3 };
enum ErrorMode { _CONTINUE = 0, _SHUTDOWN = 1 };
enum CoastMode { _COAST, _BRAKE };

namespace{
    //Commands
    const uint8_t ForwardLow[]  = {0x88, 0x8C};
    const uint8_t ForwardHigh[] = {0x89, 0x8D};
    const uint8_t ReverseLow[]  = {0x8A, 0x8E};
    const uint8_t ReverseHigh[] = {0x8B, 0x8F};
    const uint8_t MotorCoast[]  = {0x86, 0x87};
    const uint8_t BaudStartVal  =  0xAA;
    const uint8_t PollCommand   =  0x83;
    const uint8_t SetCommand    =  0x84;
    const uint8_t SetFooter[]   = {0x55, 0x2A};

    //paramater ID's
    enum Parameter { _DeviceID = 0, _DriveMode = 1,
                     _ErrorMode = 2, _SerialTimeout = 3};
}

class Qik{
private:
    static const uint32_t DefaultTimeout = 100; //milliseconds

    Stream* comms;
    CoastMode coast;
    boolean sevenBitPwm;
    /** wait up to `timeout` milliseconds for a response byte */
    uint8_t getResponse(uint32_t timeout){
        for(uint32_t t = millis()+timeout; t<millis(); /*nop*/){
            if(comms->available()) return comms->read();
        }
        return 0;
    }
    /** clear the buffer of any stale inbound bytes */
    void clearComms(){
        while(comms->available()) {
            comms->read();
        }
    }
    /** polls the Qik's value for `param` */
    uint8_t getParameter(Parameter param){
        clearComms();
        comms->write(PollCommand);
        comms->write(param & 0x7F); //high bit 0 in non-command values
        return getResponse(DefaultTimeout);
    }
    /**
     * sets the Qik's value for `param` to `value`
     * Response 0 => success, 1 => bad parameter, 2 => bad value
     */
    uint8_t setParameter(Parameter param, uint8_t value){
        clearComms();
        comms->write(SetCommand);
        comms->write(param & 0x7F); //high bit 0 in non-command values
        comms->write(value & 0x7F);
        comms->write(SetFooter,2);
        return getResponse(DefaultTimeout);
    }
    /** Set the sevenBitPwm field according to the DriveMode `m` */
    void updatePwmBitrate(DriveMode m){
        sevenBitPwm = ((m & 0x01) == 0);
    }
    /**
     * Command motor `select` to run at `power` taking current configuration
     *     into account
     */
    void commandMotor(uint8_t select, int8_t power){
        boolean reverse = (power < 0);
        boolean stopped = (power == 0);
        uint8_t pwmSet  = min(abs(power), 127);

        //send coast command
        if(stopped && coast == _COAST){
            comms->write(MotorCoast[select]);
            return;
        }

        if(sevenBitPwm){
            if(reverse) {
                comms->write(ReverseLow[select]);
            } else {
                comms->write(ForwardLow[select]);
            }
            comms->write(pwmSet);
        } else {
            pwmSet *= 2;
            if(reverse){
                if(pwmSet <= 127) {
                    comms->write(ReverseLow[select]);
                } else {
                    comms->write(ReverseHigh[select]);
                }
            } else {
                if(pwmSet <= 127) {
                    comms->write(ForwardLow[select]);
                } else {
                    comms->write(ForwardHigh[select]);
                }
            }
            if(pwmSet > 127) pwmSet = min(pwmSet-127, 127);
            comms->write(pwmSet);
        }
    }
public:
    Qik(Stream* comms): comms(comms), coast(_BRAKE), sevenBitPwm(true) {}
    /**
     * Enable the Qik by sending the baud rate detection character
     * Must be called before the Qik can be used unless the static baud jumper
     * is enabled on the board.
     */
    void enable(){
        comms->write(BaudStartVal);
        updatePwmBitrate((DriveMode)getParameter(_DriveMode));
    }
    /**
     * Set motor power and direction with a signed 8 bit number,
     *  + is forword - is reverse
     *  7 bit speed resolution
     */
    void setMotors(int8_t M0, int8_t M1){
        commandMotor(0, M0);
        commandMotor(1, M1);
    }
    void setM0(int8_t power){
        commandMotor(0, power);
    }
    void setM1(int8_t power){
        commandMotor(1, power);
    }
    /**
     * When a motor is set to stop (powen = 0), should it coast or brake
     * defaults to _BRAKE
     */
    void setCoastMode(CoastMode m){
        coast = m;
    }
    /**
     * Sets the frequency and resolution of the PWM signal used to drive the
     * motors. High frequency decreases motor noise, low frequency decreases
     * power losses due to switching.
     */
    void setDriveMode(DriveMode m){
        setParameter(_DriveMode, m);
    }
    /**
     * Set the response when a serial error occurs
     * examples are serial timeout error or unreadable serial command
     * The Qik saves this in nonvolatile memory, but defaults to _SHUTDOWN
     */
    void setErrorMode(ErrorMode m){
        setParameter(_ErrorMode, m);
    }
    /**
     * Set the length of time (in seconds) that must occur with no new
     * commands being sent before the Qik generates a serial timeout error
     * set to 0 to disable serial timeout errors
     * The Qik saves this in nonvolatile memory, but defaults to 0
     * return: the actual timeout value after rounding to nearest valid value
     */
    float setSerialTimeout(float seconds){
        static const float TIMEOUT_FACT = 0.262f;
        static const float MAX_SECONDS  = 1920.0f;

        uint16_t units = min(fabs(seconds)/TIMEOUT_FACT, MAX_SECONDS);
        uint8_t highestSetBit = 0;
        for(int i = 0; i<16; i++){
            if((units & (1 << i)) != 0) {
                highestSetBit = i;
            }
        }
        uint8_t exp = min( max(highestSetBit, 4) - 4, 0x7 );
        uint8_t mantissa = (units>>exp) & 0xF;
        uint8_t time = (exp<<4) | mantissa;

        setParameter(_SerialTimeout, time);

        return TIMEOUT_FACT * (float) (mantissa * (1 << exp));
    }
};

#endif
