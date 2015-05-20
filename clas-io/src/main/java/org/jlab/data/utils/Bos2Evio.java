/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.data.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;
import org.jlab.bos.clas6.BosDataEvent;
import org.jlab.bos.clas6.BosDataSource;
import org.jlab.evio.clas12.EvioDataBank;
import org.jlab.evio.clas12.EvioDataEvent;
import org.jlab.evio.clas12.EvioDataSync;
import org.jlab.evio.clas12.EvioFactory;

/**
 *
 * @author gavalian
 */
public class Bos2Evio {
    
    public static void printUsage(){
        System.out.println("\n\t Usage: bos2evio [flag] outputfile.evio input1.bos [input2.bos] .... ");
        System.out.println("\n flags:");
        System.out.println("\t -seb : SEB bank output (EVNT, ECPB, SCPB, CCPB, TGPB)");
        System.out.println("\t -a1c : a1c bank output (PART, TBID, TBER)");
        System.out.println("\n\n");
    }
    
    public static void main(String[] args){
        
        if(args.length<3){
            Bos2Evio.printUsage();
            System.exit(0);
        }
        
        EvioFactory.getDictionary().getDescriptor("HEADER::info").show();
        EvioFactory.getDictionary().getDescriptor("PART::particle").show();
        
        String method = args[0];
        
        if(method.compareTo("-seb")==0||method.compareTo("-a1c")==0){
            
        } else {
            System.out.println("\n\n ERROR: unknown flag " + method);
            Bos2Evio.printUsage();
            System.exit(0);
        }
        
        String output = args[1];
        
        File file = new File(output);
        if(file.exists()==true){
            System.out.println("\n\n ERROR : output file " + output + 
                    "  already exists. Can not override.\n\n");
            System.exit(0);
        }
        
        ArrayList<String> inputfiles = new ArrayList<String>();
        for(int loop = 2; loop < args.length; loop++){
            inputfiles.add(args[loop]);
        }
        
        Bos2EvioPartBank   convertPART = new Bos2EvioPartBank();
        Bos2EvioEventBank  convertEVNT = new Bos2EvioEventBank();
        EvioDataSync  writer = new EvioDataSync();
        writer.open(output);
        
        for(String inFile : inputfiles){
            BosDataSource reader = new BosDataSource();                      
            reader.open(inFile);
            while(reader.hasEvent()){
                BosDataEvent bosEvent = (BosDataEvent) reader.getNextEvent();
                //convertPART.processBosEvent(bosEvent);
                EvioDataEvent outevent = writer.createEvent(EvioFactory.getDictionary());
                /*
                for(Map.Entry<String,EvioDataBank> banks : convertPART.bankStore().entrySet()){
                    outevent.appendBank(banks.getValue());
                }*/
                //=============================================================
                //*** FILLING WITH EVNT Schema
                //=============================================================
                if(method.compareTo("-seb")==0){
                    convertEVNT.processBosEvent(bosEvent);
                    TreeMap<String,EvioDataBank> evioBanks = convertEVNT.getEvioBankStore();
                    if(evioBanks.containsKey("HEVT")==true&&evioBanks.containsKey("EVNT")==true){
                        
                        
                        outevent.appendBank(evioBanks.get("HEVT"));
                        outevent.appendBank(evioBanks.get("EVNT"));
                        
                        if(evioBanks.containsKey("TAGR")==true){
                            outevent.appendBank(evioBanks.get("TAGR"));
                        }
                        ArrayList<EvioDataBank>  detectorBanks = new ArrayList<EvioDataBank>();
                        if(evioBanks.containsKey("ECPB")==true){
                            //outevent.appendBank(evioBanks.get("ECPB"));
                            detectorBanks.add(evioBanks.get("ECPB"));
                        }
                        if(evioBanks.containsKey("LCPB")==true){
                            //outevent.appendBank(evioBanks.get("ECPB"));
                            detectorBanks.add(evioBanks.get("LCPB"));
                        }
                        if(evioBanks.containsKey("SCPB")==true){
                            //outevent.appendBank(evioBanks.get("ECPB"));
                            detectorBanks.add(evioBanks.get("SCPB"));
                        }
                        if(evioBanks.containsKey("CCPB")==true){
                            //outevent.appendBank(evioBanks.get("ECPB"));
                            detectorBanks.add(evioBanks.get("CCPB"));
                        }
                        if(detectorBanks.size()>0){
                            EvioDataBank[] pb = new EvioDataBank[detectorBanks.size()];
                            for(int loop = 0; loop < detectorBanks.size(); loop++){
                                pb[loop] = detectorBanks.get(loop);
                            }
                            outevent.appendBanks(pb);
                        }
                        writer.writeEvent(outevent);
                    }
                }
                //=============================================================
                //*** END OF EVNT Method Fill
                //=============================================================
            }
            reader.close();
        }
        
        writer.close();
    }
    
}
