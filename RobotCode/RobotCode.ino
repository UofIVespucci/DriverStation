#include "messages/MotorCommand.h"
#include "messages/Error.h"
#include "messages/Headlight.h"
#include "messages/PositionData.h"
#include "serial/Decoder.h"
#include "VespuChat.h"
#include "Encoder.h"
#include "Qik/Qik.h"
#include <AltSoftSerial.h>
AltSoftSerial motorSerial;

const int HEADLIGHT_PIN = 5;
Qik motor(&motorSerial);

extern VespuChat vespuChat;
MotorCommand mcmd([](uint8_t left, uint8_t right){
    MotorCommand::build(vespuChat, left, right); //echo command
    motor.setMotors(-(int8_t)left, -(int8_t)right);
});
Error ecmd([](uint8_t code){
    Error::build(vespuChat, code);
});
Headlight hcmd([](uint8_t brightness){
    analogWrite(HEADLIGHT_PIN, brightness);
});
Receiver* receivers[] = { &mcmd, &ecmd };
VespuChat vespuChat(Serial, receivers, sizeof(receivers)/sizeof(receivers[0]));

void setup(){
    Serial.begin(9600);
    motorSerial.begin(38400);
    encoder::begin(2,3);
    delay(100);

    motor.enable();
    motor.setDriveMode(_32KHZ);
    Error::build(vespuChat, 0x00/*starting*/);
}

void loop(){
    vespuChat.update();

    // send new encoder position data once every 2 seconds
    static uint32_t time = millis();
    if(time <= millis()){
        time += 2000;
        PositionData::build(vespuChat, encoder::getLeft(), encoder::getRight());
    }
}
