#ifndef _H
#define _H

#include "../serial/Receiver.h"
#include "../VespuChat.h"
#include <stdint.h>

typedef struct conv {
    uint8_t sig;
    uint8_t brightness;
} __attribute__((__packed__)) conv;
typedef void (*_handle) (uint8_t brightness);
class  : public Receiver {
private:
   _handle hF;
public:
    (_handle hF): hF(hF) {}
    int claim(char data) {
        return (data == 5)? 2 : -1;
    }
    void handle(const char* buf, int len){
        conv *data = (conv*) buf;
        hF(data->brightness);
    }
    static void build(VespuChat& vct, uint8_t brightness){
        uint8_t buf[2];
        conv *data = (conv*) buf;
        data->sig = 5;
        data->brightness = brightness;
        vct.deliver(buf, 2);
    }
};
#endif
