#ifndef PING_H
#define PING_H

#include "../serial/Receiver.h"
#include "../VespuChat.h"
#include <stdint.h>

typedef struct Pingconv {
    uint8_t sig;
    ;
} __attribute__((__packed__)) Pingconv;
typedef void (*handleFunc) ();
class Ping : public Receiver {
private:
   handleFunc hF;
public:
    Ping(handleFunc hF): hF(hF) {}
    int claim(char data) {
        return (data == 3)? 1 : -1;
    }
    void handle(const char* buf, int len){
        Pingconv *data = (Pingconv*) buf;
        hF();
    }
    static void build(VespuChat& vct, ){
        uint8_t buf[1];
        Pingconv *data = (Pingconv*) buf;
        data->sig = 3;
        
        vct.deliver(buf, 5);
    }
};
#endif
