package com.VespuChat;

import com.VespuChat.messages.*;
import com.serial.*;

import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.PipedOutputStream;
import java.io.PipedInputStream;
import java.util.ArrayList;
import java.util.List;

public class VespuChat_test {
    @Test
    public void MotorCommand_integration() throws Exception {
        //the array is for getting around the final reference inner-class problem
        boolean[] pass = new boolean[]{false};
        PacketReader reader = new MotorCommand(){
            protected void onReceive(byte left, byte right){
                if(left == (byte)0xaa && right == (byte)0xbb) pass[0] = true;
            }
        };
        List<PacketReader> rl = new ArrayList<PacketReader>();
        rl.add(reader);
        PipedOutputStream os = new PipedOutputStream();
        PipedInputStream is = new PipedInputStream(os);
        VespuChatTransmitter t = new VespuChatTransmitter(os);
        VespuChatReceiver r = new VespuChatReceiver(is, rl);

        t.send(MotorCommand.build((byte)0xaa, (byte)0xbb));

        try{
            //Receiver is asynchronous, so we need to sleep at least one READ_PERIOD
            Thread.sleep(VespuChatReceiver.READ_PERIOD);
        } catch (Exception e){
            e.printStackTrace();
        }

        t.close();
        //closing the receiver will make it wait until it has finished the read
        r.close();

        assertTrue(pass[0]);
    }
}
