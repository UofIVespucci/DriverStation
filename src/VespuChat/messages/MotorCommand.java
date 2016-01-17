package com.VespuChat.messages;

class MotorCommand implements PacketReader {
    public int claim(byte data){
        return (data == ${sig}) ${length} : -1;
    }
    public void handle(byte[] data){
        ByteBuffer buf = ByteBuffer.wrap(msg);
        byte  sig   = buf.get();
        short left  = buf.getShort();
        short right = buf.getShort();
        /*handler code*/
    }
    public static byte[] build(short left, short right){
        ByteBuffer buf = ByteBuffer.allocate(5);
        buf.put(VespuChat.MOTOR_COMMAND);
        buf.putShort(left);
        buf.putShort(right);
        buf.array();
    }
}

class MotorCommand : public Receiver {
public:
    int claim(char data) {
        return (data == ${sig}) ${length} : -1;
    }
    void handle(const char* buf, int len){
        ${getfrom("buf",0)}
    }
    static byte* build(args...){
        byte *buf = malloc(${length});
        ${writeto("buf",0)}
        return buf;
    }
}

public void handle(byte[] data){
    ByteBuffer buf = ByteBuffer.wrap(data);
    byte  sig = buf.get();
    ${readbuf(fields)}
    ${handler}
}
public byte[] build(${argline(fields)}){
    ByteBuffer buf = ByteBuffer.allocate(${length});
    buf.put(VespuChat.MOTOR_COMMAND);
    ${buildbuf(fields)}
    buf.array();
}

argline
buildbuf
readbuf

Float,Int32,Int16,Uint8

<message name="MotorCommand">
    <value name="left", type="int16"/>
    <value name="right", type="int16"/>
</message>
<message name="AccelData">
    <value name="xgs", type="float"/>
    <value name="ygs", type="float"/>
    <value name="zgs", type="float"/>
</message>

