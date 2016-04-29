#pragma once
#include <avr/interrupt.h>
#include <avr/io.h>
#include <util/atomic.h>
#include <stdio.h>
#include <stdarg.h>

namespace T2SlowPulse{
    bool inverted = false;
    uint8_t outBit;
    volatile uint8_t * outReg;

    ISR(TIMER2_COMPB_vect){
        //flip the output
        *outReg = *outReg ^ outBit;
    }

    ISR(TIMER2_COMPA_vect){
        //set the output to `inverted`
        *outReg = (*outReg | outBit) ^ outBit*inverted;
    }
    /** Start pulsing `pin`; global interrupts must be enabled */
    void begin(uint8_t pin){
        ATOMIC_BLOCK(ATOMIC_RESTORESTATE){
            /*
            Configure timer2 with the slowest prescalar (1 tick per 1024 clocks)
            in CTC mode (reset counter when equal to OCR2A)
            and set to generate and interrupt at OCR2A and ORC2A
            */
            //CTC mode, no direct outputs
            TCCR2A = (0 | _BV(WGM21));
            //1024 bit prescalar
            TCCR2B = (0 | _BV(CS22) | _BV(CS21) | _BV(CS20));
            //OCIE2* -> interrupt when TCNT2 == OCR2*
            TIMSK2 = (0 | _BV(OCIE2B) | _BV(OCIE2A));

            OCR2A = 255;
            OCR2B = 30;
        }
        pinMode(pin, OUTPUT);
        outBit = digitalPinToBitMask(pin);
        outReg = portOutputRegister(digitalPinToPort(pin));
    }
    /* set the pulse width interval and if it starts with HIGH or LOW (inverted)
     * upTime must be less than period
     * each `tick` is ~64 microseconds
     * the maximum period (255) is 16.3 milliseconds
    **/
    void pulseWidth(uint8_t upTime, uint8_t period, bool inv){
        OCR2B = upTime;
        OCR2A = period;
        inverted = inv;
    }
}
