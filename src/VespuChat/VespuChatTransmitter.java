package com.VespuChat;

import java.io.OutputStream;
import java.nio.ByteBuffer;

public class VespuChatTransmitter{
    private OutputStream output;

    public VespuChatTransmitter(OutputStream outputStream){
        output = outputStream;
    }

    public void close() throws Exception {
        output.close();
    }

    public void sendMotorCommand(short left, short right){
        ByteBuffer buf = ByteBuffer.allocate(5);
        buf.put(VespuChat.MOTOR_COMMAND);
        buf.putShort(left);
        buf.putShort(right);
        send(buf.array());
    }

    private void send(byte[] message) {
        try{
            output.write(VespuChat.wrap(message));
        } catch (Exception e) {
            System.err.println("Exception sending VespuChat Message");
            e.printStackTrace();
        }
    }
}
