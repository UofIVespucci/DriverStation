#ifndef DONUSECOMMANDZERO_H
#define DONUSECOMMANDZERO_H

#include "../serial/Receiver.h"
#include "../VespuChat.h"
#include <stdint.h>

typedef struct donusecommandzeroconv {
    uint8_t sig;
} __attribute__((__packed__)) donusecommandzeroconv;
typedef void (*_handledonusecommandzero) ();
class donusecommandzero : public Receiver {
private:
   _handledonusecommandzero hF;
public:
    donusecommandzero(_handledonusecommandzero hF): hF(hF) {}
    int claim(char data) {
        return (data == 0)? 1 : -1;
    }
    void handle(const char* buf, int len){
        donusecommandzeroconv *data = (donusecommandzeroconv*) buf;
        hF();
    }
    static void build(VespuChat& vct){
        uint8_t buf[1];
        donusecommandzeroconv *data = (donusecommandzeroconv*) buf;
        data->sig = 0;
        
        vct.deliver(buf, 1);
    }
};
#endif
