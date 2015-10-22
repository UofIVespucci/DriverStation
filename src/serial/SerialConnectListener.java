package com.serial;

import jssc.SerialPort;

public interface SerialConnectListener{
    /**
     * This event provides a connected serial port ready to be used
     */
    public void connectionEstablished(SerialPort newConnection);
    /**
     * This event signals that the previously selected serial port will be
     * disconnect soon and should be discarded.
     */
    public void disconnectRequest();
}
