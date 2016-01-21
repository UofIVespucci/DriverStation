package com.VespuChat.messages;

import com.serial.PacketReader;
import java.nio.ByteBuffer;

public abstract class ParseFail implements PacketReader {
    public int claim(byte data){
        return (data == 4)? 1 : -1;
    }
    public void handle(byte[] data){
        ByteBuffer buf = ByteBuffer.wrap(data);
        byte  sig = buf.get();
        
        onReceive();
    }
    public static byte[] build(){
        ByteBuffer buf = ByteBuffer.allocate(1);
        buf.put((byte)4);
        
        return buf.array();
    }
    protected abstract void onReceive();
}
