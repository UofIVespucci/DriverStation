#ifndef POSITIONDATA_H
#define POSITIONDATA_H

#include "../serial/Receiver.h"
#include "../serial/VCTransmitter.h"
#include <stdint.h>

typedef struct PositionDataconv {
    uint8_t sig;
    float x;
    float y;
    float z;
} __attribute__((__packed__)) PositionDataconv;
typedef void (*handleFunc) (float x, float y, float z);
class PositionData : public Receiver {
private:
   handleFunc hF;
public:
    PositionData(handleFunc hF): hF(hF) {}
    int claim(char data) {
        return (data == 2)? 13 : -1;
    }
    void handle(const char* buf, int len){
        PositionDataconv *data = (PositionDataconv*) buf;
        hF(data->x, data->y, data->z);
    }
    static void build(VCTransmitter* vct, float x, float y, float z){
        uint8_t buf[13];
        PositionDataconv *data = (PositionDataconv*) buf;
        data->sig = 2;
        data->x = x;
        data->y = y;
        data->z = z;
        vct->transmit(buf, 5);
    }
};
#endif
