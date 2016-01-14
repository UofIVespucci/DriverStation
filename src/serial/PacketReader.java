package com.serial;

public interface PacketReader{
    /**
     * Give this message reader a chance to claim a particular packet
     *     data is the first byte of a packet's data section
     *     should return -1 if the packet is not claimed,
     *       or the maximum packet length if claimed
     */
    public int claim(byte data);
    /**
     * When a claimed packet has finished transmitting, it will be passed
     *   to handle
     */
    public void handle(byte[] data);
}
