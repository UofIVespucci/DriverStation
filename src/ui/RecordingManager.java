package ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.io.File;

public class RecordingManager {
    Recorder activeRecorder;
    WCFXPanel wcfxPanel;
    VideoOverlay videoOverlay;
    File outFile;

    private ChangeListener<Image> imageChangeListener = new ChangeListener<Image>() {
        @Override
        public void changed(ObservableValue<? extends Image> observable, Image oldValue, Image newValue) {

        }
    };

    public RecordingManager(WCFXPanel w, VideoOverlay getVideoOverlay) {
        wcfxPanel = w;
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

    public void stopRecording() {
        activeRecorder.stop();
        videoOverlay.setRecording(false);
    }

    public boolean getRecordingStatus(){
        if (activeRecorder == null) return false;
        return activeRecorder.getIsRecording();
    }
}