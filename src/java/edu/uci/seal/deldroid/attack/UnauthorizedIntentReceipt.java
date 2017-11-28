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

public class UnauthorizedIntentReceipt {
    /*
    Unauthorized Intent receipt is an ICC attack in which a malicious component intercepts an implicit Intent by declaring an Intent Filter that matches the sent Intent.
    The sender component is the victim component
    The receiver component is the malicious component
    */
    private Component vulComponent; //sender component
    private Component malComponent; //receiver compoennt
    private Component potComponent;   // a potential component that vulComponent wants to send an Intent to.
    private boolean iac; //inter app communication
    
    public UnauthorizedIntentReceipt(Component s, Component r, Component x){
        this.vulComponent = s;
        this.malComponent = r;
        this.potComponent = x;    
        this.iac = !(this.malComponent.getPackageName().equals(this.vulComponent.getPackageName()));
    }
    @Override
    public boolean equals(Object o){
        if (!(o instanceof UnauthorizedIntentReceipt))
            return false;
        
        UnauthorizedIntentReceipt that = (UnauthorizedIntentReceipt) o;
        if (this.vulComponent.equals(that.vulComponent) && this.malComponent.equals(that.malComponent) && this.potComponent==that.potComponent){
            return true;
        }        
        return false;        
    }
    @Override
    public String toString(){
        String msg = this.vulComponent.getFullName()+" -> "+this.malComponent.getFullName()+"(X:"+this.potComponent.getFullName()+")\n" ;
        return msg;
    }

    @XmlTransient
    public Component getVulComponent() {
        return vulComponent;
    }

    public void setVulComponent(Component vulComponent) {
        this.vulComponent = vulComponent;
    }
    @XmlTransient
    public Component getMalComponent() {
        return malComponent;
    }

    public void setMalComponent(Component malComponent) {
        this.malComponent = malComponent;
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
