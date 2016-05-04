package ui.streaming;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordingManager {
    Recorder activeRecorder;
    VideoOverlay videoOverlay;
    File outFile;

    private ChangeListener<Image> imageChangeListener = new ChangeListener<Image>() {
        @Override
        public void changed(ObservableValue<? extends Image> observable, Image oldValue, Image newValue) {

        }
    };

    public RecordingManager(VideoOverlay getVideoOverlay) {
        videoOverlay = getVideoOverlay;
    }

    public void record(String filename, ObjectProperty<BufferedImage> imageObjectProperty, int fps) {
        if (activeRecorder == null || !activeRecorder.getIsRecording()) {
            activeRecorder = new Recorder(filename, imageObjectProperty, fps);
            activeRecorder.start();
        }

        videoOverlay.setRecording(true);
        System.out.println("START RECORDING");
    }

    public void record(ObjectProperty<BufferedImage> imageObjectProperty, int fps) {
        record(getDateStamp(), imageObjectProperty, fps);
    }

    private String getDateStamp() {
        String s =  new SimpleDateFormat("MM-dd-yyyy (hh.mm aa)").format(new Date());
        int version = 0;
        File f = new File(s + "-" +  Integer.toString(version) + ".mp4");

        while (f.exists()) {
            version++;
            f = new File(s + "-" +  Integer.toString(version) + ".mp4");
        }

        return f.getName();
    }

    public void stopRecording() {
        if (activeRecorder != null && activeRecorder.getIsRecording()) activeRecorder.stop();
        videoOverlay.setRecording(false);
    }

    public boolean getRecordingStatus(){
        if (activeRecorder == null) return false;
        return activeRecorder.getIsRecording();
    }
}