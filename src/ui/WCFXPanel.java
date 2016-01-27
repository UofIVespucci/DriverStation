package ui;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import java.awt.image.BufferedImage;

/**
 * Modified variant of Sarxos Webcam-Capture JavaFX code available on GitHub:
 * http://bit.ly/1OOcUKp
 */

public class WCFXPanel extends BorderPane {
    private ImageView wcImg;
    private Webcam webcam;
    private BufferedImage getImage;
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

    private void initView()
    {
//        wcImg.setFitHeight(getHeight());
//        wcImg.setFitWidth(getWidth());
//        wcImg.prefHeight(getHeight());
//        wcImg.prefWidth(getWidth());
//        wcImg.fitWidthProperty().bind(widthProperty());
//        wcImg.fitHeightProperty().bind(heightProperty());
//        wcImg.maxHeight(Double.MAX_VALUE);
//        wcImg.maxWidth(Double.MAX_VALUE);
//        wcImg.setPreserveRatio(true);
//        prefHeight(Double.MAX_VALUE);
//        prefWidth(Double.MAX_VALUE);
    }

    protected void startStream() {
        isStreaming = true;
        Task<Void> stream = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (isStreaming) {
                    try {
                        if ((getImage = webcam.getImage()) != null) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Image img = SwingFXUtils.toFXImage(getImage, null);
                                    imgProperty.set(img);
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
        if (webcam!=null) webcam.open();
        setWebcam(webcam);
    }

    public void setWebcam(Webcam w)
    {
        if (w != null) {
            isStreaming = false;
            w.close();
            System.out.println("Webcam: " + w.getName());
            w.setViewSize(WebcamResolution.VGA.getSize());
            w.open();
            isStreaming = true;
            startStream();
        } else {
            System.out.println("No webcam detected");
        }
    }
}
