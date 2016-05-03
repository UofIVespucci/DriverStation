package ui.streaming;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.io.File;

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

    public void stopRecording() {
        if (activeRecorder != null && activeRecorder.getIsRecording()) activeRecorder.stop();
        videoOverlay.setRecording(false);
    }

    public boolean getRecordingStatus(){
        if (activeRecorder == null) return false;
        return activeRecorder.getIsRecording();
    }
}