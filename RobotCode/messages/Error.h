#ifndef ERROR_H
#define ERROR_H

#include "../serial/Receiver.h"
#include "../VespuChat.h"
#include <stdint.h>

typedef struct Errorconv {
    uint8_t sig;
    uint8_t num;
} __attribute__((__packed__)) Errorconv;
typedef void (*_handleError) (uint8_t num);
class Error : public Receiver {
private:
   _handleError hF;
public:
    Error(_handleError hF): hF(hF) {}
    int claim(char data) {
        return (data == 4)? 2 : -1;
    }
    void handle(const char* buf, int len){
        Errorconv *data = (Errorconv*) buf;
        hF(data->num);
    }
    static void build(VespuChat& vct, uint8_t num){
        uint8_t buf[2];
        Errorconv *data = (Errorconv*) buf;
        data->sig = 4;
        data->num = num;
        vct.deliver(buf, 2);
    }
};
#endif
