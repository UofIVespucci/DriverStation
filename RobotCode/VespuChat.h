#ifndef VESPUCHAT_H
#define VESPUCHAT_H

#include "serial/Protocol.h"
#include "Stream.h"

namespace{
    const char HEADER[] = { 0x5F };
    const char FOOTER[] = { 0x0A };
    const int H_LEN = 1;
    const int F_LEN = 1;
}
class VespuChat : public Protocol {
public:
    VespuChat(): Protocol(HEADER, H_LEN, FOOTER, F_LEN) {}
    uint8_t calcSum(const uint8_t* buf, int len){
        uint8_t sum = 0;
        for(int i=0; i<len; i++) sum ^= buf[i];
        return sum;
    }
    bool checksum(const char* c, int len){
        return c[len-1] == calcSum((const uint8_t*)c, len-1);
    }
    //wrap and transmit a payload
    void deliver(Stream* output, const uint8_t* data, int len){
        output->write(HEADER, H_LEN);
        output->write(data, len);
        output->write(calcSum(data,len));
        output->write(FOOTER, F_LEN);
    }
};

VespuChat VespuChat;
#endif
