package com.serial;

import java.io.InputStream;
import java.io.IOException;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialInputStream extends InputStream{
    private final SerialPort port;

    public SerialInputStream(SerialPort serialPort){
        port = serialPort;
    }
    @Override
    public void close() throws IOException {
        super.close();
        try{
            port.closePort();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
    public int read() throws IOException {
        try{
            if(port.getInputBufferBytesCount() > 0){
                return port.readIntArray(1)[0];
            } else {
                return -1;
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
