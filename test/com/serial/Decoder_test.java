package com.serial;

import com.serial.*;

import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class Decoder_test {
    private Random rand = new Random();
    private byte[] testHeader   = "H".getBytes();
    private byte[] testFooter   = "F".getBytes();
    private byte[] testChecksum = "CC".getBytes();
    private Checksum mockChecksum = mock(Checksum.class);

    public Decoder_test() throws IOException {
        when(mockChecksum.length()).thenReturn(testChecksum.length);
        when(mockChecksum.calc(any(byte[].class))).thenReturn(testChecksum);
    }

    private InputStream input(byte[] header, byte[] data, byte[] checksum, byte[] footer) throws IOException {
        ByteArrayOutputStream concat = new ByteArrayOutputStream();
        concat.write(header);
        concat.write(data);
        concat.write(checksum);
        concat.write(footer);
        return new ByteArrayInputStream(concat.toByteArray());
    }

    private InputStream input(byte[] data) throws IOException {
        return input(testHeader, data, testChecksum, testFooter);
    }

    private Decoder testDecoder(InputStream is, PacketReader pr) throws IOException {
        Decoder decoder = new Decoder(is, testHeader, testFooter, mockChecksum);
        decoder.addPacketReader(pr);
        return decoder;
    }

    private Decoder testDecoder(byte[] is, PacketReader pr) throws IOException {
        return testDecoder(input(is), pr);
    }

    private byte[] randomData(int length) throws IOException {
        byte[] bytes = new byte[length];
        rand.nextBytes(bytes);
        return bytes;
    }

    @Test
    public void matchRandomData() throws IOException {
        for(int i=1; i<64; i++){
            byte[] data        = randomData(i);
            PacketReader mRead = mock(PacketReader.class);
            Decoder decoder    = testDecoder(data, mRead);

            when(mRead.claim(data[0])).thenReturn(data.length);
            decoder.update();

            verify(mRead).handle(data);
        }
    }

    @Test
    public void checksumOverCorrectRange() throws IOException {
        byte[] data        = "checksum me!".getBytes();
        PacketReader mRead = mock(PacketReader.class);
        Checksum check     = mock(Checksum.class);
        Decoder decoder    = new Decoder(input(data), testHeader, testFooter, check);

        when(mRead.claim(data[0])).thenReturn(data.length);
        when(check.length()).thenReturn(testChecksum.length);
        when(check.calc(any(byte[].class))).thenReturn(testChecksum);
        decoder.addPacketReader(mRead);
        decoder.update();

        verify(check).calc(data);
    }

    @Test
    public void longHeader() throws IOException {
        byte[] data        = "longHeader".getBytes();
        byte[] longHeader  = "abcdefghijklmnop".getBytes();
        InputStream stream = input(longHeader, data, testChecksum, testFooter);
        PacketReader mRead = mock(PacketReader.class);
        Decoder decoder    = new Decoder(stream, longHeader, testFooter, mockChecksum);

        decoder.addPacketReader(mRead);
        when(mRead.claim(data[0])).thenReturn(data.length);
        decoder.update();

        verify(mRead).handle(data);
    }

    @Test
    public void longFooter() throws IOException {
        byte[] data        = "longFooter".getBytes();
        byte[] longFooter  = "abcdefghijklmnop".getBytes();
        InputStream stream = input(testHeader, data, testChecksum, longFooter);
        PacketReader mRead = mock(PacketReader.class);
        Decoder decoder    = new Decoder(stream, testHeader, longFooter, mockChecksum);

        decoder.addPacketReader(mRead);
        when(mRead.claim(data[0])).thenReturn(data.length);
        decoder.update();

        verify(mRead).handle(data);
    }

    @Test
    public void longChecksum() throws IOException {
        byte[] longSum     = "abcdefghijklmnop".getBytes();
        byte[] data        = "longSum".getBytes();
        InputStream stream = input(testHeader, data, longSum, testFooter);
        PacketReader mRead = mock(PacketReader.class);
        Checksum checker   = mock(Checksum.class);
        Decoder decoder    = new Decoder(stream, testHeader, testFooter, checker);

        when(checker.length()).thenReturn(longSum.length);
        when(checker.calc(any(byte[].class))).thenReturn(longSum);
        decoder.addPacketReader(mRead);
        when(mRead.claim(data[0])).thenReturn(data.length);
        decoder.update();

        verify(mRead).handle(data);
    }


    @Test
    public void badPacketBefore() throws IOException {
        byte[] data = "I'mTheGoodData".getBytes();
        ByteArrayOutputStream concat = new ByteArrayOutputStream();
        concat.write(testHeader);
        concat.write("badData".getBytes());
        concat.write(testHeader);
        concat.write(data);
        concat.write(testChecksum);
        concat.write(testFooter);
        InputStream stream = new ByteArrayInputStream(concat.toByteArray());

        PacketReader mRead = mock(PacketReader.class);
        Checksum checker   = mock(Checksum.class);
        Decoder decoder    = testDecoder(stream, mRead);

        when(checker.length()).thenReturn(testChecksum.length);
        when(checker.calc(data)).thenReturn(testChecksum);
        when(mRead.claim(data[0])).thenReturn(data.length);
        decoder.update();

        verify(mRead).handle(data);
    }

    @Test
    public void longGargbageStringBefore() throws IOException {
        byte[] data = "Packet".getBytes();
        ByteArrayOutputStream concat = new ByteArrayOutputStream();
        concat.write(randomData(256));
        concat.write(testHeader);
        concat.write(data);
        concat.write(testChecksum);
        concat.write(testFooter);
        PacketReader mRead = mock(PacketReader.class);
        Decoder decoder    = testDecoder(new ByteArrayInputStream(concat.toByteArray()), mRead);

        when(mRead.claim(anyByte())).thenReturn(data.length);
        decoder.update();

        verify(mRead).handle(data);
    }

    @Test
    public void multipleMatch() throws IOException {
        byte[] data         = "I'mTheGoodData".getBytes();
        ByteArrayOutputStream concat = new ByteArrayOutputStream();
        for(int i=0; i<30; i++){
            concat.write(testHeader);
            concat.write(data);
            concat.write(testChecksum);
            concat.write(testFooter);
        }
        InputStream stream = new ByteArrayInputStream(concat.toByteArray());
        PacketReader mRead  = mock(PacketReader.class);
        Decoder decoder     = testDecoder(stream, mRead);

        when(mRead.claim(data[0])).thenReturn(data.length);
        decoder.update();

        verify(mRead, times(30)).handle(data);
    }

    @Test
    public void multiplePacketTypes() throws IOException {
        byte[][] data = {
            "firstPacket".getBytes(),
            "secondPacket".getBytes()
        };
        ByteArrayOutputStream concat = new ByteArrayOutputStream();
        for(int i=0; i<2; i++){
            concat.write(testHeader);
            concat.write(data[i]);
            concat.write(testChecksum);
            concat.write(testFooter);
        }
        InputStream stream = new ByteArrayInputStream(concat.toByteArray());
        PacketReader aRead = mock(PacketReader.class);
        PacketReader bRead = mock(PacketReader.class);
        Decoder decoder    = testDecoder(stream, aRead);

        decoder.addPacketReader(bRead);
        when(aRead.claim(any(byte.class))).thenReturn(-1);
        when(bRead.claim(any(byte.class))).thenReturn(-1);
        when(aRead.claim(data[0][0])).thenReturn(data[0].length);
        when(bRead.claim(data[1][0])).thenReturn(data[1].length);
        decoder.update();

        verify(bRead).handle(data[1]);
        verify(aRead).handle(data[0]);
    }

    @Test
    public void decoyHeaderInData() throws IOException {
        byte[] data        = testHeader;
        PacketReader mRead = mock(PacketReader.class);
        Decoder decoder    = testDecoder(data, mRead);

        when(mRead.claim(data[0])).thenReturn(data.length);
        decoder.update();

        verify(mRead).handle(any(byte[].class));
    }

    @Test
    public void decoyFooterInData() throws IOException {
        byte[] data        = testFooter;
        PacketReader mRead = mock(PacketReader.class);
        Decoder decoder    = testDecoder(data, mRead);

        when(mRead.claim(data[0])).thenReturn(data.length);
        decoder.update();

        verify(mRead).handle(any(byte[].class));
    }

    @Test
    public void dontClaimMessage() throws IOException {
        byte[] data        = "Not Claimed".getBytes();
        InputStream stream = input(testHeader, data, new byte[0], testFooter);
        PacketReader mRead = mock(PacketReader.class);
        Decoder decoder    = testDecoder(stream, mRead);

        when(mRead.claim(any(byte.class))).thenReturn(-1);
        decoder.update();

        verify(mRead, never()).handle(any(byte[].class));
    }
    @Test
    public void badChecksum() throws IOException {
        byte[] data        = "noChecksum".getBytes();
        InputStream stream = input(testHeader, data, new byte[0], testFooter);
        PacketReader mRead = mock(PacketReader.class);
        Decoder decoder    = testDecoder(stream, mRead);

        when(mRead.claim(data[0])).thenReturn(data.length);
        decoder.update();

        verify(mRead, never()).handle(any(byte[].class));
    }

    @Test
    public void packetTooLong() throws IOException {
        byte[] data        = "I'mTooLong".getBytes();
        PacketReader mRead = mock(PacketReader.class);
        Decoder decoder    = testDecoder(data, mRead);

        when(mRead.claim(data[0])).thenReturn(data.length-1);
        decoder.update();

        verify(mRead, never()).handle(any(byte[].class));
    }

    @Test
    public void removedPacketReader() throws IOException {
        byte[] data        = "NobodyReadsMe".getBytes();
        PacketReader mRead = mock(PacketReader.class);
        Decoder decoder    = testDecoder(data, mRead);

        when(mRead.claim(data[0])).thenReturn(data.length);
        decoder.removePacketReader(mRead);
        decoder.update();

        verify(mRead, never()).handle(any(byte[].class));
    }
}
