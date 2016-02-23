/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.bio;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class BioRecordIndex {
    
    long  recordPosition = (long) 0; // position of the record in the file (bytes)
    int   recordLength   =        0; // length of the record in bytes
    boolean recordCompression  = false; // indicates if the record is compressed
    
    /**
     * This list contains the pointers to the each event inside of the record
     * relative to the record position in the file, and does not include record header
     * length.
     */
    List<Integer>  eventOffsets = new ArrayList<Integer>();
    
    /**
     * Constructor for creating a record index with given position in the file.
     * @param offset offset relative to the start of the file.
     */
    public BioRecordIndex(long offset){
        this.recordPosition = offset;
    }
    
    /**
     * returns the array containing the indecies of the events in the record.
     * this array should not be used directly. the index offsets are only
     * valid after the record was decompressed (in case of compression). 
     * @return 
     */
    public List<Integer>  getEventIdex(){
        return this.eventOffsets;
    }
    
    /**
     * Returns the position of the given event in the record, relative to the start of
     * the file. the record header size and record offset already added to the event offset.
     * @param index event order in the record
     * @return 
     */
    public long getEventPosition(int index){
        return (long) (recordPosition + this.eventOffsets.get(index) + BioHeaderConstants.RECORD_HEADER_SIZE);
    }
    
    /**
     * returns record position relative to the file start.
     * @return 
     */
    public long getPosition(){
        return this.recordPosition;
    }
    /**
     * Set the compression to the boolean value passed.
     * @param isc flag indicating weather the record is compressed.
     */
    public void setCompression(boolean isc){
        this.recordCompression = isc;
    }
    /**
     * returns boolean flag compression. 
     * @return 
     */
    public boolean getCompression(){
        return this.recordCompression;
    }
    /**
     * Set record length used for reading entire record in. Length includes
     * header length, index array length and data length.
     * @param len length in bytes for the record
     */
    public void setLength(int len){
        this.recordLength = len;
    }
    /**
     * returns the length of the record,  Length includes
     * header length, index array length and data length.
     * @return total length of the record
     */
    public int getLength(){
        return this.recordLength;
    }
    /**
     * returns string representation of the record index
     * @return 
     */
    @Override
    public String toString(){
        Boolean  compression = this.getCompression();
        StringBuilder str = new StringBuilder();
        str.append(String.format("[BREC] [ %6s ]  OFFSET = %16d :  LENGTH = %14d : NEVENTS = %14d ",  
                compression.toString(),
                this.getPosition(), this.getLength(),this.getEventIdex().size()));
        return str.toString();
    }
}
