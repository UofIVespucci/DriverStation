package com.VespuChat.messages;

import com.serial.PacketReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class AccelData implements PacketReader {
    public int claim(byte data){
        return (data == 2)? 13 : -1;
    }
    public void handle(byte[] data){
        ByteBuffer buf = ByteBuffer.wrap(data);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        byte  sig = buf.get();
        float xgs = buf.getFloat();
        float ygs = buf.getFloat();
        float zgs = buf.getFloat();
        onReceive(xgs, ygs, zgs);
    }
    public static byte[] build(float xgs, float ygs, float zgs){
        ByteBuffer buf = ByteBuffer.allocate(13);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put((byte)2);
        buf.putFloat(xgs);
        buf.putFloat(ygs);
        buf.putFloat(zgs);
        return buf.array();
    }
    protected abstract void onReceive(float xgs, float ygs, float zgs);
}
