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
    private BufferedImage getImage;
    private BufferedImage stillImage;
    private boolean isStreaming = false;
    private ObjectProperty<Image> imgProperty = new SimpleObjectProperty<Image>();

    public WCFXPanel() {
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
        isStreaming = true;
        Task<Void> stream = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (isStreaming) {
                    try {
                        if ((getImage = webcam.getImage()) != null) {
                            stillImage = addDate(getImage);
                            Image img = SwingFXUtils.toFXImage(stillImage, null);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    if (getImage != null) {
                                        imgProperty.set(img);
                                    }
                                }
                            });
                            getImage.flush();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };

        Thread thread = new Thread(stream);
        thread.setDaemon(true);
        thread.start();
        wcImg.imageProperty().bind(imgProperty);
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
        Main.guiManager.stopRecording();
        if (webcam != null) webcam.close();
        if (w != null) {
            isStreaming = false;
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

    public BufferedImage addDate(BufferedImage image) {

        int w = image.getWidth();
        int h = image.getHeight();
        String cameraText = (cameraFace) ? "FRONT CAMERA" : "REVERSE CAMERA";

        BufferedImage modified = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = modified.createGraphics();
        g2.drawImage(image, null, 0, 0);

        g2.drawString(new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date()), 25, 25);
        g2.drawString(cameraText, 25, image.getHeight()-25);
        g2.dispose();

        modified.flush();

        return modified;
    }

    public void setCameraFace(boolean isFront)
    {
        cameraFace = isFront;
    }

    public BufferedImage getStillImage()
    {
        if (stillImage!=null) return stillImage;
        else return null;
    }

    public boolean getStreamingStatus(){ return isStreaming; }
}
