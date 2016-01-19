#ifndef POSITIONDATA_H
#define POSITIONDATA_H

#include "serial/Receiver.h"
#include <stdint.h>

typedef struct PositionDataconv {
    uint8_t sig;
    float x;
    float y;
    float z;
} PositionDataconv;
class PositionData : public Receiver {
public:
    int claim(char data) {
        return (data == 2)? 13 : -1;
    }
    void handle(const char* buf, int len){
        PositionDataconv *data = (*PositionDataconv) buf;
        //handle data
    }
    static uint8_t* build(float x, float y, float z){
        uint8_t *buf = malloc(13);
        PositionDataconv *data = (*PositionDataconv) buf;
        data->sig = 2;
        data->x = x;
        data->y = y;
        data->z = z;
        return buf;
    }
};
#endif
