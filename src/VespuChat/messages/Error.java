package com.VespuChat.messages;

import com.serial.PacketReader;
import java.nio.ByteBuffer;

public abstract class Error implements PacketReader {
    public int claim(byte data){
        return (data == 3)? 2 : -1;
    }
    public void handle(byte[] data){
        ByteBuffer buf = ByteBuffer.wrap(data);
        byte  sig = buf.get();
        byte num = buf.get();
        onReceive(num);
    }
    public static byte[] build(byte num){
        ByteBuffer buf = ByteBuffer.allocate(2);
        buf.put((byte)3);
        buf.put(num);
        return buf.array();
    }
    protected abstract void onReceive(byte num);
}
