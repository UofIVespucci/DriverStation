package com.VespuChat.messages;

import com.serial.PacketReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class PositionData implements PacketReader {
    public int claim(byte data){
        return (data == 2)? 13 : -1;
    }
    public void handle(byte[] data){
        ByteBuffer buf = ByteBuffer.wrap(data);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        byte  sig = buf.get();
        float x = buf.getFloat();
        float y = buf.getFloat();
        float z = buf.getFloat();
        onReceive(x, y, z);
    }
    public static byte[] build(float x, float y, float z){
        ByteBuffer buf = ByteBuffer.allocate(13);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put((byte)2);
        buf.putFloat(x);
        buf.putFloat(y);
        buf.putFloat(z);
        return buf.array();
    }
    protected abstract void onReceive(float x, float y, float z);
}
