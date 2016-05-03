package ui.streaming;

import com.github.sarxos.webcam.Webcam;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lance on 4/28/2016.
 */
public class Streamer {
    Boolean faceFront = true;
    Webcam webcam;
    ObjectProperty<BufferedImage>  imgProperty;
    ObjectProperty<Image>  fxImgProperty;
    int fps;
    private final ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);
    private Boolean isStreaming = false;

    public Streamer(Webcam getWebcam, ObjectProperty<BufferedImage> getImgProperty, ObjectProperty<Image> getFXImgProperty, int getFps) {
        webcam = getWebcam;
        imgProperty = getImgProperty;
        fxImgProperty = getFXImgProperty;
        fps = getFps;
    }

    public void start(){
        Runnable stream = new Runnable() {
            Integer count = 0;
            Long millis = 0L;
            BufferedImage getImage;
            @Override
            public void run() {
                if ((getImage = webcam.getImage()) != null) {
                    count++;
//                    System.out.println(System.currentTimeMillis()+" Frame "+count+" "+(System.currentTimeMillis()-millis));
                    millis = System.currentTimeMillis();
                    getImage = addDate(getImage);
                    imgProperty.set(getImage);
                    Image img = SwingFXUtils.toFXImage(getImage, null);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (getImage != null) {
                                fxImgProperty.set(img);
                            }
                        }
                    });
                    getImage.flush();
                }
            }
        };
        schedule.scheduleAtFixedRate(stream, 0, 1000/fps, TimeUnit.MILLISECONDS);
        isStreaming = true;
    }

    public void stop(){
        schedule.shutdown();
        try { schedule.awaitTermination(1000, TimeUnit.MILLISECONDS); }
        catch (InterruptedException e) { System.out.println("Stream close may have been interrupted"); }
        isStreaming = false;
    }

    public Boolean getIsStreaming() {
        return isStreaming;
    }

    public BufferedImage addDate(BufferedImage image) {

        int w = image.getWidth();
        int h = image.getHeight();
        String cameraText = (faceFront) ? "FRONT CAMERA" : "REVERSE CAMERA";

        BufferedImage modified = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = modified.createGraphics();
        g2.drawImage(image, null, 0, 0);

        g2.drawString(new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date()), 25, 25);
        g2.drawString(cameraText, 25, image.getHeight()-25);
        g2.dispose();

        modified.flush();

        return modified;
    }
}
