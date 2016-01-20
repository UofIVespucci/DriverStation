#ifndef ACCELDATA_H
#define ACCELDATA_H

#include "../serial/Receiver.h"
#include "../serial/VCTransmitter.h"
#include <stdint.h>

typedef struct AccelDataconv {
    uint8_t sig;
    float xgs;
    float ygs;
    float zgs;
} __attribute__((__packed__)) AccelDataconv;
typedef void (*handleFunc) (float xgs, float ygs, float zgs);
class AccelData : public Receiver {
private:
   handleFunc hF;
public:
    AccelData(handleFunc hF): hF(hF) {}
    int claim(char data) {
        return (data == 1)? 13 : -1;
    }
    void handle(const char* buf, int len){
        AccelDataconv *data = (AccelDataconv*) buf;
        hF(data->xgs, data->ygs, data->zgs);
    }
    static void build(VCTransmitter* vct, float xgs, float ygs, float zgs){
        uint8_t buf[13];
        AccelDataconv *data = (AccelDataconv*) buf;
        data->sig = 1;
        data->xgs = xgs;
        data->ygs = ygs;
        data->zgs = zgs;
        vct->transmit(buf, 5);
    }
};
#endif
