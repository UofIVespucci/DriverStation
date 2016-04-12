#pragma once

#include <inttypes.h>
#include <avr/interrupt.h>
#include <avr/io.h>
#include <util/atomic.h>

class encoder{
private:
	static int8_t  pin[2];
	static volatile int16_t leftTicks, rightTicks;
	static void interrupt(void){
		leftTicks++;
	}
	static void Binterrupt(void){
		rightTicks++;
	}
public:
	static void begin(uint8_t pinA){
		if(digitalPinToInterrupt(pinA)==NOT_AN_INTERRUPT) return;

		pin[0] = pinA;
		pin[1] = -1;

		ATOMIC_BLOCK(ATOMIC_RESTORESTATE){
			attachInterrupt(digitalPinToInterrupt(pin[0]),
							&encoder::interrupt, RISING);
		}
	}
	static void begin(uint8_t pinA, uint8_t pinB){
		if(digitalPinToInterrupt(pinA)==NOT_AN_INTERRUPT) return;
		if(digitalPinToInterrupt(pinB)==NOT_AN_INTERRUPT) return;

		pin[0] = pinA;
		pin[1] = pinB;

		ATOMIC_BLOCK(ATOMIC_RESTORESTATE){
			attachInterrupt(digitalPinToInterrupt(pin[0]),
							&encoder::interrupt, RISING);
			attachInterrupt(digitalPinToInterrupt(pin[1]),
							&encoder::Binterrupt, RISING);
		}
	}
	static int16_t getLeft(){
		return leftTicks;
	}
	static int16_t getRight(){
		return rightTicks;
	}
};
//static var initializations
int8_t   encoder::pin[] = {-1};
volatile int16_t  encoder::leftTicks = 0;
volatile int16_t  encoder::rightTicks = 0;
