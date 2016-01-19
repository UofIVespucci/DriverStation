#include "messages/MotorCommand.h"
#include "serial/Decoder.h"
#include "VespuChat.h"

char readInput(){ return Serial.read(); }
Receiver* receivers[] = {

};
const int num_receivers = 0;
Decoder decoder(&readInput, &VespuChat, receivers, num_receivers);

void setup(){
    Serial.begin(9600);
}

void loop(){
    decoder.update();
}
