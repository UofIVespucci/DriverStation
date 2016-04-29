package com.VespuChat.messages;

import com.serial.PacketReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class PositionData implements PacketReader {
    public int claim(byte data){
        return (data == 3)? 7 : -1;
    }
    public void handle(byte[] data){
        ByteBuffer buf = ByteBuffer.wrap(data);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        byte  sig = buf.get();
        short leftTrack = buf.getShort();
        short rightTrack = buf.getShort();
        short voltage = buf.getShort();
        onReceive(leftTrack, rightTrack, voltage);
    }
    public static byte[] build(short leftTrack, short rightTrack, short voltage){
        ByteBuffer buf = ByteBuffer.allocate(7);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put((byte)3);
        buf.putShort(leftTrack);
        buf.putShort(rightTrack);
        buf.putShort(voltage);
        return buf.array();
    }
    protected abstract void onReceive(short leftTrack, short rightTrack, short voltage);
}
