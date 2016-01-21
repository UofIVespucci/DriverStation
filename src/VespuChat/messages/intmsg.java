package com.VespuChat.messages;

import com.serial.PacketReader;
import java.nio.ByteBuffer;

public abstract class intmsg implements PacketReader {
    public int claim(byte data){
        return (data == 4)? 5 : -1;
    }
    public void handle(byte[] data){
        ByteBuffer buf = ByteBuffer.wrap(data);
        byte  sig = buf.get();
        int test = buf.getInt();
        onReceive(test);
    }
    public static byte[] build(int test){
        ByteBuffer buf = ByteBuffer.allocate(5);
        buf.put((byte)4);
        buf.putInt(test);
        return buf.array();
    }
    protected abstract void onReceive(int test);
}
