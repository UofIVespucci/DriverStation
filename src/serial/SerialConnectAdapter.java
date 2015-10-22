package com.serial;

import jssc.SerialPort;
import com.serial.SerialConnectListener;

public class SerialConnectAdapter implements SerialConnectListener {
    public void connectionEstablished(SerialPort newConnection){
    }
    public void disconnectRequest(){
    }
}
