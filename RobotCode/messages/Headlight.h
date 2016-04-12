#ifndef HEADLIGHT_H
#define HEADLIGHT_H

#include "../serial/Receiver.h"
#include "../VespuChat.h"
#include <stdint.h>

typedef struct Headlightconv {
    uint8_t sig;
    uint8_t brightness;
} __attribute__((__packed__)) Headlightconv;
typedef void (*_handleHeadlight) (uint8_t brightness);
class Headlight : public Receiver {
private:
   _handleHeadlight hF;
public:
    Headlight(_handleHeadlight hF): hF(hF) {}
    int claim(char data) {
        return (data == 5)? 2 : -1;
    }
    void handle(const char* buf, int len){
        Headlightconv *data = (Headlightconv*) buf;
        hF(data->brightness);
    }
    static void build(VespuChat& vct, uint8_t brightness){
        uint8_t buf[2];
        Headlightconv *data = (Headlightconv*) buf;
        data->sig = 5;
        data->brightness = brightness;
        vct.deliver(buf, 2);
    }
};
#endif
