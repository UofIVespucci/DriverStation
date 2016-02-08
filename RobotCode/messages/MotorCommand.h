#ifndef MOTORCOMMAND_H
#define MOTORCOMMAND_H

#include "../serial/Receiver.h"
#include "../VespuChat.h"
#include <stdint.h>

typedef struct MotorCommandconv {
    uint8_t sig;
    uint8_t left;
    uint8_t right;
} __attribute__((__packed__)) MotorCommandconv;
typedef void (*_handleMotorCommand) (uint8_t left, uint8_t right);
class MotorCommand : public Receiver {
private:
   _handleMotorCommand hF;
public:
    MotorCommand(_handleMotorCommand hF): hF(hF) {}
    int claim(char data) {
        return (data == 0)? 3 : -1;
    }
    void handle(const char* buf, int len){
        MotorCommandconv *data = (MotorCommandconv*) buf;
        hF(data->left, data->right);
    }
    static void build(VespuChat& vct, uint8_t left, uint8_t right){
        uint8_t buf[3];
        MotorCommandconv *data = (MotorCommandconv*) buf;
        data->sig = 0;
        data->left = left;
        data->right = right;
        vct.deliver(buf, 3);
    }
};
#endif
