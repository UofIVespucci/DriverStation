#ifndef DEBUG_H
#define DEBUG_H

#include "../serial/Receiver.h"
#include "../VespuChat.h"
#include <stdint.h>

typedef struct debugconv {
    uint8_t sig;
    uint8_t A;
    uint8_t B;
    uint8_t C;
    uint8_t D;
} __attribute__((__packed__)) debugconv;
typedef void (*_handledebug) (uint8_t A, uint8_t B, uint8_t C, uint8_t D);
class debug : public Receiver {
private:
   _handledebug hF;
public:
    debug(_handledebug hF): hF(hF) {}
    int claim(char data) {
        return (data == 6)? 5 : -1;
    }
    void handle(const char* buf, int len){
        debugconv *data = (debugconv*) buf;
        hF(data->A, data->B, data->C, data->D);
    }
    static void build(VespuChat& vct, uint8_t A, uint8_t B, uint8_t C, uint8_t D){
        uint8_t buf[5];
        debugconv *data = (debugconv*) buf;
        data->sig = 6;
        data->A = A;
        data->B = B;
        data->C = C;
        data->D = D;
        vct.deliver(buf, 5);
    }
};
#endif
