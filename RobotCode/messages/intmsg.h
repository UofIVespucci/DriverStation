#ifndef INTMSG_H
#define INTMSG_H

#include "../serial/Receiver.h"
#include "../VespuChat.h"
#include <stdint.h>

typedef struct intmsgconv {
    uint8_t sig;
    int32_t test;
} __attribute__((__packed__)) intmsgconv;
typedef void (*_handleintmsg) (int32_t test);
class intmsg : public Receiver {
private:
   _handleintmsg hF;
public:
    intmsg(_handleintmsg hF): hF(hF) {}
    int claim(char data) {
        return (data == 4)? 5 : -1;
    }
    void handle(const char* buf, int len){
        intmsgconv *data = (intmsgconv*) buf;
        hF(data->test);
    }
    static void build(VespuChat& vct, int32_t test){
        uint8_t buf[5];
        intmsgconv *data = (intmsgconv*) buf;
        data->sig = 4;
        data->test = test;
        vct.deliver(buf, 5);
    }
};
#endif
