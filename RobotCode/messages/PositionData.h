#ifndef POSITIONDATA_H
#define POSITIONDATA_H

#include "../serial/Receiver.h"
#include "../VespuChat.h"
#include <stdint.h>

typedef struct PositionDataconv {
    uint8_t sig;
    int16_t leftTrack;
    int16_t rightTrack;
    int16_t voltage;
} __attribute__((__packed__)) PositionDataconv;
typedef void (*_handlePositionData) (int16_t leftTrack, int16_t rightTrack, int16_t voltage);
class PositionData : public Receiver {
private:
   _handlePositionData hF;
public:
    PositionData(_handlePositionData hF): hF(hF) {}
    int claim(char data) {
        return (data == 2)? 7 : -1;
    }
    void handle(const char* buf, int len){
        PositionDataconv *data = (PositionDataconv*) buf;
        hF(data->leftTrack, data->rightTrack, data->voltage);
    }
    static void build(VespuChat& vct, int16_t leftTrack, int16_t rightTrack, int16_t voltage){
        uint8_t buf[7];
        PositionDataconv *data = (PositionDataconv*) buf;
        data->sig = 2;
        data->leftTrack = leftTrack;
        data->rightTrack = rightTrack;
        data->voltage = voltage;
        vct.deliver(buf, 7);
    }
};
#endif
