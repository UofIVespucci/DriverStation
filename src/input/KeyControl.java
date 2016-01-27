package input;

import javafx.scene.input.KeyCode;

public class KeyControl{
    //Will be used to field keyboard input to appropriate methods
    public void useInput(KeyCode keyCode)
    {
        switch (keyCode)
        {
        case UP:
        case LEFT:
        case RIGHT:
        case DOWN:
            //Send to robot
            break;
        }
    }
}
