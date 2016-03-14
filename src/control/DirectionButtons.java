package com.control;

import java.util.*;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class DirectionButtons implements EventHandler<KeyEvent>{
    public enum Input { UP, DOWN, LEFT, RIGHT };
    public enum CommandState {
        NORTH(Input.UP),
        NORTHEAST(Input.UP, Input.RIGHT),
        EAST(Input.RIGHT),
        SOUTHEAST(Input.DOWN, Input.RIGHT),
        SOUTH(Input.DOWN),
        SOUTHWEST(Input.DOWN, Input.LEFT),
        WEST(Input.LEFT),
        NORTHWEST(Input.UP, Input.LEFT),
        STOPPED;

        private static Map<Set<Input>, CommandState> inputMap =
            new HashMap<Set<Input>, CommandState>();
        //populate the inputMap with each value's input set
        //this is done statically because enum's constructons are called
        //before their static fields are initialized
        static {
            for(CommandState cs : CommandState.values()){
                inputMap.put(cs.inputFrom, cs);
            }
        }
        static CommandState find(Input key){
            CommandState state = inputMap.get(EnumSet.of(key));
            return (state == null)? STOPPED : state;
        }
        static CommandState find(Input dominant, Input secondary){
            Set<Input> comboSet = EnumSet.of(dominant, secondary);
            CommandState combo = inputMap.get(comboSet);
            return (combo == null)? find(dominant) : combo;
        }

        private Set<Input> inputFrom;
        CommandState(){
            inputFrom = EnumSet.noneOf(Input.class);
        }
        CommandState(Input mapping){
            inputFrom = EnumSet.of(mapping);
        }
        CommandState(Input mapA, Input mapB){
            inputFrom = EnumSet.of(mapA, mapB);
        }
    };

    private static Map<KeyCode, Input> defaultMap;
    static {
        defaultMap = new HashMap<KeyCode, Input>();
        defaultMap.put(KeyCode.UP, Input.UP);
        defaultMap.put(KeyCode.DOWN, Input.DOWN);
        defaultMap.put(KeyCode.LEFT, Input.LEFT);
        defaultMap.put(KeyCode.RIGHT, Input.RIGHT);
    }
    private Map<KeyCode, Input> keymap = defaultMap;
    void setKeyMap(Map<KeyCode, Input> map){
        keymap = map;
        keyStack.clear();
    }
    void mapKey(KeyCode event, Input input){
        keymap.put(event, input);
        keyStack.clear();
    }

    private ObjectProperty<CommandState> commandState =
        new SimpleObjectProperty<CommandState>(CommandState.STOPPED);
    CommandState getState(){ return commandState.get(); }
    private void setState(CommandState cs){ commandState.set(cs); }
    ReadOnlyObjectProperty<CommandState> stateProperty(){ return commandState; }

    public DirectionButtons(){
    }

    public DirectionButtons(Map<KeyCode, Input> kepmap){
        setKeyMap(keymap);
    }

    private List<KeyCode> keyStack = new LinkedList<KeyCode>();
    private void recalculateCommandState(){
        CommandState newState = CommandState.STOPPED;
        if (keyStack.size() == 1) {
            Input input = keymap.get( keyStack.get(0) );
            newState = CommandState.find(input);
        } else if (keyStack.size() >= 2) {
            Input topKey = keymap.get( keyStack.get(0) );
            Input subKey = keymap.get( keyStack.get(1) );
            newState = CommandState.find(topKey, subKey);
        }
        if(newState != commandState.get()){
            commandState.set(newState);
        }
    }

    public void handle(KeyEvent event){
        if(keymap.get(event.getCode()) == null) return;
        event.consume(); //we know about this key; stop others from getting it
        if(event.getEventType() == KeyEvent.KEY_PRESSED){
            keyStack.add(0, event.getCode());
        } else if (event.getEventType() == KeyEvent.KEY_RELEASED){
            keyStack.remove(event.getCode());
        } else {
            return;
        }
        recalculateCommandState();
    }
}
