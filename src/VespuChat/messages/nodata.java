package com.VespuChat.messages;

import com.serial.PacketReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class nodata implements PacketReader {
    public int claim(byte data){
        return (data == 5)? 1 : -1;
    }
    public void handle(byte[] data){
        ByteBuffer buf = ByteBuffer.wrap(data);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        byte  sig = buf.get();
        
        onReceive();
    }
    public static byte[] build(){
        ByteBuffer buf = ByteBuffer.allocate(1);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put((byte)5);
        
        return buf.array();
    }
    protected abstract void onReceive();
}
