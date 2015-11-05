package serial;

import com.serial.SerialConnectListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import jssc.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.*;

public class SerialConnectPanel extends HBox {
    private static class BaudRate{
        public String name;
        public int id;
        BaudRate(String name, int id){
            this.name = name;
            this.id = id;
        }
        @Override
        public String toString(){
            return name;
        }
    }
    private static BaudRate[] rates = new BaudRate[]{
        new BaudRate("110   ",SerialPort.BAUDRATE_110    ),
        new BaudRate("300   ",SerialPort.BAUDRATE_300    ),
        new BaudRate("600   ",SerialPort.BAUDRATE_600    ),
        new BaudRate("1200  ",SerialPort.BAUDRATE_1200   ),
        new BaudRate("4800  ",SerialPort.BAUDRATE_4800   ),
        new BaudRate("9600  ",SerialPort.BAUDRATE_9600   ),
        new BaudRate("14400 ",SerialPort.BAUDRATE_14400  ),
        new BaudRate("19200 ",SerialPort.BAUDRATE_19200  ),
        new BaudRate("38400 ",SerialPort.BAUDRATE_38400  ),
        new BaudRate("57600 ",SerialPort.BAUDRATE_57600  ),
        new BaudRate("115200",SerialPort.BAUDRATE_115200 )
    };
    private int baudRate = SerialPort.BAUDRATE_9600;
    private boolean showBaudPanel = false;
    private SerialConnectListener listener;
    private SerialPort connectedPort = null;
    private javafx.scene.control.Button refreshButton;
    private javafx.scene.control.Button connectButton;
    private ComboBox dropDown;
    private ComboBox<BaudRate> baudSelect;

    public SerialConnectPanel(SerialConnectListener listener){
        this.listener = listener;
        refreshButton = new Button();
        refreshButton.setText("Refresh");
        refreshButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                if(inProgress) return;
                dropDown.getItems().clear();
                addSerialList(dropDown);
            }
        });
        connectButton = new Button();
        connectButton.setText("Connect");
        connectButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                if (connectedPort == null) connect();
                else disconnect();
            }
        });

        dropDown = new ComboBox();
        addSerialList(dropDown);
        baudSelect = new ComboBox<BaudRate>();
        baudSelect.getItems().addAll(rates);
        //if the default baud rate is in the list, choose it
        for(int i=0; i<rates.length; i++){
            if(rates[i].id == baudRate){
                baudSelect.getSelectionModel().select(i);
                break;
            }
        }
        baudSelect.setVisible(false);
        baudSelect.managedProperty().bind(baudSelect.visibleProperty());
        getChildren().add(refreshButton);
        getChildren().add(dropDown);
        getChildren().add(connectButton);
        getChildren().add(baudSelect);
        setPadding(new Insets(5, 5, 5, 5));
        setSpacing(5);
        setStyle("-fx-background-color: #336699;");
        refreshButton.getStyleClass().add("carbon-button");
    }

    public void setBaudRate(int baudRate){
        this.baudRate = baudRate;
    }

    public void showBaudSelector(boolean show){
        baudSelect.setVisible(show);
    }

    /*
        Sometimes serial port actions can block for many seconds, so connect and
        disconnect are run off the UI thread. These functions control the four
        states and prevent multiple port actions from racing eachother
    */
    private static final String BUTTON_CONNECTING    = "Connecting";
    private static final String BUTTON_CONNECTED     = "Disconnect";
    private static final String BUTTON_DISCONNECTING = "Disabling ";
    private static final String BUTTON_DISCONNECTED  = " Connect  ";
    private boolean inProgress = false;
    private void connect(){
        if(inProgress){
            System.err.println("Connect command issued while a change was in Progress");
            return;
        }
        refreshButton.setDisable(true);
        dropDown.setDisable(true);
        connectButton.setDisable(true);
        connectButton.setText(BUTTON_CONNECTING);
        inProgress = true;
        (new Thread(connectSerial)).start();
    }
    private void connectDone(){
        refreshButton.setDisable(true);
        dropDown.setDisable(true);
        connectButton.setDisable(true);
        connectButton.setText(BUTTON_CONNECTED);
        inProgress = false;
    }
    private void disconnect(){
        if(inProgress){
            System.err.println("Connect command issued while a change was in Progress");
            return;
        }
        refreshButton.setDisable(true);
        dropDown.setDisable(true);
        connectButton.setDisable(true);
        connectButton.setText(BUTTON_DISCONNECTING);
        inProgress = true;
        (new Thread(disconnectSerial)).start();
    }
    private void disconnectDone(){
        refreshButton.setDisable(false);
        dropDown.setDisable(false);
        connectButton.setDisable(false);
        connectButton.setText(BUTTON_DISCONNECTED);
        inProgress = false;
    }

    private void addSerialList(ComboBox box){
        String[] portNames = SerialPortList.getPortNames();
        box.setItems(FXCollections.observableArrayList(portNames));
    }

    private Runnable connectSerial = new Runnable(){
        public void run(){
            if(dropDown.getSelectionModel().getSelectedItem() == null) return;

            SerialPort serialPort = new SerialPort((String)dropDown.getSelectionModel().getSelectedItem());

            try{
                serialPort.openPort();

                if(baudSelect.isVisible()){
                    baudRate = ((BaudRate)baudSelect.getSelectionModel().getSelectedItem()).id;
                }

                serialPort.setParams(baudRate,              SerialPort.DATABITS_8,
                                     SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
                                              SerialPort.FLOWCONTROL_RTSCTS_OUT);
            } catch(SerialPortException ex){
                System.err.println(ex.getMessage());
                return;
            }

            connectedPort = serialPort;
            listener.connectionEstablished(serialPort);
            connectDone();
        }
    };

    private Runnable disconnectSerial = new Runnable(){
        public void run(){
            listener.disconnectRequest();

            try{
                connectedPort.closePort();
            } catch (Exception e) {
                e.printStackTrace();
            }

            connectedPort = null;
            disconnectDone();
        }
    };
}
