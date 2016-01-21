#ifndef POSITIONDATA_H
#define POSITIONDATA_H

#include "../serial/Receiver.h"
#include "../VespuChat.h"
#include <stdint.h>

typedef struct PositionDataconv {
    uint8_t sig;
    float x;
    float y;
    float z;
} __attribute__((__packed__)) PositionDataconv;
typedef void (*_handlePositionData) (float x, float y, float z);
class PositionData : public Receiver {
private:
   _handlePositionData hF;
public:
    PositionData(_handlePositionData hF): hF(hF) {}
    int claim(char data) {
        return (data == 3)? 13 : -1;
    }
    void handle(const char* buf, int len){
        PositionDataconv *data = (PositionDataconv*) buf;
        hF(data->x, data->y, data->z);
    }
    static void build(VespuChat& vct, float x, float y, float z){
        uint8_t buf[13];
        PositionDataconv *data = (PositionDataconv*) buf;
        data->sig = 3;
        data->x = x;
        data->y = y;
        data->z = z;
        vct.deliver(buf, 13);
    }
};
#endif
