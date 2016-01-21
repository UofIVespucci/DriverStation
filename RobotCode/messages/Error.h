#ifndef ERROR_H
#define ERROR_H

#include "../serial/Receiver.h"
#include "../VespuChat.h"
#include <stdint.h>

typedef struct Errorconv {
    uint8_t sig;
    int16_t code;
} __attribute__((__packed__)) Errorconv;
typedef void (*handleFunc) (int16_t code);
class Error : public Receiver {
private:
   handleFunc hF;
public:
    Error(handleFunc hF): hF(hF) {}
    int claim(char data) {
        return (data == 3)? 3 : -1;
    }
    void handle(const char* buf, int len){
        Errorconv *data = (Errorconv*) buf;
        hF(data->code);
    }
    static void build(VespuChat& vct, int16_t code){
        uint8_t buf[3];
        Errorconv *data = (Errorconv*) buf;
        data->sig = 3;
        data->code = code;
        vct.deliver(buf, 5);
    }
};
#endif
