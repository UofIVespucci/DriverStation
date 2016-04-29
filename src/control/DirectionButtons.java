package com.control;

import java.util.*;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Event handler for keys that translates a group of keys mapping to
 *     UP, DOWN, LEFT, RIGHT into a overall 8 direction CommandState
 *     that remembers key click order and responds to releases appropriately
 * CommandState change callbacks can be attached to the stateProperty()
 */
public class DirectionButtons implements EventHandler<KeyEvent>{
    public enum Input { UP, DOWN, LEFT, RIGHT };
    public enum CommandState {
        NORTH(Input.UP),
        SOUTH(Input.DOWN),
        EAST(Input.RIGHT),
        WEST(Input.LEFT),
        NORTHEAST(Input.UP, Input.RIGHT),
        SOUTHEAST(Input.DOWN, Input.RIGHT),
        SOUTHWEST(Input.DOWN, Input.LEFT),
        NORTHWEST(Input.UP, Input.LEFT),
        STOPPED;

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
        /**
         * Retreives the CommandState that results from `key`
         * returns "STOPPED" if nothing is found
         */
        static CommandState find(Input key){
            CommandState state = inputMap.get(EnumSet.of(key));
            return (state == null)? STOPPED : state;
        }
        /**
         * Retreives the CommandState that results from a pair of Inputs
         * Returns the compound direction if possible
         * Otherwise retrieves the direction based solely on the dominant key
         * returns "STOPPED" if nothing is found
         */
        static CommandState find(Input dominant, Input secondary){
            Set<Input> comboSet = EnumSet.of(dominant, secondary);
            CommandState combo = inputMap.get(comboSet);
            return (combo == null)? find(dominant) : combo;
        }
    };

    private Map<KeyCode, Input> keymap;

    private List<KeyCode> keyStack = new LinkedList<KeyCode>();

    private ObjectProperty<CommandState> commandState =
        new SimpleObjectProperty<CommandState>(CommandState.STOPPED);
    private void setState(CommandState cs){ commandState.set(cs); }
    /**
     * Constructs a DirectionButtons Event Handler
     *     It will have the default keymap using the arrow keys
     */
    public DirectionButtons(){
        keymap = new HashMap<KeyCode, Input>();
        keymap.put(KeyCode.UP, Input.UP);
        keymap.put(KeyCode.DOWN, Input.DOWN);
        keymap.put(KeyCode.LEFT, Input.LEFT);
        keymap.put(KeyCode.RIGHT, Input.RIGHT);
    }
    /**
     * Constructs a DirectionButtons Event Handler
     * @param  kepmap The mapping of KeyCodes to Input directions to apply
     */
    public DirectionButtons(Map<KeyCode, Input> kepmap){
        setKeyMap(keymap);
    }
    /**
     * Remaps the keys DirectionButtons looks for according to `map`
     * Any memory of currently pressed keys is cleared
     * @param map The mapping of KeyCodes to Input directions to apply
     */
    public void setKeyMap(Map<KeyCode, Input> map){
        keymap = map;
        keyStack.clear();
    }
    /**
     * add a new event->input combination to the current keymap
     * Any memory of currently pressed keys is cleared
     * @param event The Keycode to begin registering
     * @param input The directional input `keycode` represents
     */
    public void mapKey(KeyCode event, Input input){
        keymap.put(event, input);
        keyStack.clear();
    }
    /**
     * Retrieve the current CommandState
     */
    public CommandState getState(){
        return commandState.get();
    }
    /**
     * Retrieve the current CommandState as a read only property
     * Attach listeners to the property to receive state change callbacks
     */
    public ReadOnlyObjectProperty<CommandState> stateProperty(){
        return commandState;
    }

    public void handle(KeyEvent event){
        if(keymap.get(event.getCode()) == null) return;
        event.consume(); //we know about this key; stop others from getting it
        if(event.getEventType() == KeyEvent.KEY_PRESSED){
            if(!keyStack.contains(event.getCode())){
                keyStack.add(0, event.getCode());
            }
        } else if (event.getEventType() == KeyEvent.KEY_RELEASED){
            keyStack.remove(event.getCode());
        } else {
            return;
        }
        recalculateCommandState();
    }

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

}
