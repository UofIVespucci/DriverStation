#pragma once

class TB66Chan{
    const uint8_t pwmPin;
    const uint8_t inA;
    const uint8_t inB;
public:
    TB66Chan(uint8_t pwmPin, uint8_t inA, uint8_t inB):
            pwmPin(pwmPin), inA(inA), inB(inB) {
    }
    void begin(){
        pinMode(pwmPin, OUTPUT);
        pinMode(inA, OUTPUT);
        pinMode(inB, OUTPUT);
    }
    void setPower(int16_t power) {
        bool direction = (power < 0);
        digitalWrite(inA,  direction);
        digitalWrite(inB, !direction);
        analogWrite(pwmPin, abs(power));
    }
};
