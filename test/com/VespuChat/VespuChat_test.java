package com.VespuChat;

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
    public void testMotorCommand() throws Exception {
        PacketReader reader = mock(PacketReader.class);
        List<PacketReader> rl = new ArrayList<PacketReader>();
        rl.add(reader);
        PipedOutputStream os = new PipedOutputStream();
        PipedInputStream is = new PipedInputStream(os);
        VespuChatTransmitter t = new VespuChatTransmitter(os);
        VespuChatReceiver r = new VespuChatReceiver(is, rl);

        when(reader.claim(VespuChat.MOTOR_COMMAND)).thenReturn(256);
        t.sendMotorCommand((short)0xaabb, (short)0xccdd);
        t.close();
        //closing the receiver will make it wait until it has finished the read
        r.close();

        verify(reader).handle(new byte[]{
                VespuChat.MOTOR_COMMAND, (byte)0xaa, (byte)0xbb, (byte)0xcc, (byte)0xdd}
            );
    }

}
