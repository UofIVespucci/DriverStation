package ui;

import javafx.concurrent.Task;
import org.jcodec.api.SequenceEncoder8Bit;
import org.jcodec.api.awt.AWTSequenceEncoder8Bit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class RecordingManager {
    WCFXPanel wcfxPanel;
    File outFile;
    private boolean isRecording = false;

    public RecordingManager(WCFXPanel w) {
        wcfxPanel = w;
    }

    public void record(String filename) {
        System.out.println("START RECORDING");
        isRecording = true;
        outFile = new File(filename);
        Task<Void> recStream = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                AWTSequenceEncoder8Bit enc = AWTSequenceEncoder8Bit.createSequenceEncoder8Bit(outFile, (int)wcfxPanel.getFps());
                enc.getEncoder().setKeyInterval(30);
                while (isRecording){
                    BufferedImage still = wcfxPanel.getStillImage();
                    enc.encodeImage(still);
                }
                enc.finish();
                return null;
            }
        };
        Thread thread = new Thread(recStream);
        thread.setDaemon(true);
        thread.start();
    }

    public void stopRecording(){
        isRecording = false;
    }

    public boolean getRecordingStatus(){
        return isRecording;
    }
}