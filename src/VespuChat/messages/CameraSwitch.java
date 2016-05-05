package com.VespuChat.messages;

import com.serial.PacketReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class CameraSwitch implements PacketReader {
    public int claim(byte data){
        return (data == 5)? 2 : -1;
    }
    public void handle(byte[] data){
        ByteBuffer buf = ByteBuffer.wrap(data);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        byte  sig = buf.get();
        byte toggle = buf.get();
        onReceive(toggle);
    }
    public static byte[] build(byte toggle){
        ByteBuffer buf = ByteBuffer.allocate(2);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put((byte)5);
        buf.put(toggle);
        return buf.array();
    }
    protected abstract void onReceive(byte toggle);
}
