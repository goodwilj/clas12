/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.root.func;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import org.root.base.EvioWritableTree;
import org.root.histogram.PaveText;

/**
 *
 * @author gavalian
 */
public class F1D extends Function1D implements EvioWritableTree {
    
    private final ArrayList<String> functionList = new ArrayList<String>();
    private String  functionString = "";
    private String functionName = "f1";
    
    public F1D(String function){
        this.initFunction(function,0.0,1.0);
    }
    
    public F1D(String function,double min, double max){
        this.initFunction(function,min,max);
    }
    
    public final void initFunction(String function, double min, double max){
        this.functionString = function;
        String[] funcs = function.split("[+]");
        this.setFunction(function);
        this.setRange(min, max);
        functionList.clear();
        functionList.addAll(Arrays.asList(funcs));
        this.initParameters();
    }
    

    public void setParameter(int par, double value){
        this.parameter(par).setValue(value);
    }
    
    public void setParLimits(int par, double min, double max){
        this.parameter(par).setLimits(min, max);
    }
    
    public double getParameter(int par){
        return this.parameter(par).value();
    }
    
    private void initParameters(){
        
        ArrayList<String> pars = new ArrayList<String>();
        
        for(String f : functionList){
            if(f.compareTo("gaus")==0){
                pars.add("amp");
                pars.add("mean");
                pars.add("sigma");
            }
            
            if(f.compareTo("landau")==0){
                pars.add("amp");
                pars.add("mean");
                pars.add("sigma");
            }
            
            if(f.compareTo("exp")==0){
                pars.add("p0");
                pars.add("p1");
            }
            
            if(f.compareTo("p0")==0){
                pars.add("p0");
            }
            
            if(f.compareTo("p1")==0){
                pars.add("p0");
                pars.add("p1");
            }
            
            if(f.compareTo("p2")==0){
                pars.add("p0");
                pars.add("p1");
                pars.add("p2");
            }
            
            if(f.compareTo("p3")==0){
                pars.add("p0");
                pars.add("p1");
                pars.add("p2");
                pars.add("p3");
            }
            
        }
        
        this.setNParams(pars.size());
        
        for(int loop = 0; loop < pars.size(); loop++){
            this.parameter(loop).setName(pars.get(loop));
        }
    }
    
    public PaveText getStat(){
        PaveText pave = new PaveText(0.02,0.98);
        int npars = this.getNParams();
        for(int loop = 0; loop < npars; loop++){
            String paramString = String.format("%-8s %8.3f", 
                    this.parameter(loop).name(),
                    this.parameter(loop).value()
                    );
            pave.addText(paramString);
        }
        return pave;
    }
    
    @Override
    public double eval(double x){
        ArrayList<Double>  values = new ArrayList<Double>();
        int parOffset = 0;
        for(String f : functionList){
            if(f.compareTo("gaus")==0){
                values.add(this.parameter(parOffset).value()*FunctionFactory.gauss(x,
                        this.parameter(parOffset+1).value(),
                        this.parameter(parOffset+2).value()));
                parOffset += 3;
            }
            
            if(f.compareTo("landau")==0){
                values.add(this.parameter(parOffset).value()*FunctionFactory.landau(x,
                        this.parameter(parOffset+1).value(),
                        this.parameter(parOffset+2).value()));
                parOffset += 3;
            }
            
            if(f.compareTo("exp")==0){
                values.add(this.parameter(parOffset).value()*
                        Math.exp(x*this.parameter(parOffset+1).value()));
                //FunctionFactory.landau(x,
                //        this.parameter(parOffset+1).value(),
                //        this.parameter(parOffset+2).value()));
                parOffset += 2;
            }
            
            if(f.compareTo("p0")==0){
                values.add(this.parameter(parOffset).value()); 
                parOffset += 1;
            }
            
            if(f.compareTo("p1")==0){
                values.add(this.parameter(parOffset).value()+
                        this.parameter(parOffset+1).value()*x);
                    parOffset += 2;
            }
            
            if(f.compareTo("p2")==0){
                values.add(this.parameter(parOffset).value()+
                        this.parameter(parOffset+1).value()*x +
                        this.parameter(parOffset+2).value()*x*x
                );
                    parOffset += 3;
            }
            
            if(f.compareTo("p3")==0){
                values.add(this.parameter(parOffset).value()+
                        this.parameter(parOffset+1).value()*x +
                        this.parameter(parOffset+2).value()*x*x +
                        this.parameter(parOffset+2).value()*x*x*x 
                );
                    parOffset += 4;
            }
        }
        
        double result = 0.0;
        for(Double val : values){
            result += val;
        }
        return result;
    }

    public TreeMap<Integer, Object> toTreeMap() {
        TreeMap<Integer, Object> hcontainer = new TreeMap<Integer, Object>();
        hcontainer.put(1, new int[]{7});
        hcontainer.put(2, new int[]{1});
        return hcontainer;
    }

    public void fromTreeMap(TreeMap<Integer, Object> map) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setName(String name){
        this.functionName = name;
    }

    public String getName() {
        return this.functionName;
    }
}
