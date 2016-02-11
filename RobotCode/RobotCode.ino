#include "messages/MotorCommand.h"
#include "messages/Error.h"
#include "serial/Decoder.h"
#include "VespuChat.h"

#include "Qik/Qik.h"

#include <AltSoftSerial.h>
AltSoftSerial motorSerial;

Qik motor(&motorSerial);

extern VespuChat vespuChat;
MotorCommand mcmd([](uint8_t left, uint8_t right){
    MotorCommand::build(vespuChat, left, right); //echo command
    motor.setMotors((int8_t)left, (int8_t)right);
});
Error ecmd([](uint8_t code){
    Error::build(vespuChat, code);
});
Receiver* receivers[] = { &mcmd, &ecmd };
VespuChat vespuChat(Serial, receivers, sizeof(receivers)/sizeof(receivers[0]));

void setup(){
    Serial.begin(9600);
    motorSerial.begin(38400);
    delay(100);
    motor.enable();
    Error::build(vespuChat, 0x00/*starting*/);
}

void loop(){
    uint32_t time = -micros();
    vespuChat.update();
    time += micros();
    //Serial.print("updated in ");
    //Serial.print(time);
    //Serial.println();
    //delay(10);
}
