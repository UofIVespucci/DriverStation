#ifndef POSITIONDATA_H
#define POSITIONDATA_H

#include "../serial/Receiver.h"
#include "../VespuChat.h"
#include <stdint.h>

typedef struct PositionDataconv {
    uint8_t sig;
    int16_t leftTrack;
    int16_t rightTrack;
} __attribute__((__packed__)) PositionDataconv;
typedef void (*_handlePositionData) (int16_t leftTrack, int16_t rightTrack);
class PositionData : public Receiver {
private:
   _handlePositionData hF;
public:
    PositionData(_handlePositionData hF): hF(hF) {}
    int claim(char data) {
        return (data == 3)? 5 : -1;
    }
    void handle(const char* buf, int len){
        PositionDataconv *data = (PositionDataconv*) buf;
        hF(data->leftTrack, data->rightTrack);
    }
    static void build(VespuChat& vct, int16_t leftTrack, int16_t rightTrack){
        uint8_t buf[5];
        PositionDataconv *data = (PositionDataconv*) buf;
        data->sig = 3;
        data->leftTrack = leftTrack;
        data->rightTrack = rightTrack;
        vct.deliver(buf, 5);
    }
};
#endif
