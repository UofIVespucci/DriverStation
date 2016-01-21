#ifndef PARSEFAIL_H
#define PARSEFAIL_H

#include "../serial/Receiver.h"
#include "../VespuChat.h"
#include <stdint.h>

typedef struct ParseFailconv {
    uint8_t sig;
    ;
} __attribute__((__packed__)) ParseFailconv;
typedef void (*handleFunc) ();
class ParseFail : public Receiver {
private:
   handleFunc hF;
public:
    ParseFail(handleFunc hF): hF(hF) {}
    int claim(char data) {
        return (data == 4)? 1 : -1;
    }
    void handle(const char* buf, int len){
        ParseFailconv *data = (ParseFailconv*) buf;
        hF();
    }
    static void build(VespuChat& vct, ){
        uint8_t buf[1];
        ParseFailconv *data = (ParseFailconv*) buf;
        data->sig = 4;
        
        vct.deliver(buf, 5);
    }
};
#endif
