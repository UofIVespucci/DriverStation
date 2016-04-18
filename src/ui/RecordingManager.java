package ui;

import com.Main;
import io.humble.video.*;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;
//import io.humble.video.awt.MediaPictureConverterFactory;
import javafx.concurrent.Task;
import java.awt.image.BufferedImage;

public class RecordingManager {
    WCFXPanel wcfxPanel;
    BufferedImage still;
    private boolean isRecording = false;

    public RecordingManager(WCFXPanel w){
        wcfxPanel = w;
    }

    public void record(String filename, String formatname, int fps, int res_x, int res_y){
        final Rational framerate = Rational.make(1, fps);
        final Muxer muxer = Muxer.make(filename, null, formatname);
        final MuxerFormat muxerFormat = muxer.getFormat();
        final Codec codec = Codec.findEncodingCodec(muxerFormat.getDefaultVideoCodecId());
        final PixelFormat.Type pixelFormat = PixelFormat.Type.PIX_FMT_YUV420P;

        //Needs Width, Height, and Pixel Format before usable
        final Encoder encoder = Encoder.make(codec);
        encoder.setWidth(res_x);
        encoder.setHeight(res_y);
        encoder.setPixelFormat(pixelFormat);
        encoder.setTimeBase(framerate);

        if(muxerFormat.getFlag(MuxerFormat.Flag.GLOBAL_HEADER))
            encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);

        encoder.open(null, null);
//        muxer.addNewStream(encoder);
        try {
            muxer.open(null, null);
        } catch (Exception e){
            e.printStackTrace();
        }

//        muxer.addNewStream(encoder);
//        muxer.open(null, null);

        MediaPicture mediaPicture = MediaPicture.make(
                encoder.getWidth(),
                encoder.getHeight(),
                encoder.getPixelFormat());

        mediaPicture.setTimeBase(framerate);

        final MediaPacket mediaPacket = MediaPacket.make();
        isRecording = true;

        /* Begin main recording loop. Should record until told to stop. Might need to add a hard-cap at some point */
        Task<Void> recStream = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int i = 0;
                MediaPictureConverter mediaPictureConverter = null;
                while (isRecording) {
                    try {
                        final BufferedImage still = convertToType(
                                wcfxPanel.getStillImage(),
                                BufferedImage.TYPE_3BYTE_BGR);

                        if (mediaPictureConverter == null)
//                            mediaPictureConverter
                            mediaPictureConverter = MediaPictureConverterFactory.createConverter(still, mediaPicture);
                        mediaPictureConverter.toPicture(mediaPicture, still, i);

                        do {
                            encoder.encode(mediaPacket, mediaPicture);
                            if (mediaPacket.isComplete())
                                muxer.write(mediaPacket, false);
                        } while (mediaPacket.isComplete());

                        i++;
                        Thread.sleep((long) (1000 * framerate.getDouble()));
                    } catch (Exception e)  {
                        e.printStackTrace();
                    }
                }
                do {
                    encoder.encode(mediaPacket, null);
                    if (mediaPacket.isComplete())
                        muxer.write(mediaPacket, false);
                } while (mediaPacket.isComplete());

                muxer.close();
                return null;
            }
        };

        Thread thread = new Thread(recStream);
        thread.setDaemon(true);
        thread.start();
    }

    //This function is taken verbatim from Art Clarke's API demonstrations
    //http://bit.ly/1WoAoKT
    public static BufferedImage convertToType(BufferedImage sourceImage,
                                              int targetType)
    {
        BufferedImage image;

        // if the source image is already the target type, return the source image

        if (sourceImage.getType() == targetType)
            image = sourceImage;

            // otherwise create a new image of the target type and draw the new
            // image

        else
        {
            image = new BufferedImage(sourceImage.getWidth(),
                    sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }

        return image;
    }

    public void stopRecording(){ isRecording = false; }

    public boolean getRecordingStatus(){ return  isRecording; }
}
