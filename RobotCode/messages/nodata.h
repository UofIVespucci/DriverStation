#ifndef NODATA_H
#define NODATA_H

#include "../serial/Receiver.h"
#include "../VespuChat.h"
#include <stdint.h>

typedef struct nodataconv {
    uint8_t sig;
} __attribute__((__packed__)) nodataconv;
typedef void (*_handlenodata) ();
class nodata : public Receiver {
private:
   _handlenodata hF;
public:
    nodata(_handlenodata hF): hF(hF) {}
    int claim(char data) {
        return (data == 5)? 1 : -1;
    }
    void handle(const char* buf, int len){
        nodataconv *data = (nodataconv*) buf;
        hF();
    }
    static void build(VespuChat& vct){
        uint8_t buf[1];
        nodataconv *data = (nodataconv*) buf;
        data->sig = 5;
        
        vct.deliver(buf, 1);
    }
};
#endif
