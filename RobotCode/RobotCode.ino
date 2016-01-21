#include "messages/MotorCommand.h"
#include "serial/Decoder.h"
#include "VespuChat.h"

extern VespuChat vespuChat;
MotorCommand mcmd([](int16_t left, int16_t right){
    MotorCommand::build(vespuChat, left, right);
    //Serial.println("Received motor command");
});
Receiver* receivers[] = { &mcmd };
VespuChat vespuChat(Serial, receivers, sizeof(receivers)/sizeof(receivers[0]));

void setup(){
    Serial.begin(9600);
    Serial.println("Done with setup");
}

void loop(){
    uint32_t time = -micros();
    vespuChat.update();
    time += micros();
    //Serial.print("updated in ");
    //Serial.print(time);
    //Serial.println();
    //MotorCommand::build(vespuChat, 0, 0x0F);
    delay(1000);
}
