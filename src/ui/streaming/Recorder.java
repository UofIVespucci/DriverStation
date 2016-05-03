package ui.streaming;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.jcodec.api.awt.AWTSequenceEncoder8Bit;
import sun.nio.ch.ThreadPool;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;

/**
 * Created by Lance on 4/28/2016.
 */
public class Recorder {
    private File outFile;
    private ObjectProperty<BufferedImage> imgProperty = new SimpleObjectProperty<BufferedImage>();
    private int fps;
    private Boolean isRecording = false;
    private AWTSequenceEncoder8Bit encoder8Bit;
    private final ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);

    public Boolean getIsRecording() { return isRecording; }

    public Recorder(String getFileName, ObjectProperty<BufferedImage> getImgProperty, int getFps) {
        outFile = new File(getFileName);
        imgProperty = getImgProperty;
        fps = getFps;
    }

    public void start(){
        try { encoder8Bit = AWTSequenceEncoder8Bit.createSequenceEncoder8Bit(outFile, fps); }
        catch (IOException e) { e.printStackTrace(); }
        encoder8Bit.getEncoder().setKeyInterval(fps);

        Runnable recordRunnable = new Runnable() {
            Integer count = 0;
            Long millis = 0L;
            @Override
            public void run() {
                try {
                    count++;
//                    System.out.println(System.currentTimeMillis()+" Frame "+count+" "+(System.currentTimeMillis()-millis));
                    millis = System.currentTimeMillis();
                    encoder8Bit.encodeImage(imgProperty.get());
                } catch (Exception e) {
                    System.out.println("SOMETHING WENT TERRIBLY, TERRIBLY WRONG"); e.printStackTrace();
                }
            }
        };
        schedule.scheduleAtFixedRate(recordRunnable, 0, 1000/fps, TimeUnit.MILLISECONDS);
        isRecording = true;
    }

    public void stop(){
        schedule.shutdown();
        try { schedule.awaitTermination(1000, TimeUnit.MILLISECONDS); }
        catch (InterruptedException e) { System.out.println("Video close may have been interrupted"); }
        isRecording = false;
        try { encoder8Bit.finish(); }
        catch (IOException e) {e.printStackTrace();}
    }
}
