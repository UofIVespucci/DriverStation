#ifndef CAMERASWITCH_H
#define CAMERASWITCH_H

#include "../serial/Receiver.h"
#include "../VespuChat.h"
#include <stdint.h>

typedef struct CameraSwitchconv {
    uint8_t sig;
    uint8_t toggle;
} __attribute__((__packed__)) CameraSwitchconv;
typedef void (*_handleCameraSwitch) (uint8_t toggle);
class CameraSwitch : public Receiver {
private:
   _handleCameraSwitch hF;
public:
    CameraSwitch(_handleCameraSwitch hF): hF(hF) {}
    int claim(char data) {
        return (data == 5)? 2 : -1;
    }
    void handle(const char* buf, int len){
        CameraSwitchconv *data = (CameraSwitchconv*) buf;
        hF(data->toggle);
    }
    static void build(VespuChat& vct, uint8_t toggle){
        uint8_t buf[2];
        CameraSwitchconv *data = (CameraSwitchconv*) buf;
        data->sig = 5;
        data->toggle = toggle;
        vct.deliver(buf, 2);
    }
};
#endif
