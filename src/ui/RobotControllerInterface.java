package ui;

import com.control.DirectionButtons;

/**
 * Created by Lance on 5/4/2016.
 */
interface RobotControllerInterface {
    void initRobotCommandListener(DirectionButtons db);
    void setHeadlightBrightness(byte l);
    void move(short l, short r);
    void cameraSwitch(boolean state);
    void ledBrightness(double d);
}
