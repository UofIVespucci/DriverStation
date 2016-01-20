#ifndef VESPUCHAT_H
#define VESPUCHAT_H

#include "serial/Protocol.h"
#include "serial/Decoder.h"
#include "Stream.h"

namespace{
    const char HEADER[] = { 0x5F };
    const char FOOTER[] = { 0x0A };
    const int H_LEN = 1;
    const int F_LEN = 1;
}
class VespuChat : public Protocol, public InputStream {
    Stream& stream;
    Decoder decoder;
public:
    VespuChat(Stream& stream, Receiver** receivers, int num_receivers):
        Protocol(HEADER, H_LEN, FOOTER, F_LEN),
        stream(stream),
        decoder(this, this, receivers, num_receivers) {}
    uint8_t calcSum(const uint8_t* buf, int len){
        uint8_t sum = 0;
        for(int i=0; i<len; i++) sum ^= buf[i];
        return sum;
    }
    bool checksum(const char* c, int len){
        return c[len-1] == calcSum((const uint8_t*)c, len-1);
    }
    //wrap and transmit a payload
    void deliver(const uint8_t* data, int len){
        stream.write(HEADER, H_LEN);
        stream.write(data, len);
        stream.write(calcSum(data,len));
        stream.write(FOOTER, F_LEN);
    }
    char read() {
        return stream.read();
    }
    void update() {
        decoder.update();
    }
};

#endif
