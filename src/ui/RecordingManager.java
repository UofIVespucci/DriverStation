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
    File outFile;

    private ChangeListener<Image> imageChangeListener = new ChangeListener<Image>() {
        @Override
        public void changed(ObservableValue<? extends Image> observable, Image oldValue, Image newValue) {

        }
    };

    public RecordingManager(WCFXPanel w) {
        wcfxPanel = w;
    }

    public void record(String filename, ObjectProperty<BufferedImage> imageObjectProperty, int fps) {
        if (activeRecorder == null || !activeRecorder.getIsRecording()) {
            activeRecorder = new Recorder(filename, imageObjectProperty, fps);
            activeRecorder.start();
        }

        System.out.println("START RECORDING");
    }

    public void stopRecording() {
        activeRecorder.stop();
    }

    public boolean getRecordingStatus(){
        if (activeRecorder == null) return false;
        return activeRecorder.getIsRecording();
    }
}