package ui;

import javafx.concurrent.Task;
//import org.jcodec.api.SequenceEncoder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class RecordingManager {
    WCFXPanel wcfxPanel;
    BufferedImage still;
    boolean isRecording = false;

    public RecordingManager(WCFXPanel w){
        wcfxPanel = w;
    }

    public void record(){
        //Declare video writer
        //Init video writer
//        File fo = null;
//        try{
//            File file = Paths.get("MyTestRecording.mp4");
//        fo = new File("MyTestRecording.mp4");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        SequenceEncoder enc = new SequenceEncoder(Paths.get("MyTestRecording.mp4"));
//        if (fo!=null)  SequenceEncoder enc = new SequenceEncoder(fo);
//        SequenceEncoder enc = null;
//        try{
//            final SequenceEncoder = new SequenceEncoder(fo);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Task<Void> recStream = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (isRecording) {
                    try {
//                        File fo = new File("MyTestRecording.mp4");
//                        SequenceEncoder enc = new SequenceEncoder(fo);
//                        Encoder
                        still = wcfxPanel.getStillImage();
                        if (still!=null) {
//                            if (enc != null)
//                            enc.encodeNativeFrame(still);
                            //Convert Image
                            //Add video to writer
                            //Make writer encode
                            Thread.sleep(33); //30 fps
                        }
                    } catch (Exception e)  {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };

        Thread thread = new Thread(recStream);
        thread.setDaemon(true);
        thread.start();
    }
}
