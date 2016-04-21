package ui;

import com.control.DirectionButtons;
import static com.control.DirectionButtons.*;

import javafx.scene.input.KeyCode;

public class GUImgr extends GUIManager {
    //Will make adjustable via GUI later
    private byte speed = 30;

    @Override protected void initRobotCommandListener(DirectionButtons db){
        db.stateProperty().addListener( (observable, olds, news) -> {
            System.out.println("In state "+news);
            switch(news){
                case NORTH:     move((byte) speed,(byte) speed); break;
                case SOUTH:     move((byte)-speed,(byte)-speed); break;
                case EAST:      move((byte)-speed,(byte) speed); break;
                case WEST:      move((byte) speed,(byte)-speed); break;
                case NORTHEAST: move((byte)     0,(byte) speed); break;
                case SOUTHEAST: move((byte)-speed,(byte)     0); break;
                case SOUTHWEST: move((byte)     0,(byte)-speed); break;
                case NORTHWEST: move((byte) speed,(byte)     0); break;
                case STOPPED:   move((byte)     0, (byte)    0); break;
            }
        });
    }

    protected void setHeadlightBrightness(byte l){
        if(t==null) return;
        t.send(com.VespuChat.messages.Headlight.build(l));
        System.out.println("Set lamp to "+l);
    }

    private void move(byte l, byte r) {
        if(t==null) return;
        t.send(com.VespuChat.messages.MotorCommand.build(l, r));
        System.out.println("Command " + l + " " + r);
    }

    public void ledBrightness(double d){
        System.out.println("LED Brightness changed to: " + (1/d));
    }
}
