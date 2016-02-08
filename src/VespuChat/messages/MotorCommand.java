package com.VespuChat.messages;

import com.serial.PacketReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class MotorCommand implements PacketReader {
    public int claim(byte data){
        return (data == 0)? 3 : -1;
    }
    public void handle(byte[] data){
        ByteBuffer buf = ByteBuffer.wrap(data);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        byte  sig = buf.get();
        byte left = buf.get();
        byte right = buf.get();
        onReceive(left, right);
    }
    public static byte[] build(byte left, byte right){
        ByteBuffer buf = ByteBuffer.allocate(3);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put((byte)0);
        buf.put(left);
        buf.put(right);
        return buf.array();
    }
    protected abstract void onReceive(byte left, byte right);
}
