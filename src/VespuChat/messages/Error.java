package com.VespuChat.messages;

import com.serial.PacketReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class Error implements PacketReader {
    public int claim(byte data){
        return (data == 4)? 2 : -1;
    }
    public void handle(byte[] data){
        ByteBuffer buf = ByteBuffer.wrap(data);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        byte  sig = buf.get();
        byte num = buf.get();
        onReceive(num);
    }
    public static byte[] build(byte num){
        ByteBuffer buf = ByteBuffer.allocate(2);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put((byte)4);
        buf.put(num);
        return buf.array();
    }
    protected abstract void onReceive(byte num);
}
