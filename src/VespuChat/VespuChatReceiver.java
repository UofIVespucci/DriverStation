package com.VespuChat;

import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.List;
import com.serial.Decoder;
import com.serial.PacketReader;

/**
 * Manage the lifecycle and asynchronous updating of a decoder configured
 *     for the VespuChat protocol
 */

public class VespuChatReceiver {
    private static final long READ_PERIOD = 50; //time between reads
    private static final TimeUnit UNIT = TimeUnit.MILLISECONDS; //READ_PERIOD units

    private ScheduledThreadPoolExecutor timer;
    private Decoder decoder;

    public VespuChatReceiver(InputStream is, List<PacketReader> prs){
        decoder = new Decoder(is, VespuChat.HEADER, VespuChat.FOOTER, VespuChat.CHECK);
        for(PacketReader pr : prs) decoder.addPacketReader(pr);
        timer = new ScheduledThreadPoolExecutor(1 /*num cores*/);
        timer.scheduleAtFixedRate(() -> decoder.update(), 0, READ_PERIOD, UNIT);
    }

    public void close() {
        //asks timer to stop scheduling new tasks
        timer.shutdown();
        try{
            timer.awaitTermination(4*READ_PERIOD, UNIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        decoder.close();
    }
}
