package com.VespuChat.messages;

import com.serial.PacketReader;
import java.nio.ByteBuffer;

public abstract class Error implements PacketReader {
    public int claim(byte data){
        return (data == 3)? 3 : -1;
    }
    public void handle(byte[] data){
        ByteBuffer buf = ByteBuffer.wrap(data);
        byte  sig = buf.get();
        short code = buf.getShort();
        onReceive(code);
    }
    public static byte[] build(short code){
        ByteBuffer buf = ByteBuffer.allocate(3);
        buf.put((byte)3);
        buf.putShort(code);
        return buf.array();
    }
    protected abstract void onReceive(short code);
}
