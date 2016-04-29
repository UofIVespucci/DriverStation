package ui;

import com.control.DirectionButtons;
import static com.control.DirectionButtons.*;

import javafx.scene.input.KeyCode;

public class GUImgr extends GUIManager {
    //Will make adjustable via GUI later
    private short speed = 255;

    @Override protected void initRobotCommandListener(DirectionButtons db){
        db.stateProperty().addListener( (observable, olds, news) -> {
            System.out.println("In state "+news);
            switch(news){
                case NORTH:     move((short) speed,(short) speed); break;
                case SOUTH:     move((short)-speed,(short)-speed); break;
                case EAST:      move((short)-speed,(short) speed); break;
                case WEST:      move((short) speed,(short)-speed); break;
                case NORTHEAST: move((short)     0,(short) speed); break;
                case SOUTHEAST: move((short)-speed,(short)     0); break;
                case SOUTHWEST: move((short)     0,(short)-speed); break;
                case NORTHWEST: move((short) speed,(short)     0); break;
                case STOPPED:   move((short)     0, (short)    0); break;
            }
        });
    }

    protected void setHeadlightBrightness(byte l){
        if(t==null) return;
        t.send(com.VespuChat.messages.Headlight.build(l));
        System.out.println("Set lamp to "+(int)(l&0xff));
    }

    private void move(short l, short r) {
        if(t==null) return;
        t.send(com.VespuChat.messages.MotorCommand.build(l, r));
        System.out.println("Command " + l + " " + r);
    }

    public void cameraSwitch(boolean state) {
        System.out.println("Set camera to "+state);
        if(t==null) return;
        t.send(com.VespuChat.messages.CameraSwitch.build(
            state ? (byte)255 : (byte)0)
        );
        //Handle Switch
    }

    public void ledBrightness(double d){
        setHeadlightBrightness((byte)(2.55*d));
        System.out.println("LED Brightness changed to: " + d);
    }
}
