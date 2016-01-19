#ifndef ACCELDATA_H
#define ACCELDATA_H

#include "serial/Receiver.h"
#include <stdint.h>

typedef struct AccelDataconv {
    uint8_t sig;
    float xgs;
    float ygs;
    float zgs;
} AccelDataconv;
class AccelData : public Receiver {
public:
    int claim(char data) {
        return (data == 1)? 13 : -1;
    }
    void handle(const char* buf, int len){
        AccelDataconv *data = (*AccelDataconv) buf;
        //handle data
    }
    static uint8_t* build(float xgs, float ygs, float zgs){
        uint8_t *buf = malloc(13);
        AccelDataconv *data = (*AccelDataconv) buf;
        data->sig = 1;
        data->xgs = xgs;
        data->ygs = ygs;
        data->zgs = zgs;
        return buf;
    }
};
#endif
