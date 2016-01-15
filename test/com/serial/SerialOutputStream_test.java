package com.serial;

import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.util.*;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialOutputStream_test {
    @Test
    public void testWrite() throws Exception {
        SerialPort sp = mock(SerialPort.class);
        SerialOutputStream so = new SerialOutputStream(sp);

        so.write(1);

        verify(sp).writeInt(1);
    }
    @Test
    public void testClose() throws Exception {
        SerialPort sp = mock(SerialPort.class);
        SerialOutputStream so = new SerialOutputStream(sp);

        so.close();

        verify(sp).closePort();
    }
    @Test(expected=IOException.class)
    public void testWriteException() throws Exception {
        SerialPort sp = mock(SerialPort.class);
        SerialOutputStream so = new SerialOutputStream(sp);

        when(sp.writeInt(anyInt())).thenThrow(new SerialPortException("","",""));

        so.write(1);
    }
    @Test(expected=IOException.class)
    public void testCloseException() throws Exception {
        SerialPort sp = mock(SerialPort.class);
        SerialOutputStream so = new SerialOutputStream(sp);

        when(sp.closePort()).thenThrow(new SerialPortException("","",""));

        so.close();
    }
}
