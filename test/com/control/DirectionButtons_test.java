package com.control;

import com.control.DirectionButtons.*;

import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import javafx.event.*;
import javafx.scene.input.*;
import static javafx.scene.input.KeyEvent.*;
import static javafx.scene.input.KeyCode.*;

public class DirectionButtons_test{
    KeyEvent makeEvent(EventType<KeyEvent> type, KeyCode kc){
        return new KeyEvent(type, kc.getName(), kc.getName(), kc,
                                        false, false, false, false);
    }
    @Test
    public void defaultStopped(){
        DirectionButtons db = new DirectionButtons();
        assertEquals(DirectionButtons.CommandState.STOPPED, db.getState());
    }
    @Test
    public void pressActivation(){
        DirectionButtons db = new DirectionButtons();
        db.handle(makeEvent(KEY_PRESSED, UP));
        assertEquals(DirectionButtons.CommandState.NORTH, db.getState());
    }
    @Test
    public void pressDeactivation(){
        DirectionButtons db = new DirectionButtons();
        db.handle(makeEvent(KEY_PRESSED,  UP));
        db.handle(makeEvent(KEY_RELEASED, UP));
        assertEquals(DirectionButtons.CommandState.STOPPED, db.getState());
    }
    @Test
    public void layeredKeyPresses(){
        DirectionButtons db = new DirectionButtons();
        db.handle(makeEvent(KEY_PRESSED, UP));
        db.handle(makeEvent(KEY_PRESSED, RIGHT));
        assertEquals(DirectionButtons.CommandState.NORTHEAST, db.getState());
    }
    @Test
    public void contradictoryKeyPresses(){
        DirectionButtons db = new DirectionButtons();
        db.handle(makeEvent(KEY_PRESSED, UP));
        db.handle(makeEvent(KEY_PRESSED, DOWN));
        assertEquals(DirectionButtons.CommandState.SOUTH, db.getState());
    }
    @Test
    public void returnToPreviousPress(){
        DirectionButtons db = new DirectionButtons();
        db.handle(makeEvent(KEY_PRESSED,  UP));
        db.handle(makeEvent(KEY_PRESSED,  RIGHT));
        db.handle(makeEvent(KEY_RELEASED, RIGHT));
        assertEquals(DirectionButtons.CommandState.NORTH, db.getState());
    }
    @Test
    public void earlierKeyReleased(){
        DirectionButtons db = new DirectionButtons();
        db.handle(makeEvent(KEY_PRESSED,  UP));
        db.handle(makeEvent(KEY_PRESSED,  RIGHT));
        db.handle(makeEvent(KEY_RELEASED, UP));
        assertEquals(DirectionButtons.CommandState.EAST, db.getState());
    }
    @Test
    public void mapNewKey(){
        DirectionButtons db = new DirectionButtons();
        db.mapKey(KeyCode.S, Input.DOWN);
        db.handle(makeEvent(KEY_PRESSED,  S));
        assertEquals(DirectionButtons.CommandState.SOUTH, db.getState());
    }
    @Test
    public void keyPressEventRepeated(){
        DirectionButtons db = new DirectionButtons();
        db.handle(makeEvent(KEY_PRESSED,  DOWN));
        db.handle(makeEvent(KEY_PRESSED,  DOWN));
        db.handle(makeEvent(KEY_PRESSED,  DOWN));
        db.handle(makeEvent(KEY_PRESSED,  DOWN));
        db.handle(makeEvent(KEY_PRESSED,  DOWN));
        db.handle(makeEvent(KEY_PRESSED,  DOWN));
        db.handle(makeEvent(KEY_PRESSED,  DOWN));
        db.handle(makeEvent(KEY_PRESSED,  DOWN));
        db.handle(makeEvent(KEY_RELEASED, DOWN));
        assertEquals(DirectionButtons.CommandState.STOPPED, db.getState());
    }
    @Test
    public void keyRemap(){
        DirectionButtons db = new DirectionButtons();
        Map<KeyCode, Input> newMap = new HashMap<KeyCode, Input>();
        newMap.put(W, Input.UP);
        newMap.put(A, Input.LEFT);
        newMap.put(S, Input.DOWN);
        newMap.put(D, Input.RIGHT);
        db.setKeyMap(newMap);
        db.handle(makeEvent(KEY_PRESSED,  W));
        assertEquals(DirectionButtons.CommandState.NORTH, db.getState());
        db.handle(makeEvent(KEY_RELEASED,  W));
        db.handle(makeEvent(KEY_PRESSED,  A));
        assertEquals(DirectionButtons.CommandState.WEST, db.getState());
        db.handle(makeEvent(KEY_RELEASED,  A));
        db.handle(makeEvent(KEY_PRESSED,  S));
        assertEquals(DirectionButtons.CommandState.SOUTH, db.getState());
        db.handle(makeEvent(KEY_RELEASED,  S));
        db.handle(makeEvent(KEY_PRESSED,  D));
        assertEquals(DirectionButtons.CommandState.EAST, db.getState());
        db.handle(makeEvent(KEY_RELEASED,  D));
    }
    @Test
    public void duplicateKeyActivation(){
        DirectionButtons db = new DirectionButtons();
        db.mapKey(KeyCode.S, Input.DOWN);
        db.handle(makeEvent(KEY_PRESSED,  S));
        db.handle(makeEvent(KEY_PRESSED,  DOWN));
        assertEquals(DirectionButtons.CommandState.SOUTH, db.getState());
        db.handle(makeEvent(KEY_RELEASED,  DOWN));
        assertEquals(DirectionButtons.CommandState.SOUTH, db.getState());
        db.handle(makeEvent(KEY_RELEASED,  S));
        assertEquals(DirectionButtons.CommandState.STOPPED, db.getState());
    }
    @Test
    public void propertyListener(){
        DirectionButtons db = new DirectionButtons();
        final boolean[] passed = {false};
        db.stateProperty().addListener(
            (obsv, news, olds)->{passed[0] = true;}
        );
        db.handle(makeEvent(KEY_PRESSED, UP));
        assertEquals(true, passed[0]);
    }
}
