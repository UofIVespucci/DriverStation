package ui;

import com.Main;
import com.github.sarxos.webcam.*;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Modified variant of Sarxos Webcam-Capture JavaFX code available on GitHub:
 * http://bit.ly/1OOcUKp
 */

public class WCFXPanel extends BorderPane {
    private boolean cameraFace = true;
    private ImageView wcImg;
    private Webcam webcam;
    public ObjectProperty<Image> imgProperty = new SimpleObjectProperty<Image>();
    public ObjectProperty<BufferedImage> stillProp = new SimpleObjectProperty<>();
    private Streamer activeStreamer;
    private RecordingManager rManager;

    public WCFXPanel(RecordingManager r) {
        rManager = r;
        wcImg = new ImageView();
        setCenter(wcImg);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initView();
            }
        });
        initWebcam();
    }

    public double getFps(){
        if (webcam!=null) return webcam.getFPS();
        else return 0;
    }

    private void initView()
    {
        wcImg.fitWidthProperty().bind(Main.guiManager.getVOWProperty());
        wcImg.fitHeightProperty().bind(Main.guiManager.getVOHProperty());
        wcImg.setPreserveRatio(true);
        wcImg.minWidth(0);
        wcImg.minHeight(0);
        minWidth(0);
        minHeight(0);
    }

    protected void startStream() {
        if (activeStreamer == null || !activeStreamer.getIsStreaming()) {
            activeStreamer = new Streamer(webcam, stillProp, imgProperty, 8);
            activeStreamer.start();
        }

        wcImg.imageProperty().bind(imgProperty);
    }

    protected void stopStream() {
        if (activeStreamer != null)
            activeStreamer.stop();
    }

    private void initWebcam()
    {
        for (Webcam wc : Webcam.getWebcams()) {
            System.out.println(wc.getName());
        }
        webcam = Webcam.getDefault();

        try {
            if (webcam!=null && !webcam.isOpen()) webcam.open(true);
            setWebcam(webcam);
        } catch (com.github.sarxos.webcam.WebcamLockException e) {
            System.err.println("Initial Webcam " + webcam.getName() + " in use!");
        }
    }

    public void setWebcam(Webcam w)
    {
        rManager.stopRecording();
        stopStream();
        if (webcam != null) webcam.close();
        if (w != null) {
            w.close();
            w.setViewSize(WebcamResolution.VGA.getSize());
            webcam = w;
            try { w.open(true); }
            catch (WebcamLockException e) {
                System.out.println("Webcam " + webcam.getName() + " in use!");
            }
            startStream();
        } else {
            System.out.println("No webcam detected");
        }
    }

    public boolean getStreamingStatus(){ return activeStreamer.getIsStreaming(); }
}
