package com.VespuChat.messages;

import com.serial.PacketReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class Debug implements PacketReader {
    public int claim(byte data){
        return (data == 6)? 5 : -1;
    }
    public void handle(byte[] data){
        ByteBuffer buf = ByteBuffer.wrap(data);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        byte  sig = buf.get();
        byte A = buf.get();
        byte B = buf.get();
        byte C = buf.get();
        byte D = buf.get();
        onReceive(A, B, C, D);
    }
    public static byte[] build(byte A, byte B, byte C, byte D){
        ByteBuffer buf = ByteBuffer.allocate(5);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put((byte)6);
        buf.put(A);
        buf.put(B);
        buf.put(C);
        buf.put(D);
        return buf.array();
    }
    protected abstract void onReceive(byte A, byte B, byte C, byte D);
}
