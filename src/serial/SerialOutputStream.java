package com.serial;

import java.io.OutputStream;
import java.io.IOException;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialOutputStream extends OutputStream{
    private final SerialPort port;

    public SerialOutputStream(SerialPort serialPort){
        port = serialPort;
    }
    @Override
    public void close() throws IOException {
        super.close();
        try{
            port.closePort();
        } catch (Exception e) {
        }
    }
    public void write(int b) throws IOException {
        try{
            port.writeInt(b);
        } catch (SerialPortException e) {
            throw new IOException(e);
        }
    }
}
