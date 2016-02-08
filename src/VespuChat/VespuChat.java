package com.VespuChat;

import java.io.ByteArrayOutputStream;
import com.serial.Checksum;

class VespuChat{
    static final byte[]  HEADER = new byte[]{ (byte)'H' };
    static final byte[]  FOOTER = new byte[]{ (byte)'F' };
    static final Checksum CHECK = new Checksum(){
        public int length() { return 1; }
        public byte[] calc(byte[] txt){ return VespuChat.checksum(txt); }
    };

    static byte[] checksum(byte[] txt) {
        byte[] sum = new byte[]{ (byte)txt.length };
        for(byte b : txt) sum[0] ^= b;
        return sum;
    }

    /**
     * Wrap a (Type Data) block in header, checksum, and footer
     * @param  content The message to be wrapped
     * @return         The wrapped message, ready to be transmitted
     */
    static byte[] wrap(byte[] content) {
        try (ByteArrayOutputStream message = new ByteArrayOutputStream()) {
            message.write(HEADER);
            message.write(content);
            message.write(checksum(content));
            message.write(FOOTER);
            return message.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Can't generate message array");
    }
}
