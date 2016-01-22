#ifndef DECODER_H
#define DECODER_H

#include <stdint.h>
#include "../util/circBuf.h"
#include "Protocol.h"
#include "Receiver.h"

class InputStream {
public:
    virtual int read() = 0;
    virtual int available() = 0;
};

struct Packet{
    int start;
    int callback;
};

/**
 * Decodes packets of the form <header+><signifier>data<checksum+><footer+>
 */

class Decoder{
public:
    Decoder(InputStream* is, Protocol* protocol, Receiver** receivers, int num_receivers):
        is(is), protocol(protocol), receivers(receivers), num_receivers(num_receivers) { }
    /** Check the input stream for more data; process anything new */
    void update();
private:
    static const int BUF_SIZE = 64;
    InputStream* is;
    Protocol* protocol;
    Receiver** receivers;
    int num_receivers;
    circBuf<char, BUF_SIZE> buffer;
    circBuf<Packet, 4> packets;
    /** receive a char, buffer it, check for packet matches */
    void receive(char);
    /** check for a match with `str` of length `len` that ends at `end` */
    bool bufferMatch(int end, const char * str, int len);
    /** return the index of the receiver that accepts the passed in signifier
      * returns -1 if no receiver is found
      */
    int  findReceiver(char sig);
    /** check and handle packets ending at `end` */
    void checkPackets(int end);
};

//when a header is found, a one byte signifier follows
//receivers will claim the signifier and return the maximum length of that packet
//when a footer is found, see if the checksum matches
//  (correct) send signifier and data to a claimant
//  (incorrect) do nothing
//When a message goes past its maximum length without a matching checksum, dump it
//When a message gets matched, dump all older messages
void Decoder::update(){
    while(is->available()) receive( (char)(is->read() & 0xff) );
}

void Decoder::receive(char c){
    buffer.add(c);
    //find headers
    if(bufferMatch(buffer.end()-1, protocol->header, protocol->header_len)){
        int recIdx = findReceiver(buffer[buffer.end()-1]);
        if(recIdx != -1){
            Packet found = { buffer.end()-1, recIdx };
            packets.add(found);
        }
    }
    //find footers
    if(bufferMatch(buffer.end(), protocol->footer, protocol->footer_len)){
        checkPackets(buffer.end() - protocol->footer_len);
    }
}

bool Decoder::bufferMatch(int end, const char * str, int len){
    if(buffer.size() <= len) return false;
    int readPoint = end - len;
    for(int i=0; i<len; i++){
        if(buffer[readPoint+i] != str[i]) return false;
    }
    return true;
}

int Decoder::findReceiver(char sig){
    for(int i=0; i<num_receivers; i++){
        if(receivers[i]->claim(sig) != -1) {
            return i;
        }
    }
    return -1;
}

void Decoder::checkPackets(int end){
    for(int i=packets.start(); i<packets.end(); i++){

        //copy message into an array
        int start  = packets[i].start;
        int length = end-start;
        if(length < 0) length += BUF_SIZE;
        char msg[length];
        for(int i=0; i<length; i++){
            msg[i] = buffer[start+i];
        }

        //check for a match
        if(protocol->checksum(msg, length)){

            //call back to claming packet handler
            receivers[packets[i].callback]->handle(msg, length);
            //clean packets list
            packets.remove(i+1);
            return;
        }
    }
}
#endif
