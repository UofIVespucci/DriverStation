
#ifndef MOTORCOMMAND_H
#define MOTORCOMMAND_H

#include "serial/Receiver";

typedef struct MotorCommandconv {
    uint8_t sig;
    int16_t left;
    int16_t right;
} MotorCommandconv;
class MotorCommand : public Receiver {
public:
    int claim(char data) {
        return (data == 0) 5 : -1;
    }
    void handle(const char* buf, int len){
        MotorCommandconv *data = (*MotorCommandconv) buf;
        //handle data
    }
    static byte* build(int16_t left, int16_t right){
        byte *buf = malloc(5);
        MotorCommandconv *data = (*MotorCommandconv) buf;
        data->sig = 0;
        data->left = left;
        data->right = right;
        return buf;
    }
}
#endif
