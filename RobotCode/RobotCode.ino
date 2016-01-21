#include "messages/MotorCommand.h"
#include "messages/Error.h"
#include "messages/Debug.h"
#include "serial/Decoder.h"
#include "VespuChat.h"

extern VespuChat vespuChat;
MotorCommand mcmd([](int16_t left, int16_t right){
    MotorCommand::build(vespuChat, left, right);
});
Error ecmd([](uint8_t code){
    Error::build(vespuChat, code);
});
Receiver* receivers[] = { &mcmd, &ecmd };
VespuChat vespuChat(Serial, receivers, sizeof(receivers)/sizeof(receivers[0]));

void setup(){
    Serial.begin(9600);
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
