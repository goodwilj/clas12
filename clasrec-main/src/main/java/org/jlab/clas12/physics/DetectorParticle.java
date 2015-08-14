/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.physics;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import org.jlab.clas.detector.DetectorType;
import org.jlab.clas.physics.Vector3;
import org.jlab.geom.prim.Line3D;
import org.jlab.geom.prim.Point3D;

/**
 *
 * @author gavalian
 */
public class DetectorParticle {
    
    private Vector3 particleMomenta = new Vector3();
    private Vector3 particleVertex  = new Vector3();
    private Integer particleCharge  = 0;
    private Integer particlePID     = 0;
    private Integer particleStatus  = 1;
    private Double  particleBeta    = 0.0;
    private Double  particleMass    = 0.0;
    private Double  particlePath    = 0.0;
    private Vector3 particleCrossPosition  = new Vector3();
    private Vector3 particleCrossDirection = new Vector3();
    
    private List<DetectorResponse>  responseStore = new ArrayList<DetectorResponse>();
    private TreeMap<DetectorType,Vector3>  projectedHit = 
            new  TreeMap<DetectorType,Vector3>();
    
    public DetectorParticle(){
        
    }
    
    public void clear(){
        this.responseStore.clear();
    }
    
    public void addResponse(DetectorResponse res){
        double distance = Math.sqrt(
                (this.particleCrossPosition.x()-res.getPosition().x())*
                        (this.particleCrossPosition.x()-res.getPosition().x())
                +
                        (this.particleCrossPosition.y()-res.getPosition().y())*
                                (this.particleCrossPosition.y()-res.getPosition().y())
                +
                        (this.particleCrossPosition.z()-res.getPosition().z())*
                                (this.particleCrossPosition.z()-res.getPosition().z())
        );
        
        Line3D   crossLine = new Line3D(this.particleCrossPosition.x(),
                this.particleCrossPosition.y(),
                this.particleCrossPosition.z(),
                this.particleCrossDirection.x()*1500.0,
                this.particleCrossDirection.y()*1500.0,
                this.particleCrossDirection.z()*1500.0);
        
        Line3D distanceLine = crossLine.distance(new Point3D
                (res.getPosition().x(), res.getPosition().y(),res.getPosition().z()));
        
        res.getMatchedPosition().setXYZ(
                distanceLine.origin().x(),
                distanceLine.origin().y(),
                distanceLine.origin().z()
                );
        
        res.setPath(distance+this.particlePath);
        this.responseStore.add(res);
    }
    
    
    public boolean hasHit(DetectorType type){
        int hits = 0;
        for( DetectorResponse res : this.responseStore){
            if(res.getDescriptor().getType()==type) hits++;
        }
        if(hits==0) return false;
        if(hits>1) System.out.println("[Warning] Too many hits for detector type = " + type);
        return true;
    }
    
    public DetectorResponse getHit(DetectorType type){
        for(DetectorResponse res : this.responseStore){
            if(res.getDescriptor().getType()==type) return res;
        }
        return null;
    }
    
    public double getBeta(){ return this.particleBeta;}
    public int    getStatus(){ return this.particleStatus;}
    public double getMass(){ return this.particleMass;}
    public int    getPid(){ return this.particlePID;}
    public Vector3  vector(){return this.particleMomenta;}
    
    public Vector3  vertex(){return this.particleVertex;}
    
    public Vector3  getCross(){ return this.particleCrossPosition;}
    
    public Vector3  getCrossDir(){ return this.particleCrossDirection;}
    
    public double   getPathLength(){ return this.particlePath;}
    public int      getCharge(){ return this.particleCharge;}
    
    public double   getPathLength(DetectorType type){
        DetectorResponse response = this.getHit(type);
        if(response==null) return -1.0;
        return this.getPathLength(response.getPosition());
    }
    
    
    
    public double   getPathLength(Vector3 vec){
        return this.getPathLength(vec.x(), vec.y(), vec.z());
    }
    
