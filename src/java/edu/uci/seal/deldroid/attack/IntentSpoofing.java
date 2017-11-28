/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uci.seal.deldroid.attack;

import edu.uci.seal.deldroid.model.Component;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Mahmoud
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"malApp","malComp", "malCompId", "malCompDsmIdx", "vulApp", "vulComp", "vulCompId", "vulCompDsmIdx", "potApp", "potComp", "potCompId", "potCompDsmIdx"})

public class IntentSpoofing {
    
    private Component malComponent; //sender component which is the malicious component
    private Component vulComponent; //receiver compoennt which is the vulnerable component
    private Component potComponent;
    private boolean iac;
    
    public IntentSpoofing(Component s, Component r, Component x){
        /*
        Intent spoong is an ICC attack in which a malicious component can communicate with an exported 365 component that is not expecting a communication from it
        */
        this.malComponent = s;
        this.vulComponent = r;
        this.potComponent = x;        
        this.iac = !(this.malComponent.getPackageName().equals(this.vulComponent.getPackageName()));
    }
    @Override
    public boolean equals(Object o){
        if (!(o instanceof IntentSpoofing))
            return false;
        
        IntentSpoofing that = (IntentSpoofing) o;
        if (this.malComponent.equals(that.malComponent) && this.vulComponent.equals(that.vulComponent) && this.potComponent==that.potComponent){
            return true;
        }        
        return false;        
    }
    @Override
    public String toString(){
        String msg = this.malComponent.getFullName()+" -> "+this.vulComponent.getFullName()+"(X:"+this.potComponent.getFullName()+")\n" ;
        return msg;
 
    }

    @XmlTransient
    public Component getMalComponent() {
        return malComponent;
    }

    public void setMalComponent(Component malComponent) {
        this.malComponent = malComponent;
    }

    @XmlTransient
    public Component getVulComponent() {
        return vulComponent;
    }

    public void setVulComponent(Component vulComponent) {
        this.vulComponent = vulComponent;
    }
    
    @XmlTransient
    public Component getPotComponent() {
        return potComponent;
    }

    public void setPotComponent(Component potComponent) {
        this.potComponent = potComponent;
    }
    
    @XmlElement(name = "malApp")
    public String getMalApp(){
        return this.malComponent.getPackageName();
    }
    @XmlElement(name = "malComp")
    public String getMalComp(){
        return this.malComponent.getFullName();
    }
    @XmlElement(name = "malCompId")
    public int getMalCompId(){
        return this.malComponent.getComponentId();
    }
    @XmlElement(name = "malCompDsmIdx")
    public int getMalCompDsmIdx(){
        return this.malComponent.getDsmIdx();
    }
    @XmlElement(name = "vulApp")
    public String getVulApp(){
        return this.vulComponent.getPackageName();
    }
    @XmlElement(name = "vulComp")
    public String getVulComp(){
        return this.vulComponent.getFullName();
    }
    @XmlElement(name = "vulCompId")
    public int getVulCompId(){
        return this.vulComponent.getComponentId();
    }
    @XmlElement(name = "vulCompDsmIdx")
    public int getVulCompDsmIdx(){
        return this.vulComponent.getDsmIdx();
    }
    @XmlElement(name = "potApp")
    public String getPotApp(){
        return this.potComponent.getPackageName();
    }
    @XmlElement(name = "potComp")
    public String getPotComp(){
        return this.potComponent.getFullName();
    }
    @XmlElement(name = "potCompId")
    public int getPotCompId(){
        return this.potComponent.getComponentId();
    }
    @XmlElement(name = "potCompDsmIdx")
    public int getPotCompDsmIdx(){
        return this.potComponent.getDsmIdx();
    }

    @XmlTransient
    public boolean isIac() {
        return iac;
    }

    public void setIac(boolean iac) {
        this.iac = iac;
    }
    
    
}
