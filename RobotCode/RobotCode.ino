#include "messages/MotorCommand.h"
#include "messages/Error.h"
#include "messages/Headlight.h"
#include "messages/PositionData.h"
#include "messages/CameraSwitch.h"
#include "serial/Decoder.h"
#include "VespuChat.h"
#include "Encoder.h"
#include "Qik/Qik.h"
#include "ADNS/ADNS.h"
#include "T2SlowPulse.h"
#include "TB66.h"

const int HEADLIGHT_PIN = A2;
const int VIDEO_PIN = 8;
const int VOLT_PIN = A7;
const int PWM_A = 5;
const int PWM_B = 6;
const int AIN1 = 4;
const int AIN2 = 7;
const int BIN1 = A0;
const int BIN2 = A1;
const int STBY = A3;

TB66Chan leftMotor(PWM_A, AIN1, AIN2);
TB66Chan rightMotor(PWM_B, BIN1, BIN2);

extern VespuChat vespuChat;
MotorCommand mcmd([](int16_t left, int16_t right){
    MotorCommand::build(vespuChat, left, right); //echo command
    leftMotor.setPower( -left  );
    rightMotor.setPower( right );
});
Error ecmd([](uint8_t code){
    Error::build(vespuChat, code);
});
Headlight hcmd([](uint8_t brightness){
    digitalWrite(HEADLIGHT_PIN, (brightness < 128));
});
CameraSwitch vcmd([](uint8_t onoff){
    T2SlowPulse::pulseWidth((onoff)? 16 : 19, 255, false);
});
Receiver* receivers[] = { &mcmd, &ecmd, &hcmd, &vcmd };
VespuChat vespuChat(Serial, receivers, sizeof(receivers)/sizeof(receivers[0]));

void setup(){
    Serial.begin(9600);
    encoder::begin(2,3);
    delay(100);

    leftMotor.begin();
    rightMotor.begin();

    pinMode(STBY, OUTPUT);
    digitalWrite(STBY, HIGH);

    Error::build(vespuChat, 0x00/*starting*/);

    pinMode(VOLT_PIN, INPUT);
    pinMode(HEADLIGHT_PIN, OUTPUT);
    digitalWrite(HEADLIGHT_PIN, LOW);
    T2SlowPulse::begin(VIDEO_PIN);
    T2SlowPulse::pulseWidth(16, 255, false);

    delay(50);
}

void loop(){
    vespuChat.update();

    // send new encoder position data once every 2 seconds
    static uint32_t time = millis();
    if(time <= millis()){
        time += 2000;
        PositionData::build(vespuChat,
            encoder::getLeft(), encoder::getRight(), analogRead(VOLT_PIN));
    }
}
