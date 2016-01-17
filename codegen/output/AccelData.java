
package com.VespuChat.messages;

import com.serial.PacketReader;

class AccelData implements PacketReader {
    public int claim(byte data){
        return (data == 1) 13 : -1;
    }
    public void handle(byte[] data){
        ByteBuffer buf = ByteBuffer.wrap(data);
        byte  sig = buf.get();
        float xgs = buf.getFloat();
        float ygs = buf.getFloat();
        float zgs = buf.getFloat();
        /*handle the message*/
    }
    public byte[] build(float xgs, float ygs, float zgs){
        ByteBuffer buf = ByteBuffer.allocate(13);
        buf.put(1);
        buf.putFloat(xgs);
        buf.putFloat(ygs);
        buf.putFloat(zgs);
        buf.array();
    }
}
