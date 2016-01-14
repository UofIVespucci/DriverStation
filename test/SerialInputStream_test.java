import com.serial.*;

import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.util.*;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialInputStream_test {
    @Test
    public void testEmpty() throws IOException, SerialPortException {
        SerialPort sp = mock(SerialPort.class);
        SerialInputStream sis = new SerialInputStream(sp);

        assertEquals(sis.read(), -1);
    }

    @Test
    public void testPassThrough() throws IOException, SerialPortException {
        SerialPort sp = mock(SerialPort.class);
        SerialInputStream sis = new SerialInputStream(sp);

        when(sp.getInputBufferBytesCount()).thenReturn(1);
        when(sp.readIntArray(1)).thenReturn(new int[]{ 12 });

        assertEquals(12, sis.read());
    }

    @Test
    public void testClose() throws IOException, SerialPortException {
        SerialPort sp = mock(SerialPort.class);
        SerialInputStream sis = new SerialInputStream(sp);

        sis.close();

        verify(sp).closePort();
    }

    @Test(expected=IOException.class)
    public void testReadException() throws IOException, SerialPortException {
        SerialPort sp = mock(SerialPort.class);
        SerialInputStream sis = new SerialInputStream(sp);

        when(sp.getInputBufferBytesCount()).thenReturn(1);
        when(sp.readIntArray(1)).thenThrow(new SerialPortException("","",""));

        sis.read();
    }

    @Test(expected=IOException.class)
    public void testCloseException() throws IOException, SerialPortException {
        SerialPort sp = mock(SerialPort.class);
        SerialInputStream sis = new SerialInputStream(sp);

        when(sp.closePort()).thenThrow(new SerialPortException("","",""));

        sis.close();
    }
}
