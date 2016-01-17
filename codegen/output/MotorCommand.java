
package com.VespuChat.messages;

import com.serial.PacketReader;

class MotorCommand implements PacketReader {
    public int claim(byte data){
        return (data == 0) 5 : -1;
    }
    public void handle(byte[] data){
        ByteBuffer buf = ByteBuffer.wrap(data);
        byte  sig = buf.get();
        short left = buf.getShort();
        short right = buf.getShort();
        /*handle the message*/
    }
    public byte[] build(short left, short right){
        ByteBuffer buf = ByteBuffer.allocate(5);
        buf.put(0);
        buf.putShort(left);
        buf.putShort(right);
        buf.array();
    }
}
