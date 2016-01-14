package com.serial;

import java.util.*;
import java.io.InputStream;

/**
 * Decodes packets of the form <header+><signifier>data<checksum+><footer+>
 */
public class Decoder{
    private final InputStream input;
    private final byte[] header;
    private final byte[] footer;
    private final Checksum sum;
    private final List<PacketReader> readers = new LinkedList<PacketReader>();

    private static class Packet{
        int startPos;
        int maxLength;
        PacketReader claimee;
        Packet(int sPos, int mLen, PacketReader claimer) {
            startPos = sPos;
            maxLength = mLen;
            claimee = claimer;
        }
    }
    private List<Packet> foundHeaders = new ArrayList<Packet>();
    private List<Byte>   buffer = new ArrayList<Byte>();

    public Decoder(InputStream input, byte[] head, byte[] tail, Checksum sum){
        this.input  = input;
        this.header = head;
        this.footer = tail;
        this.sum    = sum;
    }
    /**
     * Read new bytes from the input stream, dispatching messages as it goes
     */
    public void update(){
        //accept bytes from input
        int oldPos = buffer.size()-1;
        while(true){
            int data;
            try{
                data = input.read();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
            if(data == -1) break;
            buffer.add((byte)data);
        }
        parse(oldPos);
    }

    private void parse(int oldPos){
        //because the header includes one byte beyond the "header" buffer,
        //it isn't matched until the header was matched one byte ago
        for(int i=oldPos+1; i<buffer.size(); i++){
            if (match(header, i-1)) foundHeader(i);
            if (match(footer, i  )) foundFooter(i);
        }
        cleanBuffers();
    }

    //check if pattern appears in buffer, ending at searchPos
    private boolean match(byte[] pattern, int searchPos){
        if(searchPos < pattern.length-1) return false;
        for(int i=0; i<pattern.length; i++){
            if(buffer.get(searchPos-i) != pattern[pattern.length-1-i]) return false;
        }
        return true;
    }

    //see if any reader claims the coming packet
    //if so, add to the header list
    private void foundHeader(int pos){
        byte sig = buffer.get(pos);
        for(PacketReader r : readers) {
            int len = r.claim(sig);
            if(len != -1) {
                foundHeaders.add(new Packet(pos, len, r));
                return;
            }
        }
    }

    //check for valid checksum with possible headers
    //then send to handler on a match
    private void foundFooter(int pos){
        int footerPos   = pos - (footer.length);
        int checksumPos = footerPos - (sum.length()-1);
        //try and match the footer and checksum to a previous header
        for(int i=0; i < foundHeaders.size(); i++){
            Packet p = foundHeaders.get(i);
            int packLen = checksumPos-p.startPos;
            if(packLen < 0) continue; //packet doesn't have both type byte and checksum
            if(packLen > p.maxLength) continue; //packet exceeds max length specified

            byte[] data = new byte[packLen];
            for(int b=0; b<packLen; b++) data[b] = buffer.get(p.startPos+b);
            //calculate and match checksum
            byte[] checksum = sum.calc(data);
            if(match(checksum, footerPos)){
                //remove older headers and call handler
                foundHeaders.subList(0, i+1).clear();
                p.claimee.handle(data);
                return;
            }
        }
    }

    private void cleanBuffers(){
        int removed = Math.max(buffer.size() - header.length, 0);
        for(Iterator<Packet> iter = foundHeaders.listIterator(); iter.hasNext();) {
            Packet p = iter.next();
            if(buffer.size() - p.startPos > p.maxLength){
                iter.remove(); //remove packets past their max length
            } else {
                removed = Math.min(removed, p.startPos);
            }
        }
        buffer.subList(0, removed).clear();
        for(Packet p : foundHeaders){
            p.startPos -= removed;
        }
    }

    public void close(){
        try{
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addPacketReader(PacketReader reader){
        readers.add(reader);
    }

    public void removePacketReader(PacketReader reader){
        readers.remove(reader);
    }
}
