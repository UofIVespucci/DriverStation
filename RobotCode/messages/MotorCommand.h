#ifndef MOTORCOMMAND_H
#define MOTORCOMMAND_H

#include "../serial/Receiver.h"
#include "../serial/VCTransmitter.h"
#include <stdint.h>

typedef struct MotorCommandconv {
    uint8_t sig;
    int16_t left;
    int16_t right;
} MotorCommandconv;
typedef void (*handleFunc) (int16_t left, int16_t right);
class MotorCommand : public Receiver {
private:
   handleFunc hF;
public:
    MotorCommand(handleFunc hF): hF(hF) {}
    int claim(char data) {
        return (data == 0)? 5 : -1;
    }
    void handle(const char* buf, int len){
        MotorCommandconv *data = (MotorCommandconv*) buf;
        hF(data->left, data->right);
    }
    static void build(VCTransmitter* vct, int16_t left, int16_t right){
        uint8_t buf[5];
        MotorCommandconv *data = (MotorCommandconv*) buf;
        data->sig = 0;
        data->left = left;
        data->right = right;
        vct->transmit(buf, 5);
    }
};
#endif
