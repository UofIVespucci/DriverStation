package com.VespuChat.messages;

import com.serial.PacketReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class MotorCommand implements PacketReader {
    public int claim(byte data){
        return (data == 0)? 5 : -1;
    }
    public void handle(byte[] data){
        ByteBuffer buf = ByteBuffer.wrap(data);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        byte  sig = buf.get();
        short left = buf.getShort();
        short right = buf.getShort();
        onReceive(left, right);
    }
    public static byte[] build(short left, short right){
        ByteBuffer buf = ByteBuffer.allocate(5);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put((byte)0);
        buf.putShort(left);
        buf.putShort(right);
        return buf.array();
    }
    protected abstract void onReceive(short left, short right);
}
