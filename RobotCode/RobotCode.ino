#include "messages/MotorCommand.h"
#include "serial/Decoder.h"
#include "VespuChat.h"

extern VespuChat vespuChat;
MotorCommand mcmd([](int16_t left, int16_t right){
    MotorCommand::build(vespuChat, left, right);
});
Receiver* receivers[] = { &mcmd };
VespuChat vespuChat(Serial, receivers, sizeof(receivers)/sizeof(receivers[0]));

void setup(){
    Serial.begin(9600);
}

void loop(){
    vespuChat.update();
}