    public double   getPathLength(double x, double y, double z){
        double crosspath = Math.sqrt(
                (this.particleCrossPosition.x()-x)*(this.particleCrossPosition.x()-x)
                        + (this.particleCrossPosition.y()-y)*(this.particleCrossPosition.y()-y)
                        + (this.particleCrossPosition.z()-z)*(this.particleCrossPosition.z()-z)
        );
        return this.particlePath + crosspath;
    }
    
    public double getTime(DetectorType type){
        DetectorResponse response = this.getHit(type);
        if(response==null) return -1.0;
        return response.getTime();
    }
    
    public double getEnergy(DetectorType type){
        DetectorResponse response = this.getHit(type);
        if(response==null) return -1.0;
        return response.getEnergy();
    }
    
    public double getBeta(DetectorType type){
        DetectorResponse response = this.getHit(type);
        if(response==null) return -1.0;
        double cpath = this.getPathLength(response.getPosition());
        double ctime = response.getTime();
        double beta  = cpath/ctime/30.0;
        return beta;
    }
    
    public double getMass(DetectorType type){
        double mass2 = this.getMass2(type);
        if(mass2<0) return Math.sqrt(-mass2);
        return Math.sqrt(mass2);
    }
    
    public double getMass2(DetectorType type){
        double beta   = this.getBeta(type);
        double energy = this.getEnergy(type);
        double mass2  = this.particleMomenta.mag2()/(beta*beta) - this.particleMomenta.mag2();
        return mass2;
    }
    
    public void setStatus(int status){this.particleStatus = status;}
    public void setBeta(double beta){ this.particleBeta = beta;}
    public void setMass(double mass){ this.particleMass = mass;}
    public void setPid(int pid){this.particlePID = pid;}
    public void setCharge(int charge) { this.particleCharge = charge;}
    
    public void setCross(double x, double y, double z,
            double ux, double uy, double uz){
        this.particleCrossPosition.setXYZ(x, y, z);
        this.particleCrossDirection.setXYZ(ux, uy, uz);
    }
    
    public int getDetectorHit(List<DetectorResponse>  hitList, DetectorType type,
            int detectorLayer,
            double distanceThreshold){
        
        Line3D   trajectory = new Line3D(
                this.particleCrossPosition.x(),
                this.particleCrossPosition.y(),
                this.particleCrossPosition.z(),
                this.particleCrossDirection.x()*1500.0,
                this.particleCrossDirection.y()*1500.0,
                this.particleCrossDirection.z()*1500.0
        );
        
        Point3D  hitPoint = new Point3D();
        double   minimumDistance = 500.0;
        int      bestIndex       = -1;
        for(int loop = 0; loop < hitList.size(); loop++){
            //for(DetectorResponse response : hitList){
            DetectorResponse response = hitList.get(loop);
            if(response.getDescriptor().getType()==type&&
                    response.getDescriptor().getLayer()==detectorLayer){
                hitPoint.set(
                        response.getPosition().x(),
                        response.getPosition().y(),
                        response.getPosition().z()
                        );
                double hitdistance = trajectory.distance(hitPoint).length();
                //System.out.println(" LOOP = " + loop + "   distance = " + hitdistance);
                if(hitdistance<distanceThreshold&&hitdistance<minimumDistance){
                    minimumDistance = hitdistance;
                    bestIndex       = loop;
                }
            }
        }
        return bestIndex;
    }
    
    public void setPath(double path){
        this.particlePath = path;
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append(String.format("status = %4d  charge = %3d [pid/beta/mass] %5d %8.4f %8.4f",                 
                this.particleStatus,
                this.particleCharge,
                this.particlePID,
                this.particleBeta,this.particleMass));
        str.append(String.format("  P [ %8.4f %8.4f %8.4f ]  V [ %8.4f %8.4f %8.4f ] ",
                this.particleMomenta.x(),this.particleMomenta.y(),
                this.particleMomenta.z(),
                this.particleVertex.x(),this.particleVertex.y(),
                this.particleVertex.z()));
        str.append("\n");
        for(DetectorResponse res : this.responseStore){
            str.append(res.toString());
            str.append("\n");
        }
        
        return str.toString();
    }
}
