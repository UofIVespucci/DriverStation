package ui;

import javafx.scene.input.KeyCode;

public class GUImgr extends GUIManager {
    //Will make adjustable via GUI later
    private byte motorSpeed = 20;

    public void handleInput(KeyCode keyCode) {
        switch (keyCode) {
            case UP:    move(       motorSpeed,        motorSpeed); break;
            case DOWN:  move((byte)-motorSpeed, (byte)-motorSpeed); break;
            case LEFT:  move((byte)-motorSpeed,        motorSpeed); break;
            case RIGHT: move(       motorSpeed, (byte)-motorSpeed); break;
        }
    }
    public void handleUpInput(KeyCode keyCode) {
        switch (keyCode) {
            case UP:    move((byte)0,(byte)0); break;
            case DOWN:  move((byte)0,(byte)0); break;
            case LEFT:  move((byte)0,(byte)0); break;
            case RIGHT: move((byte)0,(byte)0); break;
        }
    }
    private void move(byte l, byte r) {
        t.send(com.VespuChat.messages.MotorCommand.build(l, r));
        System.out.println("Sending " + l + " " + r);
    }
}
